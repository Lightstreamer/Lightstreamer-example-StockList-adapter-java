/*
*
* Copyright 2013 Weswit s.r.l.
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package stocklist_demo.feed_simulator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simulates an external data feed that supplies quote values for all the
 * stocks needed for the demo.
 */
public class ExternalFeedSimulator {

    private static final Timer dispatcher = new Timer();
    private static final Random random = new Random();

    /**
     * Used to automatically generate the updates for the 30 stocks:
     * mean and standard deviation of the times between consecutive
     * updates for the same stock.
     */
    private static final double[] updateTimeMeans = {30000, 500, 3000, 90000,
                                                     7000, 10000, 3000, 7000,
                                                     7000, 7000, 500, 3000,
                                                     20000, 20000, 20000, 30000,
                                                     500, 3000, 90000, 7000,
                                                     10000, 3000, 7000, 7000,
                                                     7000, 500, 3000, 20000,
                                                     20000, 20000, };
    private static final double[] updateTimeStdDevs = {6000, 300, 1000, 1000,
                                                       100, 5000, 1000, 3000,
                                                       1000, 6000, 300, 1000,
                                                       1000, 4000, 1000, 6000,
                                                       300, 1000, 1000, 100,
                                                       5000, 1000, 3000, 1000,
                                                       6000, 300, 1000, 1000,
                                                       4000, 1000, };

    /**
     * Used to generate the initial field values for the 30 stocks.
     */
    private static final double[] refprices = {3.04, 16.09, 7.19, 3.63, 7.61,
                                               2.30, 15.39, 5.31, 4.86, 7.61,
                                               10.41, 3.94, 6.79, 26.87, 2.27,
                                               13.04, 6.09, 17.19, 13.63, 17.61,
                                               11.30, 5.39, 15.31, 14.86, 17.61,
                                               5.41, 13.94, 16.79, 6.87,
                                               11.27, };
    private static final double[] openprices = {3.10, 16.20, 7.25, 3.62, 7.65,
                                                2.30, 15.85, 5.31, 4.97, 7.70,
                                                10.50, 3.95, 6.84, 27.05, 2.29,
                                                13.20, 6.20, 17.25, 13.62,
                                                17.65, 11.30, 5.55, 15.31,
                                                14.97, 17.70, 5.42, 13.95,
                                                16.84, 7.05, 11.29, };
    private static final double[] minprices = {3.09, 15.78, 7.15, 3.62, 7.53,
                                               2.28, 15.60, 5.23, 4.89, 7.70,
                                               10.36, 3.90, 6.81, 26.74, 2.29,
                                               13.09, 5.78, 17.15, 13.62, 17.53,
                                               11.28, 5.60, 15.23, 14.89, 17.70,
                                               5.36, 13.90, 16.81, 6.74,
                                               11.29, };
    private static final double[] maxprices = {3.19, 16.20, 7.26, 3.71, 7.65,
                                               2.30, 15.89, 5.31, 4.97, 7.86,
                                               10.50, 3.95, 6.87, 27.05, 2.31,
                                               13.19, 6.20, 17.26, 13.71, 17.65,
                                               11.30, 5.89, 15.31, 14.97, 17.86,
                                               5.50, 13.95, 16.87, 7.05,
                                               11.31, };
    private static final String[] stockNames = {"Anduct", "Ations Europe",
                                                "Bagies Consulting", "BAY Corporation",
                                                "CON Consulting", "Corcor PLC",
                                                "CVS Asia", "Datio PLC",
                                                "Dentems", "ELE Manufacturing",
                                                "Exacktum Systems", "KLA Systems Inc",
                                                "Lted Europe", "Magasconall Capital",
                                                "MED", "Mice Investments",
                                                "Micropline PLC", "Nologicroup Devices",
                                                "Phing Technology", "Pres Partners",
                                                "Quips Devices", "Ress Devices",
                                                "Sacle Research", "Seaging Devices",
                                                "Sems Systems, Inc", "Softwora Consulting",
                                                "Systeria Develop", "Thewlec Asia",
                                                "Virtutis", "Yahl" };

    /**
     * Used to keep the contexts of the 30 stocks.
     */
    private final ArrayList stockGenerators = new ArrayList();

    private ExternalFeedListener listener;

    /**
     * Starts generating update events for the stocks. Sumulates attaching
     * and reading from an external broadcast feed.
     */
    public void start() {
        for (int i = 0; i < 30; i++) {
            MyProducer myProducer = new MyProducer("item" + (i + 1), i);
            stockGenerators.add(myProducer);
            long waitTime = myProducer.computeNextWaitTime();
            scheduleGenerator(myProducer, waitTime);
        }
    }

    /**
     * Sets an internal listener for the update events.
     * Since now, the update events were ignored.
     */
    public void setFeedListener(ExternalFeedListener listener) {
        this.listener = listener;
    }

