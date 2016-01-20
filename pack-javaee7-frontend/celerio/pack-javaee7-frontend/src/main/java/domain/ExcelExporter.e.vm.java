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
$output.java($entity.excelExporter)##

$output.require("javax.inject.Inject")##
$output.require($entity.model)##
$output.require($WebModelSupport, "GenericExcelExporter")##

/**
 * Exports to excel document {@link ${entity.model.type}} search criteria and result. 
 */
$output.dynamicAnnotationTakeOver("javax.faces.view.ViewScoped", "javax.inject.Named")##
public class $output.currentClass extends GenericExcelExporter<$entity.model.type> {
    private static final long serialVersionUID = 1L;

    @Inject
    protected transient $entity.searchForm.type sf;
    
    public ${output.currentClass}() {
        super(#{foreach}($attribute in $entity.searchResultAttributes.flatUp.list)#{if}($attribute.hasXToOneRelation())"$attribute.xToOneRelation.labelName"#{else}"$attribute.labelName"#{end}$project.print($velocityHasNext, ", ")#{end});
    }

    @Override
    protected void fillResultItem(int row, $entity.model.type item) {
        int col = 0;
#foreach ($attribute in $entity.searchResultAttributes.flatUp.list)
#if ($attribute.hasXToOneRelation())
        setEntity(row, col++, item.${attribute.xToOneRelation.to.getter}());
#else
#if($attribute.isInCpk() && !$attribute.isInFk())
        setValue(row, col++, item.${identifiableProperty.getter}().${attribute.getter}());
#elseif ($attribute.isJavaUtilOnlyTime())
        setTime(row, col++, item.${attribute.getter}());
#elseif ($attribute.isJavaUtilDateAndTime())
        setDateTime(row, col++, item.${attribute.getter}());
#else
        setValue(row, col++, item.${attribute.getter}());
#end
#end
#end
    }
    
    @Override
    public void fillSearchCriteria(int row) {
        useCriteriaSheet();
#if($entity.hibernateSearchAttributes.flatUp.size > 0)
#if($entity.hibernateSearchAttributes.flatUp.size > 1)
$output.requireStatic("org.apache.commons.lang.StringUtils.join")##
        setLeftHeader(row, 0, "search_full_text");
        setValue(row++, 1, join(sf.getTermsOnAll().getSelected(), ' '));
#end
#foreach ($attribute in $entity.hibernateSearchAttributes.uniqueFlatUp.list)
        setTermSelector(row++, 0, "$attribute.labelName", sf.${attribute.getter}TermSelector());
#end
#end

#foreach ($attribute in $entity.searchAttributes.flatUp.list)
#if ($attribute.isBlob())
##          nothing for blobs
#elseif($attribute.isSimpleFk())
        setSelectedEntities(row++, 0, "$attribute.xToOneRelation.labelName", sf.${attribute.xToOneRelation.to.getter}Selector().getSelected()); 
#elseif($attribute.isInCpk() && !$attribute.isInFk())
// todo
#elseif ($attribute.isEnum() || $attribute.isBoolean() || $attribute.isString())
        setSelector(row++, 0, "$attribute.labelName", sf.${attribute.getter}Selector()); 
#elseif ($attribute.isNumeric())
#if($attribute.isRangeable())
        setRangeNumber(row++, 0, "$attribute.labelName", sf.${attribute.getter}Range());
#else
        setLeftHeader(row, 0, "$attribute.labelName");
        setValue(row++, 1, sf.${entity.model.getter}().${attribute.getter}());
#end
#elseif ($attribute.isDate() && !$attribute.isVersion())
#if($attribute.isJavaUtilOnlyDate())
#if($attribute.isRangeable())
        setRangeDate(row++, 0, "$attribute.labelName", sf.${attribute.getter}Range()); 
#else
        setLeftHeader(row, 0, "$attribute.labelName");
        setValue(row++, 1, sf.${entity.model.getter}().${attribute.getter}());
#end
#elseif($attribute.isJavaUtilDateAndTime())
#if($attribute.isRangeable())
        setRangeDateTime(row++, 0, "$attribute.labelName", sf.${attribute.getter}Range());
#else
        setLeftHeader(row, 0, "$attribute.labelName");
        setDateTime(row++, 1, sf.${entity.model.getter}().${attribute.getter}());
#end
#elseif($attribute.isLocalDate())
#if($attribute.isRangeable())
        setRangeLocalDate(row++, 0, "$attribute.labelName", sf.${attribute.getter}Range());
#else
        setLeftHeader(row, 0, "$attribute.labelName");
        setValue(row++, 1, sf.${entity.model.getter}().${attribute.getter}());
#end
#elseif($attribute.isLocalDateTime())
#if($attribute.isRangeable())
        setRangeLocalDateTime(row++, 0, "$attribute.labelName", sf.${attribute.getter}Range());
#else
        setLeftHeader(row, 0, "$attribute.labelName");
        setValue(row++, 1, sf.${entity.model.getter}().${attribute.getter}());
#end
#else
        // date type not supported            
#end
#else
#if($attribute.isPassword())
        // password is not exported 
#elseif(!$attribute.isInCpk())
        setSelector(row++, 0, "$attribute.labelName", sf.${attribute.getter}Selector()); 
#else
    setLeftHeader(row, 0, "$attribute.labelName");
    setValue(row++, 1, sf.${entity.model.getter}().${identifiableProperty.getter}().${attribute.getter}());
#end
#end
#end
## =====================================================
## COMPOSITE X TO ONE
## =====================================================
#foreach ($relation in $entity.compositeXToOne.flatUp.list)
        setSelectedEntities(row++, 0, "$relation.labelName", sf.${relation.to.getter}Selector().getSelected()); 
#end
## =====================================================
## MANY TO MANY
## =====================================================
#foreach ($relation in $entity.manyToMany.flatUp.list)
        setSelectedEntities(row++, 0, "$relation.labelName", sf.${entity.model.getter}().${relation.to.getters}()); 
#end
    }
}