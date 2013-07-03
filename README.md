
# Lightstreamer StockList Demo Adapter #

This project includes the resources needed to develop a Data Adapter for the [Lighstreamer StockList Demos](http://www.lightstreamer.com/demos#StockListDemo) that are pluggable into Lightstreamer Server.
Please refer [here](http://www.lightstreamer.com/latest/Lightstreamer_Allegro-Presto-Vivace_5_1_Colosseo/Lightstreamer/DOCS-SDKs/General%20Concepts.pdf) for more details about Lightstreamer Adapters.<br>
The Stock-List Demo simulates a market data feed and front-end for stock quotes. It shows a list of stock symbols and updates prices and other fields displayed on the page in real time.<br>

The project is comprised of source code and a deployment example. The Stock-List Data Adapter is comprised of three Java classes:

## ExternalFeedSimulator ##

Contains the data generation logic (that is, the actual feed simulator). This is a broadcast feed, that delivers all the generated data, with no notion of subscriptions.

## ExternalFeedListener ##

Handles the data delivery from the ExternalFeedSimulator to the DemoDataProvider, to better decouple the two classes.

## DemoDataAdapter ##

Implements the DataProvider interface to handle the communication with Lightstreamer Kernel. Leverages the ExternalFeedListener to get the data originated by the ExternalFeedSimulator. Injects the received data into Lightstreamer Kernel or filters it our based on the subscriptions/unsubscriptions received from the Kernel.

See the source code comments for further details.

The Metadata Adapter functionalities are absolved by the [LiteralBasedProvider](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java), a simple full implementation of a Metadata Adapter, made available in Lightstreamer SDK. 


# Build #

If you want to skip the build process of this Adapter please note that the "Deployment_LS" folder of this project contains a ready-made deployment resource for the Lightstreamer server.<br>
Otherwise follow this steps:

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
After you have Downloaded and installed Lightstreamer, please go to the "adapters" folder of your Lightstreamer Server installation. You should find a "Demo" folder containing some adapter ready-made for several demo including the Stock-List ones, please note that the MetaData Adapter jar installed is a mixed one that combines the functionality of several demos.
If this is not your case because you have removed the "Demo" folder or you want to install the Stock-List adapter alone, please follow this steps to configure the adapter properly:

1. You have to create a new folder, let's call it "StockList", and a "lib" folder inside it.
2. Create an "adapters.xml" file inside the "StockList" folder and use a content similar to that of the file in the directory /Deplolyment_LS/StockList (this is an example configuration, you can modify it to your liking).
3. Copy into /StockList/lib the jars (LS_quote_feed_simulator.jar and LS_StockListDemo_DataAdapter.jar) created in the previous section.

Now with the StockList folder obtained on your behalf or with the one in the /Deployment_LS of this project, you must follow these steps:

1. Make sure you have installed Lightstreamer Server, as explained in the GETTING_STARTED.TXT file in the installation home directory.
2. Make sure that Lightstreamer Server is not running.
3. Copy the "StockList" directory and all of its files from this directory to the "adapters" subdirectory in your Lightstreamer Server installation home directory.
4. Copy the "ls-generic-adapters.jar" file from the "lib" directory of the sibling "Reusable_MetadataAdapters" SDK example to the "shared/lib" subdirectory in your Lightstreamer Server installation home directory.
5. Lightstreamer Server is now ready to be launched.

Please try your Adapter with one of the clients in this [list](https://github.com/Weswit/Lightstreamer-example-StockList-adapter-java#clients-using-this-adapter).

# See Also #

## Clients using this Adapter ##

* [Lightstreamer StockList Demo Client for Dojo](https://github.com/Weswit/Lightstreamer-example-StockList-client-dojo)
* [Lightstreamer StockList Demo Client for PhoneGap](https://github.com/Weswit/Lightstreamer-example-StockList-client-phonegap)
* [Lightstreamer Portfolio Demo Client for JavaScript](https://github.com/Weswit/Lightstreamer-example-Portfolio-client-javascript)
* [Lightstreamer Portfolio Demo Client for Dojo](https://github.com/Weswit/Lightstreamer-example-Portfolio-client-dojo)
* [Lightstreamer StockList Demo Client for jQuery](https://github.com/Weswit/Lightstreamer-example-StockList-client-jquery)
* [Lightstreamer StockList Demo Client for Java SE](https://github.com/Weswit/Lightstreamer-example-StockList-client-java)
* [Lightstreamer StockList Demo Client for .NET](https://github.com/Weswit/Lightstreamer-example-StockList-client-dotnet)
* [Lightstreamer StockList Demo Client for Adobe Flex SDK](https://github.com/Weswit/Lightstreamer-example-StockList-client-flex)
* [Lightstreamer StockList Demo Client for Flash](https://github.com/Weswit/Lightstreamer-example-StockList-client-flash)
* [Lightstreamer StockList Demo Client for BlackBerry WebWorks](https://github.com/Weswit/Lightstreamer-example-StockList-client-blackberry10-html)

## Related projects ##

* [Lightstreamer Reusable Metadata Adapter in Java](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer Portfolio Demo Adapter](https://github.com/Weswit/Lightstreamer-example-Portfolio-adapter-java)

# Lightstreamer Compatibility Notes #

- Compatible with Lightstreamer SDK for Java Adapters since 5.1