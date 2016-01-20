## Copyright 2015 JAXIO http://www.jaxio.com
##
## Licensed under the Apache License, Version 2.0 (the "License");
## you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
##
##    http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
##
$output.java($WebModelSupport, "GenericController")##

$output.requireStatic("com.google.common.base.Throwables.propagate")##
$output.requireStatic("com.google.common.base.Preconditions.checkNotNull")##
$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.requireStatic("org.apache.commons.lang.StringUtils.isBlank")##
$output.require("java.io.Serializable")##
$output.require("java.util.List")##
$output.require("java.util.logging.Logger")##
$output.require("com.google.common.base.Splitter")##
$output.require("javax.faces.component.UIComponent")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.inject.Inject")##
$output.require("org.apache.commons.beanutils.PropertyUtils")##
$output.require("org.apache.commons.lang.WordUtils")##
$output.require("com.jaxio.jpa.querybyexample.JpaUniqueUtil")##
$output.require("com.jaxio.jpa.querybyexample.SearchParameters")##
#if($project.hibernateSearchUsed)
$output.require("com.jaxio.jpa.querybyexample.TermSelector")##
#end
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require($PrinterSupport, "GenericPrinter")##
$output.require("com.jaxio.jpa.querybyexample.GenericRepository")##
$output.require($WebUtil, "MessageUtil")##
$output.require($WebPermissionSupport, "GenericPermission")##

/**
 * Base controller for JPA entities providing helper methods to:
 * <ul>
 *  <li>navigate to select/edit view</li>
 *  <li>support autoComplete component</li>
 *  <li>perform actions</li>
 *  <li>support excel export</li>
 * </ul>
 */
public abstract class ${output.currentClass}<E extends Identifiable<PK>, PK extends Serializable> {
    private static final String PERMISSION_DENIED = "/error/accessdenied";
    private String selectUri;
    private String editUri;

    private transient Logger log = Logger.getLogger(${output.currentClass}.class.getName());
    @Inject
    protected JpaUniqueUtil jpaUniqueUtil;
    @Inject
    protected MessageUtil messageUtil;
#if($project.hibernateSearchUsed)
    @Inject
    protected MetamodelUtil metamodelUtil;
#end
    protected GenericRepository<E, PK> repository;
    protected GenericPermission<E> permission;
    protected GenericPrinter<E> printer;

    public void init(GenericRepository<E, PK> repository, GenericPermission<E> permission, GenericPrinter<E> printer, String selectUri, String editUri) {
        this.repository = checkNotNull(repository);
        this.permission = checkNotNull(permission);
        this.printer = checkNotNull(printer);
        this.selectUri = checkNotNull(selectUri);
        this.editUri = checkNotNull(editUri);
    }
    
    public GenericRepository<E, PK> getRepository() {
        return repository;
    }

    public PK convertToPrimaryKey(String pkAsString) {
        return repository.convertToPrimaryKey(pkAsString);
    }

    public GenericPermission<E> getPermission() {
        return permission;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }    

    public String select() {
        checkPermission(permission.canSelect());
        return selectUri + "?faces-redirect=true";
    }

    public String create() {
        checkPermission(permission.canCreate());
        return editUri + "?faces-redirect=true";
    }

    public String edit(E entity) {
        checkPermission(permission.canEdit(entity));        
        return editUri + "?faces-redirect=true&${identifiableProperty.var}=" + entity.${identifiableProperty.getter}().toString();        
    }

    public String view(E entity) {
        checkPermission(permission.canView(entity));        
        return editUri + "?faces-redirect=true&readonly=true&${identifiableProperty.var}=" + entity.${identifiableProperty.getter}().toString();        
    }

    public String print(E entity) {
        checkPermission(permission.canView(entity));        
        return editUri + "?faces-redirect=true&readonly=true&print=true&${identifiableProperty.var}=" + entity.${identifiableProperty.getter}().toString();        
    }

    protected void checkPermission(boolean check) {
        if (!check) {
            throw new IllegalStateException();
        }
    }    


    // ----------------------------------------
    // AUTO COMPLETE SUPPORT  
    // ----------------------------------------

