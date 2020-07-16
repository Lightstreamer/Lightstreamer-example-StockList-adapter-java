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
package stocklist_demo.adapters;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import stocklist_demo.feed_simulator.ExternalFeedListener;
import stocklist_demo.feed_simulator.ExternalFeedSimulator;

import com.lightstreamer.interfaces.data.DataProvider;
import com.lightstreamer.interfaces.data.IndexedItemEvent;
import com.lightstreamer.interfaces.data.ItemEvent;
import com.lightstreamer.interfaces.data.ItemEventListener;
import com.lightstreamer.interfaces.data.SubscriptionException;

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
public class StockQuotesDataAdapter implements DataProvider {

    /**
     * Private logger; a specific "LS_demos_Logger.StockQuotes" category
     * should be supplied by log4j configuration.
     */
    private Logger logger;

    private volatile ItemEventListener listener;
    private final HashMap<String,SubscriptionInfo> subscribedItems = new HashMap<String,SubscriptionInfo>();
    private final ExternalFeedSimulator myFeed;

    /*
     * used for the IndexedItemEvent version only (currently commented out)
     */
    private static final String[] names = new String[]{"stock_name", "time",
                                                       "last_price",
                                                       "ask", "bid",
                                                       "bid_quantity",
                                                       "ask_quantity",
                                                       "pct_change", "min",
                                                       "max", "ref_price",
                                                       "open_price", "item_status"};
    /*
     * used for the IndexedItemEvent version only (currently commented out)
     */
    private static final HashMap<String,Integer> codes = new HashMap<String,Integer>() {
        {
            for (int code = 0; code < names.length; code++) {
                put(names[code], new Integer(code));
            }
        }
    };
    
    /**
     * Currently unused:
     * A static map, to be used by the Metadata Adapter to find the data
     * adapter instance; this allows the Metadata Adapter to forward client
     * messages to the adapter.
     * The map allows multiple instances of this Data Adapter to be included
     * in different Adapter Sets. Each instance is identified with the name
     * of the related Adapter Set; defining multiple instances in the same
     * Adapter Set is not allowed.
     */
    public static final ConcurrentHashMap<String, StockQuotesDataAdapter> feedMap =
        new ConcurrentHashMap<String, StockQuotesDataAdapter>();

    public StockQuotesDataAdapter() {
        myFeed = new ExternalFeedSimulator();
    }

    /**
     * Starts the simulator feed (or connects to the external
     * feed, for a real feed).
     */
    public void init(Map params, File configDir) {
        String logConfig = (String) params.get("log_config");
        if (logConfig != null) {
            File logConfigFile = new File(configDir, logConfig);
            String logRefresh = (String) params.get("log_config_refresh_seconds");
            if (logRefresh != null) {
                DOMConfigurator.configureAndWatch(logConfigFile.getAbsolutePath(), Integer.parseInt(logRefresh) * 1000);
            } else {
                DOMConfigurator.configure(logConfigFile.getAbsolutePath());
            }
        }
        logger = Logger.getLogger("LS_demos_Logger.StockQuotes");
        
        //The feedMap of this adapter is never used
        // Read the Adapter Set name, which is supplied by the Server as a parameter
        String adapterSetId = (String) params.get("adapters_conf.id");
        // Put a reference to this instance on a static map
        // to be read by the Metadata Adapter
        feedMap.put(adapterSetId, this);

        myFeed.start();
        logger.info("StockQuotesDataAdapter ready.");
    }

    public void setListener(ItemEventListener listener) {
        this.listener = listener;
        myFeed.setFeedListener(new MyFeedListener());
    }

    public void subscribe(String itemName, boolean needsIterator)
            throws SubscriptionException {
        logger.debug("Subscribing to " + itemName);
        if (needsIterator) {
            // OK, as we will always send HashMap objects
        }
        if (itemName.startsWith("item")) {
            
            synchronized (subscribedItems) {
                SubscriptionInfo si = new SubscriptionInfo(new Boolean(false), new Boolean(true));
                subscribedItems.put(itemName, si);
            }
            // now we ask the feed for the snapshot; our feed will insert
            // an event with snapshot information into the normal updates flow
            myFeed.sendCurrentValues(itemName);
        } else {
            logger.error("Cannot subscribe to " + itemName + " - only names starting with \"item\" are supported");
            throw new SubscriptionException("Unexpected item: " + itemName);
        }
    }

    public void unsubscribe(String itemName) {
        logger.debug("Unsubscribing from " + itemName);
        synchronized (subscribedItems) {
            SubscriptionInfo si = subscribedItems.remove(itemName);
            synchronized (si) {
                si.stopSubscription();
            }
        }
    }

    public boolean isSnapshotAvailable(String itemName) {
        return true;
    }

    // never used in the demo, just showing the feature
    public void clearStatus() {
        synchronized (subscribedItems) {
            Set<String> keys = subscribedItems.keySet();
            for (String itemName : keys) {
                listener.clearSnapshot(itemName);
            }
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
    
                // For better control of the update flow, the SmartDataProvider
                // interface can be implemented instead of the simpler DataProvider
    
                /* basic version:
                 */
                if (si.isStillSubscribed()) {
                    listener.update(itemName, currentValues, isSnapshot);
                }
    
                /* ItemEvent version:
                listener.update(itemName, new ItemEvent() {
    
                    public Iterator getNames() {
                        return currentValues.keySet().iterator();
                    }
    
                    public Object getValue(String name) {
                        return currentValues.get(name);
                    }
    
                }, isSnapshot);
                */
    
                /* IndexedItemEvent version:
                listener.update(itemName, new IndexedItemEvent() {
    
                    public int getMaximumIndex() {
                        return names.length - 1;
                    }
    
                    public int getIndex(String name) {
                        Object keyObject = codes.get(name);
                        if (keyObject == null) {
                            return -1;
                        } else {
                            return ((Integer) keyObject).intValue();
                        }
                    }
    
                    public String getName(int index) {
                        if (currentValues.get(names[index]) == null) {
                            return null;
                        } else {
                            return names[index];
                        }
                    }
    
                    public Object getValue(int index) {
                        return currentValues.get(names[index]);
                    }
    
                }, isSnapshot);
                */
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
            this.snapshotReceived = new Boolean(true);
        }

        public Boolean getSnapshotReceived() {
            return snapshotReceived;
        }

        Boolean stillSubscribed = null;
        
        public boolean isStillSubscribed() {
            return this.stillSubscribed.booleanValue();
        }
        
        public SubscriptionInfo(Boolean snapshotReceived, Boolean isStillSubscribed) {
            this.snapshotReceived = snapshotReceived;
            this.stillSubscribed = isStillSubscribed;
        }
        
        public void  stopSubscription() {
            this.stillSubscribed = new Boolean(false);
        }
    }
    
}
