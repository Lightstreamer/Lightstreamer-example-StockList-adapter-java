# Lightstreamer - Stock-List Demo - Java Adapter

<!-- START DESCRIPTION lightstreamer-example-stocklist-adapter-java -->

This project includes the resources needed to develop a Data Adapter for the [Lighstreamer - Basic Stock-List Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-Stocklist-client-javascript#basic-stock-list-demo---html-client) that is pluggable into Lightstreamer Server.<br>
The Stock-List demos simulate a market data feed and front-end for stock quotes. They show a list of stock symbols and updates prices and other fields displayed on the page in real-time.<br>

## Details

The project is comprised of source code and a deployment example.

### Dig the Code

The Stock-List Data Adapter is comprised of three Java classes.

#### ExternalFeedSimulator

Contains the data generation logic (that is, the actual feed simulator). This is a broadcast feed, which delivers all the generated data, with no notion of subscriptions.

#### ExternalFeedListener

Handles the data delivery from the ExternalFeedSimulator to the DemoDataProvider, to better decouple the two classes.

#### DemoDataAdapter

Implements the DataProvider interface to handle the communication with Lightstreamer Kernel. Leverages the ExternalFeedListener to get the data originated by the ExternalFeedSimulator. Injects the received data into Lightstreamer Kernel or filters it out based on the subscriptions/unsubscriptions received from the Kernel.<br>
<br>

See the source code comments for further details.

The Metadata Adapter functionalities are absolved by the `LiteralBasedProvider` in [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java), a simple full implementation of a Metadata Adapter, already provided by Lightstreamer server. 

<!-- END DESCRIPTION lightstreamer-example-stocklist-adapter-java -->

### The Adapter Set Configuration

This Adapter Set is configured and will be referenced by the clients as `DEMO`.

The `adapters.xml` file for the Stock-List Demo, should look like:

```xml
<?xml version="1.0"?>

<!-- Mandatory. Define an Adapter Set and sets its unique ID. -->
<adapters_conf id="DEMO">

  <!--
    Not all configuration options of an Adapter Set are exposed by this file.
    You can easily expand your configurations using the generic template,
    `DOCS-SDKs/sdk_adapter_java_inprocess/doc/adapter_conf_template/adapters.xml`,
    as a reference.
  -->

    <metadata_provider>

        <adapter_class>com.lightstreamer.adapters.metadata.LiteralBasedProvider</adapter_class>

        <!-- Optional.
             See LiteralBasedProvider javadoc. -->
        <!--
        <param name="max_bandwidth">40</param>
        <param name="max_frequency">3</param>
        <param name="buffer_size">30</param>
        <param name="distinct_snapshot_length">10</param>
        <param name="prefilter_frequency">5</param>
        <param name="allowed_users">user123,user456</param>
        -->

        <!-- Optional.
             See LiteralBasedProvider javadoc. -->
        <param name="item_family_1">item.*</param>
        <param name="modes_for_item_family_1">MERGE</param>

    </metadata_provider>

    <data_provider name="QUOTE_ADAPTER">

        <adapter_class>stocklist_demo.adapters.StockQuotesDataAdapter</adapter_class>

        <!-- Optional for StockQuotesDataAdapter.
             Configuration file for the Adapter's own logging.
             Logging is managed through log4j. -->
        <param name="log_config">adapters_log_conf.xml</param>
        <param name="log_config_refresh_seconds">10</param>

    </data_provider>

</adapters_conf>
```

<i>NOTE: not all configuration options of an Adapter Set are exposed by the file suggested above. 
You can easily expand your configurations using the generic template, `DOCS-SDKs/sdk_adapter_java_inprocess/doc/adapter_conf_template/adapters.xml`, as a reference.</i><br>
<br>
Please refer [here](http://www.lightstreamer.com/docs/base/General%20Concepts.pdf) for more details about Lightstreamer Adapters.<br>

## Install

If you want to install a version of the *Stock-List Demo* in your local Lightstreamer Server, follow these steps:

* Download *Lightstreamer Server* (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* Make sure that Lightstreamer Server is not running.
* In the `adapters` folder of your Lightstreamer Server installation, you may find a `Demo` folder, containing some adapters ready-made for several demo including the Stock-List one. If this is the case, you already have a Stock-List Demo Adapter installed and you may stop here. Please note that, in case of Demo folder already installed, the MetaData Adapter jar installed is a mixed one that combines the functionality of several demos. If the Demo folder is not installed, or you have removed it, or you want to install the Stock-List Adapter Set alone, please continue to follow the next steps.
* Get the `deploy.zip` file of the [latest release](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-java/releases), unzip it, and copy the `StockList` folder into the `adapters` folder of your Lightstreamer Server installation.
* Launch Lightstreamer Server.
* Test the Adapter, launching the [Basic Stock-List Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-Stocklist-client-javascript#basic-stock-list-demo---html-client) listed in [Clients Using This Adapter](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-java#clients-using-this-adapter).

## Build

To build your own version of `LS_StockListDemo_DataAdapter.jar` and `LS_quote_feed_simulator.jar`, instead of using the one provided in the `deploy.zip` file from the [Install](https://github.com/Weswit/Lightstreamer-example-Stocklist-adapter-java#install) section above, follow these steps:
* Download this project.
* Get the `ls-adapter-interface.jar` file from the `/lib` folder of the [latest Lightstreamer distribution](http://www.lightstreamer.com/download), and copy it into the `lib` folder.
* Get the `log4j-1.2.17.jar` file from [Apache log4j](https://logging.apache.org/log4j/1.2/) and copy it into the `lib` folder.
* Create the jars `LS_StockListDemo_DataAdapter.jar` and `LS_quote_feed_simulator.jar` with commands like these:
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath lib/log4j-1.2.17.jar -sourcepath src/src_feed -d tmp_classes src/src_feed/portfolio_demo/feed_simulator/ExternalFeedSimulator.java

 >jar cvf LS_quote_feed_simulator.jar -C tmp_classes src_feed

 >javac -source 1.7 -target 1.7 -nowarn -g -classpath lib/log4j-1.2.17.jar;lib/ls-adapter-interface.jar;LS_quote_feed_simulator.jar -sourcepath src/src_adapter -d tmp_classes src/src_adapter/stocklist_demo/adapters/StockQuotesDataAdapter.java

 >jar cvf LS_StockListDemo_DataAdapter.jar -C tmp_classes src_adapter
```
* Copy the just compiled `LS_StockListDemo_DataAdapter.jar` and `LS_quote_feed_simulator.jar` in the `adapters/Stocklist/lib` folder of your Lightstreamer Server installation.

## See Also

### Clients Using This Adapter

<!-- START RELATED_ENTRIES -->

* [Complete list of clients using this Adapter](https://github.com/Weswit?query=lightstreamer-example-stocklist-client)

<!-- END RELATED_ENTRIES -->
### Related Projects

* [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer - Portfolio Demo - Java Adapter](https://github.com/Weswit/Lightstreamer-example-Portfolio-adapter-java)

### The Same Demo Adapter With Other Technologies

* [Lightstreamer - Stock-List Demo - .NET Adapter](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-dotnet)
* [Lightstreamer - Stock-List Demo - Java (JMS) Adapter](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-JMS)

## Lightstreamer Compatibility Notes

* Compatible with Lightstreamer SDK for Java In-Process Adapters since 6.0
- For a version of this example compatible with Lightstreamer SDK for Java Adapters version 5.1, please refer to [this tag](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-java/releases/tag/for_Lightstreamer_5.1.2).
