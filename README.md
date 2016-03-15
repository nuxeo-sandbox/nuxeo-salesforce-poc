# About Nuxeo Salesforce POC

The **Nuxeo** addon [_nuxeo-salesforce_](https://github.com/nuxeo/nuxeo-salesforce) allows Salesforce users to attach documents to their Salesforce Objects (such as Opportunities, Contacts, Accounts...) through the Salesforce UI within a Nuxeo server.

This project started as a fork of that add-on but has greatly extended the scope of the SFDC integration.  The main additional features are:

* Prescribed content; the SFDC metadata is used to filter content coming from Nuxeo.
* Template support; generate templates from within SFDC, they are attached to the SFDC object on the back end.
* Share Nuxeo content from within SFDC.
* Content analytics; the time spend previewing a document is logged and displayed.
* Repository-wide search page; can be used as a separate tab within SFDC.

## Sub-Module Organization

- `nuxeo-salesforce-core`: Nuxeo plugin Core bringing access to Salesforce accounts.
- `nuxeo-salesforce-ui`: Nuxeo plugin UI for managing SFDC metadata on the back-end.
- `nuxeo-salesforce-web`: Nuxeo plugin Web providing UI components accessing Salesforce Objects.

## Building

`mvn clean install`

## Support

**These features are not part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.


## Licensing

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)


## About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.

More information is available at [www.nuxeo.com](http://www.nuxeo.com).
