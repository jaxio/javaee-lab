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
$output.java($entity.searchForm)##

$output.require($entity.model)##
$output.require("com.jaxio.jpa.querybyexample.SearchParameters")##
$output.require($WebModelSupport, "GenericSearchForm")##
$output.require($entity.root.primaryKey)##

/**
 * View Helper to search {@link ${entity.model.type}}.
 * It exposes a {@link $entity.model.type} instance so it can be used in search by-example-query.
 */
$output.dynamicAnnotationTakeOver("javax.faces.view.ViewScoped", "javax.inject.Named")##
public class $output.currentClass extends GenericSearchForm<$entity.model.type, $entity.root.primaryKey.type, $output.currentRootClass> {
    private static final long serialVersionUID = 1L;
## HIBERNATE SEARCH
#if($entity.hibernateSearchAttributes.uniqueFlatUp.isNotEmpty())
#foreach ($attribute in $entity.hibernateSearchAttributes.uniqueFlatUp.list)
#if($velocityCount == 1)

    // full text search (applied first)
#end    
$output.requireMetamodel($attribute.entity.model)##
$output.require($attribute.entity.model)##
$output.require("com.jaxio.jpa.querybyexample.TermSelector")##
$output.require($attribute)##
    protected TermSelector ${attribute.var}TermSelector = new TermSelector(${attribute.entity.model.type}_.$attribute.var);
#end

    // classic search
#end
    protected $entity.model.type $entity.model.var = new ${entity.model.type}();
## RANGEABLE 
#foreach ($attribute in $entity.rangeableSearchAttributes.uniqueFlatUp.list)
$output.requireMetamodel($attribute.entity.model)##
$output.requireStatic("com.jaxio.jpa.querybyexample.Range.newRange")##
$output.require("com.jaxio.jpa.querybyexample.Range")##
$output.require($attribute.entity.model)##
$output.require($attribute.fullType)##
    protected Range<$attribute.entity.model.type, ${attribute.type}> ${attribute.var}Range = newRange(${attribute.entity.model.type}_.$attribute.var);
#end
## MULTI SELECTABLE
#foreach ($attribute in $entity.multiSelectableSearchAttributes.uniqueFlatUp.list)
$output.requireMetamodel($attribute.entity.model)##
$output.requireStatic("com.jaxio.jpa.querybyexample.PropertySelector.newPropertySelector")##
$output.require($attribute.entity.model)##
$output.require("com.jaxio.jpa.querybyexample.PropertySelector")##
$output.require($attribute)##
    protected PropertySelector<$attribute.entity.model.type, ${attribute.type}> ${attribute.var}Selector = newPropertySelector(${attribute.entity.model.type}_.$attribute.var);
#end
## X TO ONE
#foreach ($relation in $entity.xToOne.flatUp.list)
$output.require($relation.to)##
$output.requireStatic("com.jaxio.jpa.querybyexample.PropertySelector.newPropertySelector")##
$output.require("com.jaxio.jpa.querybyexample.PropertySelector")##
$output.require($relation.fromEntity.model)##
$output.requireMetamodel($relation.fromEntity.model)##
#if($relation.isIntermediate() || !$relation.fromAttribute.isInCpk() || $relation.isComposite())
$output.require($relation.toEntity.root.primaryKey)##
    protected PropertySelector<$relation.fromEntity.model.type, $relation.to.type> ${relation.to.var}Selector = newPropertySelector(${relation.fromEntity.model.type}_.$relation.to.var);
#else
$output.requireMetamodel($relation.fromEntity.root.primaryKey)##
    protected PropertySelector<$relation.fromEntity.model.type, $relation.to.type> ${relation.to.var}Selector = newPropertySelector(${relation.fromEntity.model.type}_.$entity.root.primaryKey.var, ${relation.fromEntity.root.primaryKey.type}_.$relation.fromAttribute.var);
#end
#end
## MANY TO MANY
#foreach ($relation in $entity.manyToMany.flatUp.list)
$output.requireMetamodel($relation.fromEntity.root.primaryKey)##
$output.requireMetamodel($relation.fromEntity.model)##
$output.requireStatic("com.jaxio.jpa.querybyexample.PropertySelector.newPropertySelector")##
$output.require("com.jaxio.jpa.querybyexample.PropertySelector")##
$output.require($relation.from.fullType)##
$output.require($relation.to.fullType)##
    protected PropertySelector<$relation.from.type, $relation.to.type> ${relation.to.vars}Selector = newPropertySelector(false, ${relation.from.type}_.${relation.to.vars});
#end

    public $entity.model.type ${entity.model.getter}() {
        return $entity.model.var;
    }

    @Override
    protected $entity.model.type getEntity() {
        return ${entity.model.getter}();
    }