    /**
     * Auto-complete support. This method is used by primefaces autoComplete component.
     */
    public List<E> complete(String value) {
        try {
            SearchParameters searchParameters = new SearchParameters() //
                .limitBroadSearch() //
                .distinct() //
                .orMode();
        E template = repository.getNew();
        for (String property : completeProperties()) {
#if($project.hibernateSearchUsed)
$output.require("com.jaxio.jpa.querybyexample.MetamodelUtil")##
            if (repository.isIndexed(property)) {
                    searchParameters.addTerm(new TermSelector(metamodelUtil.toAttribute(property, repository.getType())).selected(value));
            } else {
                PropertyUtils.setProperty(template, property, value);
            }
#else
            PropertyUtils.setProperty(template, property, value);
#end
        }
        return repository.find(template, searchParameters);
        } catch(Exception e) {
            log.warning("error during complete: " + e.getMessage());
            throw propagate(e);
        }
    }

    protected Iterable<String> completeProperties() {
        String completeOnProperties = parameter("completeOnProperties", String.class);
        return isBlank(completeOnProperties) ? printer.getDisplayedAttributes() : Splitter.on(";,").omitEmptyStrings().split(completeOnProperties);
    }

    public List<String> completeProperty(String value) {
        return completeProperty(value, parameter("property", String.class), parameter("maxResults", Integer.class));
    }

    public List<String> completeProperty(String value, String property) {
        return completeProperty(value, property, null);
    }

    public List<String> completeProperty(String toMatch, String property, Integer maxResults) {
#if($project.hibernateSearchUsed)
        List<String> values = newArrayList();
        if (repository.isIndexed(property)) {
            values.addAll(completePropertyUsingFullText(toMatch, property, maxResults));
        } else {
            values.addAll(completePropertyInDatabase(toMatch, property, maxResults));
        }
#else
        List<String> values = completePropertyInDatabase(toMatch, property, maxResults);
#end
        if (isBlank(toMatch) || values.contains(toMatch)) {
            // the term is already in the results, return them directly
            return values;
        } else {
            // add the term before the results as it is not part of the results
            List<String> retWithValue = newArrayList(toMatch);
            retWithValue.addAll(values);
            return retWithValue;
        }
    }
#if($project.hibernateSearchUsed)
$output.require("com.jaxio.jpa.querybyexample.MetamodelUtil")##

    protected List<String> completePropertyUsingFullText(String term, String property, Integer maxResults) {
        try {
            SearchParameters searchParameters = new SearchParameters().limitBroadSearch().distinct();
            searchParameters.addTerm(new TermSelector(metamodelUtil.toAttribute(property, repository.getType())).selected(term));
            if (maxResults != null) {
                searchParameters.setMaxResults(maxResults);
            }
            return repository.findProperty(String.class, repository.getNew(), searchParameters, property);
        } catch(Exception e) {
            log.warning("error during completePropertyUsingFullText: " + e.getMessage());
            throw propagate(e);
        }
    }
#end

    protected List<String> completePropertyInDatabase(String value, String property, Integer maxResults) {
        try {
            SearchParameters searchParameters = new SearchParameters() //
                .limitBroadSearch() //
                .caseInsensitive() //
                .anywhere() //
                .distinct();
            if (maxResults != null) {
                searchParameters.setMaxResults(maxResults);
            }
        E template = repository.getNew();
        PropertyUtils.setProperty(template, property, value);
            return repository.findProperty(String.class, template, searchParameters, property);
        } catch(Exception e) {
            log.warning("error during completePropertyInDatabase: " + e.getMessage());
            throw propagate(e);
        }
    }

    /**
     * A simple autoComplete that returns exactly the input. It is used in search forms with {@link PropertySelector}.
     */
    public List<String> completeSame(String value) {
        return newArrayList(value);
    }

    @SuppressWarnings("unchecked")
    protected <T> T parameter(String propertyName, Class<T> expectedType) {
        return (T) UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes().get(propertyName);
    }

    protected SearchParameters defaultOrder(SearchParameters searchParameters) {
        return searchParameters;
    }
}