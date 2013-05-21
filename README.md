
Lightstreamer Stock-List Demo Adapter
=====================================

This project include the implementation of the Data Adapter in Java implementing the functionalities required by Lightstreamer Stock-List demos.

Java Data Adapter and MetaData Adapter
--------------------------------------

This Data Adapter accepts a limited set of item names (the names starting with "item") and listens to a (simulated) stock quotes feed, waiting for update events. The events pertaining to the currently subscribed items are then forwarded to Lightstreamer.
This example demonstrates how a Data Adapter could interoperate with a broadcast feed (which always sends data for all available items) by selecting the updates to be sent. Many other types of feeds may exist, with very different behaviors.
The Metadata Adapter functionalities are absolved by the LiteralBasedProvider, a simple full implementation of a Metadata Adapter, made available in Lightstreamer SDK. 

External Feed Simulator
-----------------------

This component simulates an external data feed that supplies quote values for all the stocks needed for the demos.

Configure Lightstreamer
-----------------------

After you have Downloaded and installed Lightstreamer, please go to the "adapters" folder of your Lightstreamer Server installation. You should find a "Demo" folder containing some adapter ready-made for several demo including the Stock-List ones, please note that the MetaData Adapter jar installed is a mixed one that combines the functionality of several demos.
If this is not your case because you have removed the "Demo" folder or you want to install the Stock-List adapter alone, please follow this steps to configure the adapter properly:

1. You have to create a new folder to deploy the Stock-List adapters, let's call it "StockList", and a "lib" folder inside it.
2. Create an "adapters.xml" file inside the "StockList" folder and use the following contents (this is an example configuration, you can modify it to your liking):
```xml      
<?xml version="1.0"?>
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
3. Get the ls-adapter-interface.jar and log4j-1.2.15.jar files from the [Lightstreamer 5 Colosseo distribution](http://www.lightstreamer.com/download).
4. Copy into "lib" folder the jars LS_quote_feed_simulator.jar and LS_StockListDemo_DataAdapter.jar created by something like these commands
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/log4j-1.2.15.jar -sourcepath src/src_feed -d tmp_classes src/src_feed/portfolio_demo/feed_simulator/ExternalFeedSimulator.java
 
 >jar cvf LS_quote_feed_simulator.jar -C tmp_classes src_feed
 
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/log4j-1.2.15.jar;compile_libs/ls-adapter-interface/ls-adapter-interface.jar;LS_quote_feed_simulator.jar -sourcepath src/src_adapter -d tmp_classes src/src_adapter/stocklist_demo/adapters/StockQuotesDataAdapter.java
 
 >jar cvf LS_StockListDemo_DataAdapter.jar -C tmp_classes src_adapter
```

See Also
--------

* TODO: add link to GitHub project of [Lightstreamer Basic Stock-List Demo Client for JavaScript]
* TODO: add link to GitHub project of [Lightstreamer Stock-List Demo Client for JavaScript]
* TODO: add link to GitHub project of [Lightstreamer Framed Stock-List Demo Client for JavaScript]
* TODO: add link to GitHub project of [Lightstreamer Simple Grid Demo Client for JavaScript]
* TODO: add link to GitHub project of [Lightstreamer Bandwidth and Frequency Demo Client for JavaScript]
* TODO: add link to GitHub project of [Lightstreamer Chart Demo Client for JavaScript]
* [Lightstreamer Portfolio Demo Client for JavaScript](https://github.com/Weswit/Lightstreamer-example-Portfolio-client-javascript)
* [Lightstreamer Portfolio Demo Client for Dojo](https://github.com/Weswit/Lightstreamer-example-Portfolio-client-dojo)

Lightstreamer Compatibility Notes
---------------------------------

- Compatible with Lightstreamer SDK for Java Adapters since 5.1