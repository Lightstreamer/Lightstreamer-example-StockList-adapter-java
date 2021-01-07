# Lightstreamer - Stock-List Demo - Java Adapter

<!-- START DESCRIPTION lightstreamer-example-stocklist-adapter-java -->

This project includes the resources needed to develop a Data Adapter for the [Lighstreamer - Basic Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-Stocklist-client-javascript#basic-stock-list-demo---html-client) that is pluggable into Lightstreamer Server.<br>
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

The Metadata Adapter functionalities are absolved by the `LiteralBasedProvider` in [Lightstreamer Java In-Process Adapter SDK](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess#literalbasedprovider-metadata-adapter), a simple full implementation of a Metadata Adapter, already provided by Lightstreamer server. 

<!-- END DESCRIPTION lightstreamer-example-stocklist-adapter-java -->

### The Adapter Set Configuration

This Adapter Set is configured and will be referenced by the clients as `DEMO`.

The `adapters.xml` file for the Stock-List Demo, should look like:

```xml
<?xml version="1.0"?>

<!-- Mandatory. Define an Adapter Set and sets its unique ID. -->
<adapters_conf id="DEMO">

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
        <param name="log_config_refresh_seconds">10</param>

    </data_provider>

</adapters_conf>
```

<i>NOTE: not all configuration options of an Adapter Set are exposed by the file suggested above. 
You can easily expand your configurations using the generic template, see the [Java In-Process Adapter Interface Project](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess#configuration) for details.</i><br>
<br>
Please refer [here](https://lightstreamer.com/docs/ls-server/latest/General%20Concepts.pdf) for more details about Lightstreamer Adapters.<br>

## Install

If you want to install a version of the *Stock-List Demo* in your local Lightstreamer Server, follow these steps:

* Download *Lightstreamer Server* (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* Make sure that Lightstreamer Server is not running.
* Get the `deploy.zip` file of the [latest release](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java/releases), unzip it, and copy the `StockList` folder into the `adapters` folder of your Lightstreamer Server installation.
* Launch Lightstreamer Server.
* Test the Adapter, launching the [Basic Stock-List Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-Stocklist-client-javascript#basic-stock-list-demo---html-client) listed in [Clients Using This Adapter](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java#clients-using-this-adapter).

## Build

To build your own version of `example-StockList-adapter-java-0.0.1-SNAPSHOT.jar` instead of using the one provided in the `deploy.zip` file from the [Install](https://github.com/Lightstreamer/Lightstreamer-example-Stocklist-adapter-java#install) section above, you have two options:
either use [Maven](https://maven.apache.org/) (or other build tools) to take care of dependencies and building (recommended) or gather the necessary jars yourself and build it manually.
For the sake of simplicity only the Maven case is detailed here.

### Maven

You can easily build and run this application using Maven through the pom.xml file located in the root folder of this project. As an alternative, you can use an alternative build tool (e.g. Gradle, Ivy, etc.) by converting the provided pom.xml file.

Assuming Maven is installed and available in your path you can build the demo by running
```sh 
 mvn install dependency:copy-dependencies 
```

## See Also

### Clients Using This Adapter

<!-- START RELATED_ENTRIES -->

* [Complete list of clients using this Adapter](https://github.com/Lightstreamer?utf8=%E2%9C%93&q=lightstreamer-example-stocklist-client&type=&language=)

<!-- END RELATED_ENTRIES -->
### Related Projects

* [Lightstreamer Java In-Process Adapter SDK](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess)
* [LiteralBasedProvider Metadata Adapter](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess#literalbasedprovider-metadata-adapter)
* [Lightstreamer - Portfolio Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-Portfolio-adapter-java)

### The Same Demo Adapter With Other Technologies

* [Lightstreamer - Stock-List Demo - .NET Adapter](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-dotnet)
* [Lightstreamer - Stock-List Demo - Java (JMS) Adapter](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-JMS)

## Lightstreamer Compatibility Notes

* Compatible with Lightstreamer SDK for Java In-Process Adapters since 6.0
- For a version of this example compatible with Lightstreamer SDK for Java Adapters version 5.1, please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-StockList-adapter-java/releases/tag/for_Lightstreamer_5.1.2).
