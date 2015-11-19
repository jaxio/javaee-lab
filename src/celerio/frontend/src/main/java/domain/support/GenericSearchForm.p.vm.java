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
$output.java($WebModelSupport, "GenericSearchForm")##
$output.require("java.io.Serializable")##
$output.require("javax.inject.Inject")##
$output.require("javax.annotation.PostConstruct")##
$output.require("com.jaxio.jpa.querybyexample.SearchParameters")##
#if($project.hibernateSearchUsed)
$output.require("com.jaxio.jpa.querybyexample.TermSelector")##
#end
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require($WebConversation, "ConversationBean")##
$output.require($WebConversation, "ConversationContext")##
$output.require($WebConversation, "ConversationManager")##
$output.require($WebUtil, "MessageUtil")##

/**
 * Base Search Form for JPA entities.
 */
public abstract class ${output.currentClass}<E extends Identifiable<PK>, PK extends Serializable, F extends ${output.currentClass}<E, PK, F>> extends CommonAction<E> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private transient ConversationManager conversationManager;
    @Inject
    private transient MessageUtil messageUtil;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        ConversationBean currentConversation = conversationManager.getCurrentConversation();
        if (currentConversation == null) {
            return;
        }
//
//        ConversationContext<?> currentContext = currentConversation.getCurrentContext();
//        if (currentContext != null && currentContext.getBean(getPrefilledFormName(), getClass()) != null) {
//            resetWithOther((F) currentContext.getBean(getPrefilledFormName(), getClass()));
//        }
    }

    public String getPrefilledFormName(){
        return "_" + this.getClass().getName() + "_prefilled_" ;
    }

    /**
     * Return the entity example used in this search form.
     */
    protected abstract E getEntity();
#if($project.isSavedSearchPresent())
$output.require($project.savedSearch.model)##        

    /**
     * Always null as we use the setter only to add element.
     */
    public SavedSearch getCurrentSavedSearch() {
        return null;
    }
    
    public void setCurrentSavedSearch(${project.savedSearch.model.type} $project.savedSearch.model.var) {
        if ($project.savedSearch.model.var != null) {
            messageUtil.info("saved_search_loaded", ${project.savedSearch.model.var}.getName());
            resetWithOther(fromByteArray(${project.savedSearch.model.var}.getFormContent()));
        }
    }
    
    protected String searchFormName;

    public String getSearchFormName() {
        return searchFormName;
    }

    public void setSearchFormName(String searchFormName) {
        this.searchFormName = searchFormName;
    }

    private boolean privateSearch;

    public boolean isPrivateSearch() {        
        return privateSearch;
    }         

    public void setPrivateSearch(boolean privateSearch) {         
        this.privateSearch = privateSearch;
    }          
#end
#if($project.hibernateSearchUsed) 

    protected TermSelector termsOnAll = new TermSelector();

    public TermSelector getTermsOnAll() {
        return termsOnAll;
    }
#end
    /**
     * Convert all the search inputs into a @{link SearchParameters}. 
     */
    public abstract SearchParameters toSearchParameters();

    /**
     * default search parameters
     */
    public SearchParameters searchParameters() {
        return new SearchParameters() //
            .limitBroadSearch() //
            .distinct() //
            .anywhere() //
            .caseInsensitive();
    }

    // Reset related

    public abstract F newInstance();

    public abstract void resetWithOther(F other);

    public void reset() {
        messageUtil.info("search_reseted");
#if($project.isSavedSearchPresent())
        setSearchFormName(null);
#end
        resetWithOther(newInstance());
    }
#if($project.isSavedSearchPresent())
$output.requireStatic("com.google.common.base.Throwables.propagate")##
$output.requireStatic("org.apache.commons.io.IOUtils.closeQuietly")##
$output.require("java.io.ByteArrayInputStream")##
$output.require("java.io.ByteArrayOutputStream")##
$output.require("java.io.ObjectInputStream")##
$output.require("java.io.ObjectOutputStream")##
$output.require("java.io.Serializable")##

    protected byte[] toByteArray() {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw propagate(e);
        } finally {
            closeQuietly(oos);
            closeQuietly(baos);
        }
    }

    @SuppressWarnings("unchecked")
    protected F fromByteArray(byte[] bytes) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (F) ois.readObject();
        } catch (Exception e) {
            throw propagate(e);
        } finally {
            closeQuietly(ois);
        }
    }
#end
}
