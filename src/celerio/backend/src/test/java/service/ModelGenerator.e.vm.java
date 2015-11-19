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
$output.javaTest($entity.modelGenerator, $entity.modelGenerator.type)##

$output.require($entity.model)##
#foreach ($enumAttribute in $entity.enumAttributes.flatUp.list)
$output.require($enumAttribute)##
#end

/**
 * Helper class to create transient entities instance for testing purposes.
 * Simple properties are pre-filled with random values.
 */
$output.dynamicAnnotationTakeOver("javax.inject.Named","javax.inject.Singleton")##
public class $output.currentClass {

    /**
     * Returns a new $entity.model.type instance filled with random values.
     */
    public $entity.model.type ${entity.model.getter}() {
        $entity.model.type $entity.model.var = new ${entity.model.type}();

#if ($entity.root.primaryKey.isComposite())
#foreach ($pkAttribute in $entity.root.primaryKey.attributes)
$output.require($Util, "ValueGenerator")##
$output.require($pkAttribute)##
#if (!$pkAttribute.isInFk())
$output.require($pkAttribute)##
        ${entity.model.var}.${identifiableProperty.getter}().${pkAttribute.setter}($pkAttribute.values.dummy);
#end
#end
#end
#if ($entity.root.hasSimplePk() && $entity.root.primaryKey.attribute.jpa.isManuallyAssigned())
$output.require($entity.root.primaryKey.attribute)##
        // primary key column must be set manually
$output.require($Util, "ValueGenerator")##
        ${entity.model.var}.${entity.root.primaryKey.attribute.setter}($entity.root.primaryKey.attribute.values.dummy);
#end
#foreach ($attribute in $entity.simpleAttributes.flatUp.list)
#if(!$attribute.isLocalDateOrTime())
$output.require($attribute)##
#end
#if($attribute.isUnique())
$output.require($Util, "ValueGenerator")##
#end
#if ($velocityCount == 1)
        // simple attributes follows
#end
        ${entity.model.var}.${attribute.setter}($attribute.values.dummy);
#end
#foreach ($relation in $entity.relations.flatUp.list)
#if ($relation.isMandatory() && !$relation.isInverse())
#if ($relation.toEntity.name != $entity.name)
$output.require($relation.toEntity.model)##
        // mandatory relation
        $relation.toEntity.model.type $relation.to.var = ${relation.toEntity.modelGenerator.var}.${relation.toEntity.model.getter}();
        ${relation.toEntity.repository.var}.save($relation.to.var);
        ${entity.model.var}.${relation.to.setter}($relation.to.var);
#end
#end
#end
        return $entity.model.var;
    }

#foreach ($relation in $entity.relations.flatUp.list)
#if ($relation.isMandatory() && !$relation.isInverse())
#if ($relation.toEntity.name != $entity.name)
$output.require("javax.inject.Inject")##
$output.require($relation.toEntity.repository, $relation.toEntity.modelGenerator)##
#if($output.requireFirstTime($relation.toEntity.repository))
    @Inject
    private $relation.toEntity.repository.type $relation.toEntity.repository.var;
    @Inject
    private $relation.toEntity.modelGenerator.type $relation.toEntity.modelGenerator.var;
#end
#end
#end
#end
}