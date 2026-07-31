/*
*
* Copyright (c) Lightstreamer Srl
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package com.lightstreamer.examples.stocklist_demo.adapters;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lightstreamer.examples.stocklist_demo.feed_simulator.ExternalFeedListener;
import com.lightstreamer.examples.stocklist_demo.feed_simulator.ExternalFeedSimulator;

import com.lightstreamer.adapter.common.InitException;
import com.lightstreamer.adapter.common.Mode;
import com.lightstreamer.adapter.data.DataAdapter;
import com.lightstreamer.adapter.data.ItemListener;
import com.lightstreamer.adapter.data.ItemManager;
import com.lightstreamer.adapter.data.SubscriptionException;

/**
 * This Data Adapter accepts a limited set of item names (the names starting
 * with "item") and listens to a (simulated) stock quotes feed, waiting for
 * update events. The events pertaining to the currently subscribed items
 * are then forwarded to Lightstreamer.
 * This example demonstrates how a Data Adapter could interoperate with
 * a broadcast feed (which always sends data for all available items)
 * by selecting the updates to be sent. Many other types of feeds may exist,
 * with very different behaviors.
 */
public class StockQuotesDataAdapter implements DataAdapter {

    /**
     * Private logger; a specific "LS_demos_Logger.StockQuotes" category
     * should be supplied by log4j configuration.
     */
    private Logger logger;

    private final HashMap<String,SubscriptionInfo> subscribedItems = new HashMap<String,SubscriptionInfo>();
    private final ExternalFeedSimulator myFeed;
    
    public StockQuotesDataAdapter() {
        myFeed = new ExternalFeedSimulator();
    }

    /**
     * Starts the simulator feed (or connects to the external
     * feed, for a real feed).
     */
    public void init(Map<String, String> params, File configDir) throws InitException {
        /*
        String logConfig = params.get("log_config");
        if (logConfig != null) {
            File logConfigFile = new File(configDir, logConfig);
            String logRefresh = params.get("log_config_refresh_seconds");
            if (logRefresh != null) {
                DOMConfigurator.configureAndWatch(logConfigFile.getAbsolutePath(), Integer.parseInt(logRefresh) * 1000);
            } else {
                DOMConfigurator.configure(logConfigFile.getAbsolutePath());
            }
        }*/
        logger = LogManager.getLogger("LS_demos_Logger.StockQuotes");
        
        myFeed.setFeedListener(new MyFeedListener());
        myFeed.start();
        logger.info("StockQuotesDataAdapter ready.");
    }

    public CompletionStage<ItemManager> getItemManager(String itemName)
            throws SubscriptionException {
        if (!itemName.startsWith("item")) {
            logger.error("Cannot subscribe to " + itemName + " - only names starting with \"item\" are supported");
            throw new SubscriptionException("Unexpected item: " + itemName);
        }
        return CompletableFuture.completedFuture(new StockItemManager(itemName));
    }


    public void shutdown() {
        myFeed.stop();
    }

    // never used in the demo, just showing the feature
    public void clearStatus() {
        synchronized (subscribedItems) {
            Set<String> keys = subscribedItems.keySet();
            for (String itemName : keys) {
                SubscriptionInfo si = subscribedItems.get(itemName);
                si.getItemListener().clearSnapshot();
            }
        }
    }
    
    private class StockItemManager implements ItemManager {
        private final String itemName;

        StockItemManager(String itemName) {
            this.itemName = itemName;
        }

        @Override
        public Mode getSupportedMode() {
            return Mode.MERGE;
        }

        @Override
        public CompletionStage<Void> subscribe(ItemListener listener) {
            logger.debug("Subscribing to " + itemName);
            synchronized (subscribedItems) {
                SubscriptionInfo si = new SubscriptionInfo(false, true, listener);
                subscribedItems.put(itemName, si);
            }
            // now we ask the feed for the snapshot; our feed will insert
            // an event with snapshot information into the normal updates flow
            myFeed.sendCurrentValues(itemName);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public boolean isSnapshotAvailable() {
            return true;
        }

        @Override
        public CompletionStage<Void> unsubscribe() {
            logger.debug("Unsubscribing from " + itemName);
            synchronized (subscribedItems) {
                SubscriptionInfo si = subscribedItems.remove(itemName);
                synchronized (si) {
                    si.stopSubscription();
                }
            }
            return CompletableFuture.completedFuture(null);
        }
    }

    private class MyFeedListener implements ExternalFeedListener {

        /**
         * Called by our feed for each update event occurrence on some stock.
         * If isSnapshot is true, then the event contains a full snapshot
         * with the current values of all fields for the stock.
         */
        
        public void onEvent(String itemName, final HashMap<String,String> currentValues,
                            boolean isSnapshot) {
            SubscriptionInfo si;
            
            synchronized (subscribedItems) {
                if (!subscribedItems.containsKey(itemName)) {
                    return;
                }
                si = subscribedItems.get(itemName);
            }
    
            synchronized (si) {
                boolean snapshotReceived = si.getSnapshotReceived().booleanValue();
                if (!snapshotReceived) {
                    if (!isSnapshot) {
                        // we ignore the update and keep waiting until
                        // a full snapshot for the item has been received
                        return;
                    }
                    si.setSnapshotReceived();
                } else {
                    if (isSnapshot) {
                        // it's not the first event we have received carrying
                        // snapshot information for the item; so, this event
                        // is not a snapshot from Lightstreamer point of view
                        isSnapshot = false;
                    }
                }
                
                // We should ensure that update cannot be called after
                // unsubscribe, so we need to hold the subscribedItems lock;
                // however, update is nonblocking; moreover, it only takes locks
                // to first order mutexes; so, it can safely be called here
    
                // Note that, in case a rapid subscribe-unsubscribe-subscribe
                // sequence has just been issued for this item,
                // we may still be receiving and forwarding the snapshot
                // related with the first subscribe call;
                // this case still leads to a perfectly consistent update flow,
                // in this scenario, so no checks are inserted to detect the case
    
                if (si.isStillSubscribed()) {
                    ItemListener itemListener = si.getItemListener();
                    if (isSnapshot) {
                        itemListener.snapshot(currentValues, null);
                    } else {
                        itemListener.update(currentValues);
                    }
                }
            }
        }
    }

    /**
     * Manages the current state of the subscription 
     * for a single Item.
     */
    private class SubscriptionInfo {
        Boolean snapshotReceived = null;
        public void setSnapshotReceived() {
            this.snapshotReceived = Boolean.TRUE;
        }

        public Boolean getSnapshotReceived() {
            return snapshotReceived;
        }

        Boolean stillSubscribed = null;
        
        public boolean isStillSubscribed() {
            return this.stillSubscribed.booleanValue();
        }

        ItemListener itemListener = null;

        public ItemListener getItemListener() {
            return itemListener;
        }
        
        public SubscriptionInfo(boolean snapshotReceived, boolean isStillSubscribed, ItemListener itemListener) {
            this.snapshotReceived = snapshotReceived;
            this.stillSubscribed = isStillSubscribed;
            this.itemListener = itemListener;
        }
        
        public void  stopSubscription() {
            this.stillSubscribed = Boolean.FALSE;
        }
    }
    
}
