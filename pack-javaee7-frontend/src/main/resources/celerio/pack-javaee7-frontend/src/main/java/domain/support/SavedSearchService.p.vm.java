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
$output.generateIf($project.isSavedSearchPresent())##
$output.java($WebModelSupport, "${project.savedSearch.model.type}Service")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.require("java.util.List")##
$output.require("javax.inject.Inject")##
$output.require($project.savedSearch.model)##
$output.requireMetamodel($project.savedSearch.model)##
$output.require($project.savedSearch.repository)##
$output.require($project.accountEntity.model)##
$output.require($project.accountEntity.repository)##
$output.require($Context, "UserContext")##
$output.require("com.jaxio.jpa.querybyexample.SearchParameters")##
$output.require($WebModelSupport, "GenericSearchForm")##
$output.require($WebUtil, "MessageUtil")##

/**
 * Store/Load Search Form Content to db.
 */
$output.dynamicAnnotationTakeOver("javax.ejb.Singleton","javax.inject.Named")##
@SuppressWarnings({ "rawtypes" })
public class $output.currentClass {
    @Inject
    private $project.savedSearch.repository.type $project.savedSearch.repository.var;
    @Inject
    private MessageUtil messageUtil;
    @Inject
    private $project.accountEntity.repository.type $project.accountEntity.repository.var;

    public void save(GenericSearchForm searchForm) {
        $project.savedSearch.model.type $project.savedSearch.model.var = savedSearchRepository.findUniqueOrNone(example(searchForm));

        if ($project.savedSearch.model.var == null) {
            $project.savedSearch.model.var = example(searchForm);
        }

        if (searchForm.isPrivateSearch()) {
            $project.savedSearch.model.var = markSearchPrivate($project.savedSearch.model.var);
        } else {
            $project.savedSearch.model.var = markSearchPublic($project.savedSearch.model.var);
    }

        ${project.savedSearch.model.var}.setFormContent(searchForm.toByteArray());
        savedSearchRepository.save(${project.savedSearch.model.var});
        messageUtil.info("saved_search_saved", ${project.savedSearch.model.var}.getName());

        // manual search form reset : reset only form metadata 
        searchForm.setSearchFormName(null);
        searchForm.setPrivateSearch(false);
    }

    private ${project.savedSearch.model.type} example(GenericSearchForm searchForm) {
        ${project.savedSearch.model.type} ${project.savedSearch.model.var} = new ${project.savedSearch.model.type}();
        ${project.savedSearch.model.var}.setName(searchForm.getSearchFormName());
        ${project.savedSearch.model.var}.setFormClassname(searchForm.getClass().getSimpleName());
        return ${project.savedSearch.model.var};
        }

    /**
     * @param $project.savedSearch.model.var
     * @return $project.savedSearch.model.var marked as private with current $project.accountEntity.model.var
     */
    private ${project.savedSearch.model.type} markSearchPrivate($project.savedSearch.model.type $project.savedSearch.model.var) {
        $project.accountEntity.model.type currentUser = ${project.accountEntity.repository.var}.getById(UserContext.getId());
        ${project.savedSearch.model.var}.${project.accountEntity.model.setter}(currentUser);
        return ${project.savedSearch.model.var};
    }

    private $project.savedSearch.model.type markSearchPublic($project.savedSearch.model.type $project.savedSearch.model.var) {
        return ${project.savedSearch.model.var}.${project.accountEntity.model.var}(null);
    }

    private $project.savedSearch.model.type privateExample(GenericSearchForm searchForm) {
        return markSearchPrivate(example(searchForm));
    }

    public <F extends GenericSearchForm> Finder finderFor(F searchFrom) {
        // we use a Finder in order to have the required List<String> find(String) method
        return new Finder(savedSearchRepository, searchFrom);
    }

    public class Finder {
        private SavedSearchRepository savedSearchRepository;
        private GenericSearchForm searchFrom;

        public Finder(SavedSearchRepository savedSearchRepository, GenericSearchForm searchFrom) {
            this.savedSearchRepository = savedSearchRepository;
            this.searchFrom = searchFrom;
        }

        public List<${project.savedSearch.model.type}> find(String name) {
            List<${project.savedSearch.model.type}> searches = newArrayList();
            searches.addAll(findPrivate(name));
            searches.addAll(findPublic(name));
            return searches;
        }

        private List<${project.savedSearch.model.type}> findPublic(String name) {
            List<${project.savedSearch.model.type}> results = newArrayList();
            ${project.savedSearch.model.type} example = example(searchFrom).name(name);
			SearchParameters sp = new SearchParameters() //
    			.caseInsensitive() //
                .anywhere() //
                .property(${project.savedSearch.model.type}_.$project.accountEntity.model.var, (Object) null);
            for (${project.savedSearch.model.type} savedSearch : savedSearchRepository.find(example, sp)) {
                results.add(savedSearch);
            }
            return results;
    }
    
        private List<${project.savedSearch.model.type}> findPrivate(String name) {
            List<${project.savedSearch.model.type}> results = newArrayList();
            ${project.savedSearch.model.type} example = privateExample(searchFrom).name(name);
			SearchParameters anywhere = new SearchParameters() //
    			.caseInsensitive() //
                .anywhere();
            for (${project.savedSearch.model.type} savedSearch : savedSearchRepository.find(example	, anywhere)) {
                results.add(savedSearch);
    }
            return results;
        }
    }
}