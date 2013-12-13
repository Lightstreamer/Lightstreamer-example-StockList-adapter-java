# Lightstreamer - Stock-List Demo - Java Adapter #

This project includes the resources needed to develop a Data Adapter for the [Lighstreamer - Basic Stock-List Demo - HTML Client](http://www.lightstreamer.com/demos#StockListDemo) that is pluggable into Lightstreamer Server.
Please refer [here](http://www.lightstreamer.com/latest/Lightstreamer_Allegro-Presto-Vivace_5_1_Colosseo/Lightstreamer/DOCS-SDKs/General%20Concepts.pdf) for more details about Lightstreamer Adapters.<br>
The Stock-List demos simulate a market data feed and front-end for stock quotes. They show a list of stock symbols and updates prices and other fields displayed on the page in real time.<br>

The project is comprised of source code and a deployment example. The Stock-List Data Adapter is comprised of three Java classes.

## ExternalFeedSimulator ##

Contains the data generation logic (that is, the actual feed simulator). This is a broadcast feed, that delivers all the generated data, with no notion of subscriptions.

## ExternalFeedListener ##

Handles the data delivery from the ExternalFeedSimulator to the DemoDataProvider, to better decouple the two classes.

## DemoDataAdapter ##

Implements the DataProvider interface to handle the communication with Lightstreamer Kernel. Leverages the ExternalFeedListener to get the data originated by the ExternalFeedSimulator. Injects the received data into Lightstreamer Kernel or filters it our based on the subscriptions/unsubscriptions received from the Kernel.<br>
<br>

See the source code comments for further details.

The Metadata Adapter functionalities are absolved by the `LiteralBasedProvider` in [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java), a simple full implementation of a Metadata Adapter, made available in Lightstreamer distribution. 


# Build #

If you want to skip the build process of this Adapter please note that in the [deploy release](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-java/releases) of this project you can find the "deploy.zip" file that contains a ready-made deployment resource for the Lightstreamer server. <br>
Otherwise follow these steps:

* Get the ls-adapter-interface.jar and log4j-1.2.15.jar files from the [latest Lightstreamer distribution](http://www.lightstreamer.com/download) and put these files into lib folder.
* Create the jars LS_quote_feed_simulator.jar and LS_StockListDemo_DataAdapter.jar created by something like these commands
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath lib/log4j-1.2.15.jar -sourcepath src/src_feed -d tmp_classes src/src_feed/portfolio_demo/feed_simulator/ExternalFeedSimulator.java
 
 >jar cvf LS_quote_feed_simulator.jar -C tmp_classes src_feed
 
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath lib/log4j-1.2.15.jar;lib/ls-adapter-interface.jar;LS_quote_feed_simulator.jar -sourcepath src/src_adapter -d tmp_classes src/src_adapter/stocklist_demo/adapters/StockQuotesDataAdapter.java
 
 >jar cvf LS_StockListDemo_DataAdapter.jar -C tmp_classes src_adapter
```

# Deploy #

Now you are ready to deploy the Stock-List Demo Adapter into Lighstreamer server. 
After you have Downloaded and installed Lightstreamer, please go to the "adapters" folder of your Lightstreamer Server installation. You should find a "Demo" folder containing some adapters ready-made for several demo including the Stock-List ones, please note that the MetaData Adapter jar installed is a mixed one that combines the functionality of several demos. If this is not your case because you have removed the "Demo" folder or you want to install the Stock-List Adapter Set alone, please follow the below steps to configure the Stock-List Adapter Set properly.

You have to create a specific folder to deploy the Stock-List Adapter otherwise get the ready-made "StockList" deploy folder from "deploy.zip" of the [latest release](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-java/releases) of this project and skips the next three steps.<br>

1. You have to create a new folder, let's call it "StockList", and a "lib" folder inside it.
2. Create an "adapters.xml" file inside the "StockList" folder and use the following content (this is an example configuration, you can modify it to your liking):

```xml
<?xml version="1.0"?>

<!-- Mandatory. Define an Adapter Set and sets its unique ID. -->
<adapters_conf id="DEMO">

    <!-- Mandatory. Define the Metadata Adapter. -->
    <metadata_provider>

        <!-- Mandatory. Java class name of the adapter. -->
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

    <!-- Mandatory. Define the Data Adapter. -->
    <data_provider name="QUOTE_ADAPTER">

        <!-- Mandatory. Java class name of the adapter. -->
        <adapter_class>stocklist_demo.adapters.StockQuotesDataAdapter</adapter_class>

        <!-- Optional for StockQuotesDataAdapter.
             Configuration file for the Adapter's own logging.
             Logging is managed through log4j. -->
        <param name="log_config">adapters_log_conf.xml</param>
        <param name="log_config_refresh_seconds">10</param>

    </data_provider>

</adapters_conf>
```
<br>
3. Copy into /StockList/lib the jars (LS_quote_feed_simulator.jar and LS_StockListDemo_DataAdapter.jar) created in the previous section.

Now your "StockList" folder is ready to be deployed in the Lightstreamer server, please follow these steps:

1. Make sure you have installed Lightstreamer Server, as explained in the GETTING_STARTED.TXT file in the installation home directory.
2. Make sure that Lightstreamer Server is not running.
3. Copy the "StockList" directory and all of its files from this directory to the "adapters" subdirectory in your Lightstreamer Server installation home directory.
4. Copy the "ls-generic-adapters.jar" file from the "lib" directory of the sibling "Reusable_MetadataAdapters" SDK example to the "shared/lib" subdirectory in your Lightstreamer Server installation home directory.
5. Lightstreamer Server is now ready to be launched.

Please test your Adapter with one of the clients in this [list](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-java#clients-using-this-adapter).

# See Also #

## Clients using this Adapter ##
* [Lightstreamer - Stock-List Demos - HTML Clients](https://github.com/Weswit/Lightstreamer-example-Stocklist-client-javascript)
* [Lightstreamer - Stock-List Demo - Dojo Toolkit Client](https://github.com/Weswit/Lightstreamer-example-StockList-client-dojo)
* [Lightstreamer - Stock-List Demo - PhoneGap Client](https://github.com/Weswit/Lightstreamer-example-StockList-client-phonegap)
* [Lightstreamer - Portfolio Demos - HTML Clients](https://github.com/Weswit/Lightstreamer-example-Portfolio-client-javascript)
* [Lightstreamer - Portfolio Demo - Dojo Toolkit Client](https://github.com/Weswit/Lightstreamer-example-Portfolio-client-dojo)
* [Lightstreamer - Basic Stock-List Demo - jQuery (jqGrid) Client](https://github.com/Weswit/Lightstreamer-example-StockList-client-jquery)
* [Lightstreamer - Basic Stock-List Demo - Java SE (Swing) Client](https://github.com/Weswit/Lightstreamer-example-StockList-client-java)
* [Lightstreamer - Basic Stock-List Demo - .NET Client](https://github.com/Weswit/Lightstreamer-example-StockList-client-dotnet)
* [Lightstreamer - Stock-List Demos - Flex Clients](https://github.com/Weswit/Lightstreamer-example-StockList-client-flex)
* [Lightstreamer - Stock-List Demos - Flash Clients](https://github.com/Weswit/Lightstreamer-example-StockList-client-flash)
* [Lightstreamer - Basic Stock-List Demo - BlackBerry (WebWorks) Client](https://github.com/Weswit/Lightstreamer-example-StockList-client-blackberry10-html)

## Related projects ##
* [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer - Portfolio Demo - Java Adapter](https://github.com/Weswit/Lightstreamer-example-Portfolio-adapter-java)

## The same Demo Adapter with other technologies ##
* [Lightstreamer - Stock-List Demo - .NET Adapter](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-dotnet)

# Lightstreamer Compatibility Notes #

- Compatible with Lightstreamer SDK for Java Adapters since 5.1