    @Override
    public ${output.currentRootClass} newInstance() {
        return new ${output.currentRootClass}();
    }

    @Override
    public SearchParameters toSearchParameters() {
        SearchParameters sp = searchParameters();
#if($entity.hibernateSearchAttributes.uniqueFlatUp.isNotEmpty())
        sp.term(termsOnAll#foreach ($attribute in $entity.hibernateSearchAttributes.uniqueFlatUp.list), ${attribute.var}TermSelector#end);
#end
#if($entity.root.hasCompositePk())
        sp.distinct(false); // we must force to false to cirvumvent a bug in H2 or Hibernate. TODO: report the bug
#end    
#foreach ($attribute in $entity.searchResultAttributes.uniqueFlatUp.list)
#if ($attribute.hasXToOneRelation())
$output.requireMetamodel($entity.model)##
        sp.fetch(${entity.model.type}_.${attribute.xToOneRelation.to.var});
#end
#end
#if($entity.rangeableSearchAttributes.uniqueFlatUp.isNotEmpty())
        sp.range(#foreach ($attribute in $entity.rangeableSearchAttributes.uniqueFlatUp.list)${attribute.var}Range$project.print($velocityHasNext,", ", ");")#end
#end
#if($entity.multiSelectableSearchAttributes.uniqueFlatUp.isNotEmpty())
        sp.property(#foreach ($attribute in $entity.multiSelectableSearchAttributes.uniqueFlatUp.list)${attribute.var}Selector$project.print($velocityHasNext,", ", ");")#end
#end
#if($entity.xToOne.flatUp.isNotEmpty())
        sp.property(#foreach ($relation in $entity.xToOne.flatUp.list)${relation.to.var}Selector$project.print($velocityHasNext,", ", ");")#end
#end
#if($entity.manyToMany.flatUp.isNotEmpty())
        sp.property(#foreach ($relation in $entity.manyToMany.flatUp.list)${relation.to.vars}Selector$project.print($velocityHasNext,", ", ");")#end
#end
        return sp;
    }

    @Override
    public void resetWithOther(${output.currentRootClass} other) {
        this.$entity.model.var = other.${entity.model.getter}();
#if($entity.hibernateSearchAttributes.uniqueFlatUp.isNotEmpty())
        this.termsOnAll = other.getTermsOnAll();
#foreach ($attribute in $entity.hibernateSearchAttributes.uniqueFlatUp.list)
        this.${attribute.var}TermSelector = other.${attribute.getter}TermSelector();
#end
#end
#foreach ($attribute in $entity.rangeableSearchAttributes.uniqueFlatUp.list)
        this.${attribute.var}Range = other.${attribute.getter}Range();
#end
#foreach ($attribute in $entity.multiSelectableSearchAttributes.uniqueFlatUp.list)
        this.${attribute.var}Selector = other.${attribute.getter}Selector();
#end
#foreach ($relation in $entity.xToOne.flatUp.list)
        this.${relation.to.var}Selector = other.${relation.to.getter}Selector();
#end
#foreach ($relation in $entity.manyToMany.flatUp.list)
        this.${relation.to.vars}Selector = other.${relation.to.getters}Selector();
#end
    }

#foreach ($attribute in $entity.hibernateSearchAttributes.uniqueFlatUp.list)
#if ($velocityCount == 1)

    // Term selectors    
#end
    public TermSelector ${attribute.getter}TermSelector() {
        return ${attribute.var}TermSelector;
    }
#end
#foreach ($attribute in $entity.rangeableSearchAttributes.uniqueFlatUp.list)
#if ($velocityCount == 1)

    // Ranges
#end
    public Range<$attribute.entity.model.type, ${attribute.type}> ${attribute.getter}Range() {
        return ${attribute.var}Range;
    }
#end
#foreach ($attribute in $entity.multiSelectableSearchAttributes.uniqueFlatUp.list)
#if ($velocityCount == 1)

    // Property selectors
#end
    public PropertySelector<$attribute.entity.model.type, $attribute.type> ${attribute.getter}Selector() {
        return ${attribute.var}Selector;
    }
#end
#foreach ($relation in $entity.xToOne.flatUp.list)
#if ($velocityCount == 1)

    // Relation selectors
#end
    public PropertySelector<$relation.fromEntity.model.type, $relation.to.type> ${relation.to.getter}Selector() {
        return ${relation.to.var}Selector;
    }
#end
#foreach ($relation in $entity.manyToMany.flatUp.list)
#if ($velocityCount == 1)

    // Relation selectors
#end
    public PropertySelector<$relation.from.type, $relation.to.type> ${relation.to.getters}Selector() {
        return ${relation.to.vars}Selector;
    }
#end
}
