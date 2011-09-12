# e-Navigation Enhanced INS #

## Introduction ##
   
ee-INS (e-Navigation Enhanced Integrated Navigation System) is an ECDIS like
application for demonstrating possible e-Navigation services.
   
The application is in Java and uses OpenMap(tm) for presenting geospatial
information and as a JavaBeans(tm) component framework.

TODO more

For detailed description see Wiki.

## Prerequisites ##

* JDK 1.6+ (http://java.sun.com/j2se/)
* Apache Ant 1.7+ (http://ant.apache.org) or Eclipse IDE (http://eclipse.org)

## Building ##

	ant

## Running ##

	ant run
	
## Javadoc ##

	ant javadoc
	
## Project structure ###

|-- build
|-- dist
|-- extlib
`-- src
    `-- main
        |-- java
        `-- resources

* `build` - generated directory with compiled class files
* `extlib` - third party jar files
* `src/main/java` - source root
* `src/main/resources` - Resources like images, default settings, etc.
* `dist` - a generated directory with a compiled distributable version of the application.
  The application is run from within this directory.   

## Versioning ##

The version is controlled in `build.xml` as a property. The convention is to
use the format `<major>.<minor>-<dev version>` for non-final versions, and 
`<major>.<minor>` for final releases. E.g.

	<property name="version" value="2.0-PRE1" />
	
for first pre-version of 2.0 and

	<property name="version" value="2.0" />
	
for the final version. 

## ChangeLog ##

TODO describe

## ENC layer ##

TODO describe how Navicon ENC layer can be incorporated

## Eclipse development ##

TODO import as project

## Contribution ##

TODO fork and make pull requests
