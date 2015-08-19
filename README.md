#Nuxeo Salesforce (Work in progress)

# Projects

- Tree
  - `nuxeo-for-salesforce`: Salesforce plugin bringing access to Nuxeo instances
  - `nuxeo-salesforce-core`: Nuxeo plugin Core bringing access to Salesforce accounts
  - `nuxeo-salesforce-web`: Nuxeo plugin Web providing UI components for Salesforce plugin and views for Nuxeo plugin

# Getting Started

- [Download a Nuxeo server](http://www.nuxeo.com/en/downloads) (the zip version)

- Unzip it

- Install nuxeo-salesforce plugin from command line
  - Linux/Mac:
    - `NUXEO_HOME/bin/nuxeoctl mp-init`
    - `NUXEO_HOME/bin/nuxeoctl mp-install nuxeo-salesforce`
    - `NUXEO_HOME/bin/nuxeoctl start`
  - Windows:
    - `NUXEO_HOME\bin\nuxeoctl.bat mp-init`
    - `NUXEO_HOME\bin\nuxeoctl.bat mp-install nuxeo-salesforce`
    - `NUXEO_HOME\bin\nuxeoctl.bat start`

- From your browser, go to `http://localhost:8080/nuxeo`

- Follow Nuxeo Wizard by clicking 'Next' buttons, re-start once completed

- Check Nuxeo correctly re-started `http://localhost:8080/nuxeo`
  - username: Administrator
  - password: Administrator

- You can now use the addon.


Note: Your machine needs internet access. If you have a proxy setting, skip the mp-init and mp-install steps at first, just do nuxeoctl start and run the wizzard where you will be asked your proxy settings.

## Building

    mvn clean install

## Deploying

Install [the Nuxeo Salesforce Marketplace Package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/nuxeo-salesforce).

Or manually copy the built artifacts into `$NUXEO_HOME/templates/custom/bundles/` and activate the "custom" template.

## Configuring

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
