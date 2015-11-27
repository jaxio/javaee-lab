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
$output.java($Search, "MassIndexerService")##

$output.requireStatic("org.hibernate.search.jpa.Search.getFullTextEntityManager")##
$output.require("java.util.Arrays")##
$output.require("java.util.logging.Logger")##
$output.require("javax.annotation.PostConstruct")##
$output.require("javax.inject.Inject")##
$output.require("javax.inject.Named")##
$output.require("javax.persistence.EntityManager")##
$output.require("javax.persistence.PersistenceContext")##
$output.require("org.apache.commons.lang.time.StopWatch")##
$output.require($Util,"IntConfig")##
#foreach($entity in $project.search.list)
$output.require($entity.model)##
#end

$output.dynamicAnnotationTakeOver("javax.ejb.Singleton","javax.inject.Named","javax.ejb.Startup")##
public class $output.currentClass {
	@Inject
    private Logger log;

    protected static Class<?>[] CLASSES_TO_BE_INDEXED = { //
#foreach($entity in $project.search.list)
			#if ($velocityCount > 1), #end${entity.model.type}.class //
#end
    };

    @PersistenceContext // inject would not work as we use it outside of a request scoped.
    protected EntityManager entityManager;

    @Inject
    @IntConfig(name="massIndexer.nbThreadsToLoadObjects", defaultValue = 1)
    protected int threadsToLoadObjects;

    @Inject
    @IntConfig(name="massIndexer.batchSizeToLoadObjects", defaultValue = 10)
    protected int batchSizeToLoadObjects;

    @Inject
    @IntConfig(name="massIndexer.nbThreadsForSubsequentFetching", defaultValue = 1)
    protected int threadsForSubsequentFetching;

    @PostConstruct
    public void createIndex() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
			for (Class<?> classToBeIndexed : CLASSES_TO_BE_INDEXED) {
				indexClass(classToBeIndexed);
			}
		} finally {
			stopWatch.stop();
			log.info("Indexed " + Arrays.toString(CLASSES_TO_BE_INDEXED) + " in " + stopWatch.toString());
		}
	}

	private void indexClass(Class<?> classToBeIndexed) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
            getFullTextEntityManager(entityManager) //
					.createIndexer(classToBeIndexed) //
//                    .batchSizeToLoadObjects(batchSizeToLoadObjects) //
//                    .threadsToLoadObjects(threadsToLoadObjects) //
					.startAndWait();
		} catch (InterruptedException e) {
			log.warning("Interrupted while indexing " + classToBeIndexed.getSimpleName());
			Thread.currentThread().interrupt();
        } finally {
            stopWatch.stop();
            log.info("Indexed " + classToBeIndexed.getSimpleName() + " in " + stopWatch.toString());
        }
    }
}
