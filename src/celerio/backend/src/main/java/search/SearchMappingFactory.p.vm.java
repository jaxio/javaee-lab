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
$output.skipIf($project.search.isEmpty())##
$output.java($Search, "SearchMappingFactory")##

$output.require("org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory")##
$output.require("org.apache.lucene.analysis.core.LowerCaseFilterFactory")##
$output.require("org.apache.lucene.analysis.ngram.NGramTokenizerFactory")##
$output.require("org.hibernate.search.annotations.Factory")##
$output.require("org.hibernate.search.cfg.SearchMapping")##

/**
 * This configuration is picked up by hibernate search using the <code>hibernate.search.model_mapping</code>
 * code in <code>/META-INF/persistence.xml</code>
 */
public class $output.currentClass {
    @Factory
    public SearchMapping getSearchMapping() {
        SearchMapping mapping = new SearchMapping();
        mapping.analyzerDef("custom", NGramTokenizerFactory.class).tokenizerParam("maxGramSize", "40") //
                .filter(ASCIIFoldingFilterFactory.class) //
                .filter(LowerCaseFilterFactory.class);
        return mapping;
    }
}