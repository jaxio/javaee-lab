# Celerio templates: full Java EE 7 app

>>
>> NOTE: This is still a work in progress, we are looking for feedbacks from Java EE 7 experts.
>> 

Reverse a sample SQL schema and generate a full S-CRUD Java EE 7 web application.

The code generation is done by [Celerio](http://www.jaxio.com/en/).

The project uses its own code generation templates, see [src/main/celerio](https://github.com/jaxio/javaee-lab/tree/master/src/celerio).

The sample SQL schema is here: [src/main/sql/h2/01-create.sql](https://github.com/jaxio/javaee-lab/tree/master/src/main/sql/h2/01-create.sql)

Note that this is still a work in progress, we are looking for feedbacks from Java EE 7 experts.

By S-CRUD we mean:

* **S**search
* **C**reate
* **R**read
* **U**update
* **D**delete

The generated application runs on WildFly 10, it is a pure Java EE 7 application:

* JPA with Hibernate
* Search with Hibernate Search / Lucene
* JSF 2.2
* Primefaces 5.3 / Omnifaces
* Shiro for authentication

It also relies on house-made solutions for:

* query by example
* JSF conversation

# Requirements

* Java 8
* Maven 3.1.1
* Latest WildFly version (currently 10.0.0-CR4)

## Step 1: start WildFly

From wildfly distribution root folder, run:

    ./bin/standalone.sh
    
## Step 2: reverse sample schema and generate the source
    
From this folder run from:

    mvn -Pdb,metadata,gen generate-sources

## Step 3: deploy on WildFly

    mvn wildfly:undeploy  <== if you deployed previously, undeploy it first

    mvn wildfly:deploy

## Step 4: access the app and play

    http://localhost:8080/demo

## Extra tips: delete generated code

    mvn -PcleanGen clean

# Contribute

You may contribute in several ways:

* By using the generated app and trying to find its limits
* By reviewing the generated code, is Java EE 7 properly used ?
* By trying to generate a project using your own database schema

You may of course [report issues](https://github.com/jaxio/javaee-lab/issues) and/or submit pull requests.

# Limitations

* Hibernate search not tested yet
* LocalDate not supported by PrimeFaces p:calendar, even with our converter!

