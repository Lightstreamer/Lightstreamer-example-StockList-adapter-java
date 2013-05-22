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

import java.util.HashMap;

/**
 * Used to receive data from the simulated broadcast feed in an
 * asynchronous way, through the onEvent method.
 */
public interface ExternalFeedListener {

    /**
     * Called by the feed for each update event occurrence on some stock.
     * If isSnapshot is true, then the event contains a full snapshot,
     * with the current values of all fields for the stock.
     */
    void onEvent(String itemName, HashMap<String,String> currentValues, boolean isSnapshot);

}