    /**
     * Generates new values and sends a new update event at the time
     * the producer declared to do it.
     */
    private void scheduleGenerator(final MyProducer producer, long waitTime) {
        dispatcher.schedule(new TimerTask() {
            public void run() {
                long nextWaitTime;
                synchronized (producer) {
                    producer.computeNewValues();
                    if (listener != null) {
                        listener.onEvent(producer.itemName,
                                         producer.getCurrentValues(false),
                                         false);
                    }
                    nextWaitTime = producer.computeNextWaitTime();
                }
                scheduleGenerator(producer, nextWaitTime);
            }
        }, waitTime);
    }

    /**
     * Forces sending an event with a full snapshot for a stock.
     */
    public void sendCurrentValues(String itemName) {
        for (int i = 0; i < 30; i++) {
            final MyProducer myProducer = (MyProducer) stockGenerators.get(i);
            if (myProducer.itemName.equals(itemName)) {
                dispatcher.schedule(new TimerTask() {
                    public void run() {
                        synchronized (myProducer) {
                            listener.onEvent(myProducer.itemName,
                                             myProducer.getCurrentValues(true),
                                             true);
                        }
                    }
                }, 0);
                break;
            }
        }
    }

    /**
     * Manages the current state and generates update events
     * for a single stock.
     */
    private class MyProducer {
        private final String itemName;
        private int open, ref, last, min, max, other;
        private double mean, stddev;
        private String stockName;

        /**
         * Initializes stock data based on the already prepared values.
         */
        public MyProducer(String itemName, int itemPos) {
            this.itemName = itemName;
            // all prices are converted in integer form to simplify the
            // management; they will be converted back before being sent
            // in the update events
            open = (int) Math.round(openprices[itemPos] * 100);
            ref = (int) Math.round(refprices[itemPos] * 100);
            min = (int) Math.ceil(minprices[itemPos] * 100);
            max = (int) Math.floor(maxprices[itemPos] * 100);
            stockName = stockNames[itemPos];
            last = open;
            mean = updateTimeMeans[itemPos];
            stddev = updateTimeStdDevs[itemPos];
        }

        /**
         * Decides, for ease of simulation, the time at which the next
         * update for the stock will happen.
         */
        public long computeNextWaitTime() {
            long millis;
            do {
                millis = (long) gaussian(mean, stddev);
            } while (millis <= 0);
            return millis;
        }

        /**
         * Changes the current data for the stock.
         */
        public void computeNewValues() {
            // this stuff is to ensure that new prices follow a random
            // but nondivergent path, centered around the reference price
            double limit = ref / 4.0;
            int jump = ref / 100;
            double relDist = (last - ref) / limit;
            int direction = 1;
            if (relDist < 0) {
                direction = -1;
                relDist = -relDist;
            }
            if (relDist > 1) {
                relDist = 1;
            }
            double weight = (relDist * relDist * relDist);
            double prob = (1 - weight) / 2;
            boolean goFarther = random.nextDouble() < prob;
            if (!goFarther) {
                direction *= -1;
            }
            int difference = uniform(0, jump) * direction;
            int gap = ref / 250;
            int delta;
            if (gap > 0) {
                do {
                    delta = uniform(-gap, gap);
                } while (delta == 0);
            } else {
                delta = 1;
            }
            last += difference;
            other = last + delta;
            if (last < min) {
                min = last;
            }
            if (last > max) {
                max = last;
            }
        }

        /**
         * Picks the stock field values and stores them in a <field->value>
         * HashMap. If fullData is false, then only the fields whose value
         * is just changed are considered (though this check is not strict).
         */
        public HashMap<String,String> getCurrentValues(boolean fullData) {
            final HashMap<String,String> event = new HashMap<String,String>();

            String format = "HH:mm:ss";
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            Date now = new Date();
            event.put("time", formatter.format(now));
            event.put("timestamp", Long.toString(now.getTime()));
            addDecField("last_price", last, event);
            if (other > last) {
                addDecField("ask", other, event);
                addDecField("bid", last, event);
            } else {
                addDecField("ask", last, event);
                addDecField("bid", other, event);
            }
            int quantity;
            quantity = uniform(1, 200) * 500;
            event.put("bid_quantity", Integer.toString(quantity));
            quantity = uniform(1, 200) * 500;
            event.put("ask_quantity", Integer.toString(quantity));
            double var = (last - ref) / (double) ref * 100;
            addDecField("pct_change", (int) (var * 100), event);
            if ((last == min) || fullData) {
                addDecField("min", min, event);
            }
            if ((last == max) || fullData) {
                addDecField("max", max, event);
            }
            if (fullData) {
                event.put("stock_name", stockName);
                addDecField("ref_price", ref, event);
                addDecField("open_price", open, event);
                //since it's a simulator the item is always active
                event.put("item_status","active");
            }
            return event;
        }
    }

    private void addDecField(String fld, int val100, HashMap<String,String> target) {
        double val = (((double) val100) / 100);
        String buf = Double.toString(val);
        target.put(fld, buf);
    }

    private double gaussian(double mean, double stddev) {
        double base = random.nextGaussian();
        return base * stddev + mean;
    }

    private int uniform(int min, int max) {
        int base = random.nextInt(max + 1 - min);
        return base + min;
    }

}


/*--- Formatted in Lightstreamer Java Convention Style on 2005-11-03 ---*/