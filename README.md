# BigchainDB Android Boilerplate

Basic Android boilerplate app which wraps text in an asset and performs CREATE transaction in BigchainDB. This app is created using [Java driver for BigchainDB](https://github.com/bigchaindb/java-bigchaindb-driver).

Android app developers can use this boilerplate as a starting point for developing BigchainDB based apps. It showcases how to use the BigchainDB Java Driver in Android apps.

## Pre-requisites

* BigchainDB node running locally. If you don't already have a BigchainDB node running locally, you can set it up by following the instructions [here](https://docs.bigchaindb.com/projects/contributing/en/latest/dev-setup-coding-and-contribution-process/run-node-with-docker-compose.html#setting-up-a-single-node-development-environment-with-docker-compose).

* Android Studio
* JDK 8 (or later)
* Android SDK (api v23 or later)

## Setup

* Clone this repo using `git clone https://github.com/bigchaindb/android-boilerplate.git`.
* Open this repo in Android Studio.
* Build this project and Build apk.
* Click Run app using any AVD (>= api 23)

## Usage

The boilerplate is based on the [Getting Started page on BigchainDB website](https://www.bigchaindb.com/developers/getstarted/). 

Once you have the local node and this app running, just enter some text in the text box and tap/click **Send Transaction**. The text will be wrapped in a BigchainDB transaction and will be posted to the local node, using the BigchainDB Java Driver. The response with full transaction object will be shown in the space below the button.

![](bigchaindb-android-app.gif)
