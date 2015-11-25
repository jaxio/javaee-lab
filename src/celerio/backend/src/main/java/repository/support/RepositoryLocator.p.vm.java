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
$output.java($RepositorySupport, "RepositoryLocator")##

$output.require("javax.enterprise.context.ApplicationScoped")##
$output.require("javax.inject.Named")##
$output.require("javax.inject.Inject")##
$output.require("javax.annotation.PostConstruct")##
$output.require("java.io.Serializable")##
$output.require("java.util.Map")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.requireStatic("com.google.common.collect.Maps.newHashMap")##
$output.require("com.jaxio.jpa.querybyexample.GenericRepository")##

@Named
@ApplicationScoped
public class RepositoryLocator {
    private Map<String, GenericRepository<?, ?>> repositories = newHashMap();

#foreach ($entity in $project.withoutManyToManyJoinEntities.list)
$output.require($entity.repository)
    @Inject
    private $entity.repository.type $entity.repository.var;
#end

    @PostConstruct
    public void init() {
#foreach ($entity in $project.withoutManyToManyJoinEntities.list)
        repositories.put(${entity.repository.var}.getType().getSimpleName(), $entity.repository.var);
#end
    }

    @SuppressWarnings("unchecked")
    public <PK extends Serializable> GenericRepository<Identifiable<PK>, PK> getRepository(Identifiable<PK> entity) {
        // note: do not use entity.getClass() as it could be an hibernate proxy...
        return (GenericRepository<Identifiable<PK>, PK>) repositories.get(entity.entityClassName());
    }
}
