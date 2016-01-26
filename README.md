## Celerio Generation Template Packs: Java EE 7 web application

These Celerio Generation Template Packs contain some source code templates that
are interpreted by Celerio code generator in order to generate a full S-CRUD Java EE 7 web application.

* [pack-javaee7-backend](https://github.com/jaxio/javaee-lab/tree/master/pack-javaee7-backend)
* [pack-javaee7-frontend](https://github.com/jaxio/javaee-lab/tree/master/pack-javaee7-frontend)
* [pack-javaee7-frontend-conversation](https://github.com/jaxio/javaee-lab/tree/master/pack-javaee7-frontend-conversation) - disabled by default
* [pack-javaee7-wildfly](https://github.com/jaxio/javaee-lab/tree/master/pack-javaee7-wildfly)

S-CRUD means: **S**earch, **C**reate, **R**ead, **U**pdate, **D**elete

The generated application runs on WildFly 10, it is a pure Java EE 7 application, it relies on:

* JPA with Hibernate
* Search with Hibernate Search / Lucene
* JSF 2.2
* Primefaces 5.3 / Omnifaces 2.1
* Shiro for authentication

## Requirements

* Java 8
* Maven 3.1.1
* Latest [WildFly](http://wildfly.org/downloads/) version (we currently use 10.0.0-CR4)

## Generate a Java EE 7 web application

These packs is part of Celerio distribution.

Have already Maven 3 and Java 1.8 installed ?

To generate an application from these packs simply execute:

    mvn com.jaxio.celerio:bootstrap-maven-plugin:4.0.4:bootstrap

Please refer to [Celerio Documentation](http://www.jaxio.com/documentation/celerio) for more details on Celerio.

Once generated, to deploy it on WildFly 10, assuming it is running on localhost, execute from your generated app root folder:

    mvn wildfly:deploy

## Contribute

You may contribute in several ways:

* By using the generated app and trying to find its limits
* By reviewing the generated code, is Java EE 7 properly used ?
* By trying to generate a project using your own database schema

You may of course [report issues](https://github.com/jaxio/javaee-lab/issues) and/or submit pull requests.

## Limitations

* Cache does not seem to work (ehcache)
* LocalDate not supported by PrimeFaces p:calendar, even with our converter!
