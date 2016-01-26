# Java EE 7 Celerio Templates (demo for dev)

This demo project is convenient when developing the Java EE 7 code generation templates.

It reverses this [sample SQL schema](https://github.com/jaxio/javaee-lab/tree/master/src/main/sql/h2/02-create.sql) and
uses directly (not through jars) the following templates:

* [pack-javaee7-backend](https://github.com/jaxio/javaee-lab/tree/master/pack-javaee7-backend).
* [pack-javaee7-frontend](https://github.com/jaxio/javaee-lab/tree/master/pack-javaee7-frontend).
* [pack-javaee7-frontend-conversation](https://github.com/jaxio/javaee-lab/tree/master/pack-javaee7-frontend-conversation) - disabled by default
* [pack-javaee7-wildfly](https://github.com/jaxio/javaee-lab/tree/master/pack-javaee7-wildfly).

You may edit the file [celerio-template-packs.xml](https://github.com/jaxio/javaee-lab/tree/master/demo/src/main/config/celerio-maven-plugin/celerio-template-packs.xml) 
to choose between a **conversation-based front end or a simpler front end version**. By default, the simpler front end is enabled.

The generated application depends on the following library, which for convenience is part of the JavaEE Lab github project.

* [javaee7-jpa-query-by-example](https://github.com/jaxio/javaee-lab/tree/master/javaee7-jpa-query-by-example).
 
The generated application runs on WildFly 10, it is a pure Java EE 7 application, it relies on:

* JPA with Hibernate
* Search with Hibernate Search / Lucene
* JSF 2.2
* Primefaces 5.3 / Omnifaces 2.1
* Shiro for authentication

It also relies on house-made solution for:

* JSF conversation (depending on if you choose this front-end version or not)

# Requirements

* Java 8
* Maven 3.1.1
* Latest [WildFly](http://wildfly.org/downloads/) version (we currently use 10.0.0-CR4)

# How to run it

## Step 1: start WildFly

From wildfly distribution root folder, run:

    ./bin/standalone.sh
    
## Step 2: reverse the sample SQL schema and generate the source code
    
From this folder run:

    cd demo
    mvn -Pdb,metadata,gen generate-sources

## Step 3: deploy on WildFly

From the demo folder:

    mvn wildfly:undeploy  <== if you deployed previously, undeploy it first

    mvn wildfly:deploy

## Step 4: access the app and play

    http://localhost:8080/demo

## Extra tip: delete generated code

From the demo folder:

    mvn -PcleanGen clean

# Contribute

You may contribute in several ways:

* By using the generated app and trying to find its limits
* By reviewing the generated code, is Java EE 7 properly used ?
* By trying to generate a project using your own database schema

You may of course [report issues](https://github.com/jaxio/javaee-lab/issues) and/or submit pull requests.

# Limitations

* Cache does not seem to work (ehcache)
* LocalDate not supported by PrimeFaces p:calendar, even with our converter!

