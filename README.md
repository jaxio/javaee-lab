
# Celerio templates: full Java EE 7 app

This is a work in progress...

To generate the source:

  mvn -Pdb,metadata,gen generate-sources

To run:

  mvn -Pdb,metadata,gen package embedded-glassfish:run

BEWARE, on macosx it create tmp files under /var/folders/mw/...






Below are old notes (need to rework):

DEPLOY ON A RUNNING GLASSFISH SERVER:
=====================================

  1/ deploy the datasource
     asadmin add-resources src/main/webapp/WEB-INF/glassfish-resources.xml

  2/ deploy the war archive
     asadmin deploy target/${configuration.applicationName}.war


KNOWN LIMITATIONS:
==================

* https://java.net/jira/browse/GLASSFISH-20775
  to circumvent it, remove the 'of' formatting date from the error.xhtml page

* In AuditLogListener (if you use audit)
  We had to add the following method to make it compile
	@Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        // TODO Auto-generated method stub
        return false;
    }

* https://hibernate.atlassian.net/browse/HSEARCH-1386 
  hibernate search is not compatible with an hibernate version supporting jpa 2.1
  To avoid it, simply generate a project that do not need hibernate search...

* Beware of this incompatibility between EL 3.0 and EL 2.2
  "The default coercion for nulls to non-primitive types (except String) returns 
   nulls. For instance, a null coerced to Boolean now returns a null, while a 
   null coerced to boolean returns false."
   => it has an impact in search.xhtml
   instead of 
   		<c:if test="#{not multiCheckboxSelection}">
   we now use        
   		<c:if test="#{empty multiCheckboxSelection or not multiCheckboxSelection}">


TO BE DONE WHEN TIME PERMITS:
=============================

Currently, in 99% part of the code we reuse our javaee6 codebase.
We have not modified places where we could leverage javaee7 for simplicity and lack of time reason.
Here is a non exhaustive list:

* LoginForm should use CDI ViewScope which is compatible with JSF 2.2
* register Hibernate Listener in a standard way is now possible
* use new xsd information for JSF 2.2 in components.xml and in all xhtml page 
  see for example http://jsflive.wordpress.com/2013/05/16/jsf22-namespaces/  
  
HELP:
=====

* To get info on plugin goals:
  mvn help:describe -Dplugin=embedded-glassfish

* Tip and tricks + examples:
  https://weblogs.java.net/blog/bhavanishankar/archive/2012/03/19/tips-tricks-and-troubleshooting-embedded-glassfish

