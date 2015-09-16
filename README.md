#Nuxeo Salesforce

# Projects

- Repository:
  - `nuxeo-salesforce-core`: Nuxeo plugin Core bringing access to Salesforce accounts.
  - `nuxeo-salesforce-web`: Nuxeo plugin Web providing UI components accessing Salesforce Objects.

## Building

`mvn clean install`

# Getting Started

- [Download a Nuxeo server](http://www.nuxeo.com/en/downloads) (the zip version)

- Unzip it

- Deploy `nuxeo-salesforce-core` and `nuxeo-salesforce-web` in `NUXEO_HOME/nxserver/bundles`

OR

- Install nuxeo-salesforce plugin from command line
  - Linux/Mac:
    - `NUXEO_HOME/bin/nuxeoctl mp-init`
    - `NUXEO_HOME/bin/nuxeoctl mp-install nuxeo-salesforce`
    - `NUXEO_HOME/bin/nuxeoctl start`
  - Windows:
    - `NUXEO_HOME\bin\nuxeoctl.bat mp-init`
    - `NUXEO_HOME\bin\nuxeoctl.bat mp-install nuxeo-salesforce`
    - `NUXEO_HOME\bin\nuxeoctl.bat start`

  or Install [the Nuxeo Salesforce Marketplace Package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/nuxeo-salesforce).

- From your browser, go to `http://localhost:8080/nuxeo`

- Follow Nuxeo Wizard by clicking 'Next' buttons, re-start once completed

- Check Nuxeo correctly re-started `http://localhost:8080/nuxeo`
  - username: Administrator
  - password: Administrator

- HTTPS configuration:

	Salesforce is requiring Nuxeo server to be accessed through HTTPS. Follow this [documentation](https://doc.nuxeo.com/x/GAFc) to configure your reverse proxy for production purpose. For a dev or test environment, you can configure your Nuxeo server in HTTPS directly with the following configuration parameters example:

		nuxeo.server.https.port=8443
		nuxeo.server.https.keystoreFile=/Users/vpasquier/.keystore
		nuxeo.server.https.keystorePass=******

	You can setup the keystore by following the [Oracle documentation](https://docs.oracle.com/cd/E19509-01/820-3503/ggfen/index.html)

- Add the following configuration parameter (in `NUXEO_HOME/bin/nuxeo.conf`):

		org.nuxeo.salesforce.consumer.key=YOUR_SALESFORCE_CONSUMER_KEY
		org.nuxeo.salesforce.callback.url=https://NUXEO_URL/nuxeo/picker/callback/callback.html

If you're using `Firefox` browser, you don't need to configure it for accessing the plugin within Salesforce. However with `chrome`, here are the guidelines to allow the access:

- Authorize `Popups` from Salesforce (to allow OAuth execution)
- Go to `https://localhost:8443/nuxeo` and allow `chrome` to access in HTTPS your Nuxeo server

In your Salesforce account, you can setup the Nuxeo Salesforce plugin through the Salesforce Marketplace 

OR directly from your Salesforce dashboard:

- Go in your Salesforce dashboard
- Go on `Setup` (top right)
- Go to `Build > Create > Apps`
- Add a new `Connected Apps` named Nuxeo
- Configure `OAuth` settings by referencing the callback URL: `https://NUXEO_URL/nuxeo/picker/callback/callback.html`
- Configure Canvas App URL `https://NUXEO_URL/nuxeo/picker` and select OAuth Webflow for Access Method
- Save the Nuxeo `Connected App`
- Go to `Customize > Any SF Object` like Opportunity
- Click on `Pages Layout > Edit SF Object Layout`
- Add Nuxeo `Canvas App` anywhere in the page
- Save

## QA results

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=addons_nuxeo-salesforce-master)](https://qa.nuxeo.org/jenkins/job/addons_nuxeo-salesforce-master/)

##Report & Contribute

We are glad to welcome new developers on this initiative, and even simple usage feedback is great.
- Ask your questions on [Nuxeo Answers](http://answers.nuxeo.com)
- Report issues on this GitHub repository (see [issues link](http://github.com/nuxeo/nuxeo-salesforce/issues) on the right)
- Contribute: Send pull requests!

#About
##Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at www.nuxeo.com.
