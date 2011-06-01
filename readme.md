Valet is a simple Java API to interact AWS's [Route53](http://aws.amazon.com/route53/) DNS service.

The AWS Java SDK does not currently support Route53; this simple binding may help if you want to use Java to modify Route53 zone data.

View [ValetExample](src/main/java/com/widen/examples/ValetExample.java) for typical usage of Valet API.

Includes [automation classes](src/main/java/com/widen/valet/importer) to import/one-way-sync existing Windows DNS server files.

To run the example [ZoneSummary](src/main/java/com/widen/examples/ZoneSummary.java) application using Gradle

    gradle zoneSummary -Paws-access-key=MY_AWS_ACCESS_KEY -Paws-secret-key=MY_AWS_SECRET_KEY

Available via Maven:

      server: http://widen.artifactoryonline.com/widen/libs-widen-public
       group: widen
    artifact: valet
     version: 0.2

Or browse the repo directly at [https://widen.artifactoryonline.com/widen/libs-widen-public](https://widen.artifactoryonline.com/widen/libs-widen-public/widen/valet/)

Change Log:

	0.2 - Weighted Round Robin support
	      Delete zone implemented

    0.1 - Initial version

Contact Uriah Carpenter (uriah at widen.com) with questions.

Licensed under Apache, Version 2.0.