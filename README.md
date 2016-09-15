# pvwatts-java

**pvwatts-java** is a relatively faithful implementation of the NREL PVWatts [v4](https://developer.nrel.gov/docs/solar/pvwatts-v4/) and [v5](https://developer.nrel.gov/docs/solar/pvwatts-v5/) APIs. This is accomplished via the [SAM SDK](https://sam.nrel.gov/sdk).

## Usage
**pvwatts-java** is usable in two ways: as a drop-in replacement for the [PvWatts JSON APIs](https://developer.nrel.gov/docs/solar/) or as a library that you can call directly.

### Web API
To start the API, simply run `mvn clean compile exec:java`. Once it has finished starting, the server will be available at `http://localhost:3000`. The v4 API and the v5 APIs are located at `/pvwatts/v4.json` and `/pvwatts/v5.json`, respectively. Before trying to run any simulations, make sure to take a look at the [weather data](#weather-data) section.

You can see the documentation for those APIs [here (v4)](https://developer.nrel.gov/docs/solar/pvwatts-v4/) and [here (v5)](https://developer.nrel.gov/docs/solar/pvwatts-v5/).

### Library
The [web api](#web-api) uses the underlying `PvWatts4Service` and `PvWatts5Service` classes in order to run simulations. These services accept a `PvWattsXRequest` and return a `PvWattsXResponse` containing the inputs and outputs of each service. The requests and responses contain the same data as the [APIs](#web-api) linked above.

## Weather Data
**pvwatts-java** understands `.tm2` files and TMY3-formatted `.csv` files, but no weather data is included in the package. By default, **pvwatts-java** will look for TMY2 and TMY3 data in the `tmy2` and `tmy3` subdirectories of the directory specified in the `pvwatts.weatherDirectory` property, which is set in `configuration.properties`.

To get started, you can download common datasets from the following locations:

    * **TMY2** - [http://rredc.nrel.gov/solar/old_data/nsrdb/1961-1990/tmy2/]
      (http://rredc.nrel.gov/solar/old_data/nsrdb/1961-1990/tmy2/)
    * **TMY3** - [http://rredc.nrel.gov/solar/old_data/nsrdb/1991-2005/tmy3/]
      (http://rredc.nrel.gov/solar/old_data/nsrdb/1991-2005/tmy3/)

Simply download the entire archive of files and extract them to the `pvwatts.weatherDirectory` directory. **pvwatts-java** will find the files there.

