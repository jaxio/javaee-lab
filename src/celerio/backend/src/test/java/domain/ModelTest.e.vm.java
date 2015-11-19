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
$output.javaTest($entity.model)##

$output.require("java.io.*")##
$output.require("java.util.*")##
$output.requireStatic("org.fest.assertions.Assertions.assertThat")##
$output.require("org.junit.Test")##

/**
 * Basic tests for ${entity.model.type}
 */
@SuppressWarnings("unused")
public class $output.currentClass {
#if ($entity.root.hasSimplePk())

    // test unique primary key
    @Test
    public void newInstanceHasNoPrimaryKey() {
        $entity.model.type model = new ${entity.model.type}();
        assertThat(model.${identifiableProperty.iser}()).isFalse();
    }

    @Test
    public void ${identifiableProperty.iser}ReturnsTrue() {
        $entity.model.type model = new ${entity.model.type}();
        model.${entity.root.primaryKey.setter}(${entity.root.primaryKey.attribute.values.dummy});
        assertThat(model.${entity.root.primaryKey.getter}()).isNotNull();
        assertThat(model.${identifiableProperty.iser}()).isTrue();
    }
#end
#if ($entity.root.hasCompositePk())
    // test composite primary key

    @Test
    public void newInstanceHasNoPrimaryKey() {
        $entity.model.type model = new ${entity.model.type}();
        assertThat(model.${entity.root.primaryKey.getter}()).isNotNull();
        assertThat(model.${identifiableProperty.iser}()).isFalse();
    }

    @Test
    public void setEmptyCompositePrimaryKey() {
        $entity.model.type model = new ${entity.model.type}();
        $entity.root.primaryKey.type pk = new ${entity.root.primaryKey.type}();
        model.${entity.root.primaryKey.setter}(pk);
        assertThat(model.${identifiableProperty.iser}()).isFalse();
        assertThat(model.${entity.root.primaryKey.getter}()).isSameAs(pk);
    }

    @Test
    public void setValidCompositePrimaryKey() {
        $entity.model.type model = new ${entity.model.type}();
        $entity.root.primaryKey.type pk = new ${entity.root.primaryKey.type}();
#foreach ($pkAttribute in $entity.root.primaryKey.attributes)
        pk.${pkAttribute.setter}(${pkAttribute.values.dummy});
#end
        model.${entity.root.primaryKey.setter}(pk);

        assertThat(model.${identifiableProperty.iser}()).isTrue();
        assertThat(model.${entity.root.primaryKey.getter}()).isSameAs(pk);
    }
#end
#foreach ($relation in $entity.manyToOne.list)
$output.require($relation.to)##
#if ($velocityCount == 1)

    //-------------------------------------------------------------
    // Many to One:  $relation.toString()
    //-------------------------------------------------------------
#end

    @Test
    public void manyToOne_${relation.to.setter}() {
        ${entity.model.type} many = new ${entity.model.type}();

        // init
        $relation.to.type one = new ${relation.to.type}();
#if(!$relation.isIntermediate())
#foreach ($attributePair in $relation.attributePairs)
#if($relation.isComposite())
        one.${identifiableProperty.getter}().${attributePair.toAttribute.setter}(${attributePair.toAttribute.values.dummy});
#else
        one.${attributePair.toAttribute.setter}(${attributePair.toAttribute.values.dummy});
#end
#end
#end
        many.${relation.to.setter}(one);

        // make sure it is propagated properly
        assertThat(many.${relation.to.getter}()).isEqualTo(one);

        // now set it to back to null
        many.${relation.to.setter}(null);

        // make sure null is propagated properly
        assertThat(many.${relation.to.getter}()).isNull();
    }
#end
#foreach ($relation in $entity.oneToMany.list)
$output.require($relation.to)##
#if ($velocityCount == 1)

    //-------------------------------------------------------------
    // One to Many: $relation.toString()
    //-------------------------------------------------------------
#end

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // ${relation.toEntity.model.var}.$relation.to.var <-- ${relation.fromEntity.model.var}.$relation.from.vars
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    @Test
    public void oneToMany_${relation.to.adder}() {
        $entity.model.type one = new ${entity.model.type}();
        $relation.toEntity.model.type many = new ${relation.toEntity.model.type}();

        // init
        one.${relation.to.adder}(many);

        // make sure it is propagated
        assertThat(one.${relation.to.getters}()).contains(many);
        assertThat(one).isEqualTo(many.${relation.from.getter}());

        // now set it to null
        one.${relation.to.remover}(many);

        // make sure null is propagated
        assertThat(one.${relation.to.getters}().contains(many)).isFalse();
        assertThat(many.${relation.from.getter}()).isNull();
    }
#end
#foreach ($mm in $entity.manyToMany.list)
$output.require($mm.to)##
#if ($velocityCount == 1)

    //-------------------------------------------------------------
    // Pure Many to Many
    //-------------------------------------------------------------
#end

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
##    // $mm.fromAttribute.fullName <--- $mm.middleTable.name ---> $mm.toAttribute.fullName (Pure Many to Many)
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    @Test
    public void manyToMany_${mm.to.adder}() {
        ${entity.model.type} many1 = new ${entity.model.type}();
        $mm.toEntity.model.type many2 = new ${mm.toEntity.model.type}();

        // add it
        many1.${mm.to.adder}(many2);

        // check it is propagated
        assertThat(many1.${mm.to.getters}()).contains(many2);
#if ($mm.hasInverse())
        assertThat(many2.${mm.from.getters}()).contains(many1);
#end
        // now let's remove it
        many1.${mm.to.remover}(many2);

        // check it is propagated
        assertThat(many1.${mm.to.getters}().contains(many2)).isFalse();
#if ($mm.hasInverse())
        assertThat(many2.${mm.from.getters}().contains(many1)).isFalse();
#end
    }
#end

#if($entity.useBusinessKey())
    @Test
    public void equalsUsingBusinessKey() {
        $entity.model.type model1 = new ${entity.model.type}();
        $entity.model.type model2 = new ${entity.model.type}();
#foreach ($attribute in $entity.businessKey)
$output.require($attribute)##
        ${attribute.type} ${attribute.var} = ${attribute.values.dummy};
#if ($attribute.isSetterAccessibilityPublic())
        model1.${attribute.setter}(${attribute.var});
        model2.${attribute.setter}(${attribute.var});
#else
$output.require($attribute.xToOneRelation.to)##    
        ${attribute.xToOneRelation.to.type} ${attribute.xToOneRelation.to.var} = new ${attribute.xToOneRelation.to.type}();
#if (${attribute.xToOneRelation.toEntity.root.hasSimplePk()})
        ${attribute.xToOneRelation.to.var}.${identifiableProperty.setter}(${attribute.var});
#end
        model1.${attribute.xToOneRelation.to.setter}(${attribute.xToOneRelation.to.var});
        model2.${attribute.xToOneRelation.to.setter}(${attribute.xToOneRelation.to.var});
#end
#end
        assertThat(model1).isEqualTo(model2);
        assertThat(model2).isEqualTo(model1);
        assertThat(model1.hashCode()).isEqualTo(model2.hashCode());
    }
#else
/*
    public void equalsUsingPk() {
        $entity.model.type model1 = new ${entity.model.type}();
        $entity.model.type model2 = new ${entity.model.type}();
#foreach ($attribute in $entity.allAttributes.list)
#if ($attribute.isInPk())

        ${attribute.type} ${attribute.var} = ${attribute.values.dummy};
        model1.${attribute.setter}(${attribute.var});
        model2.${attribute.setter}(${attribute.var});
#elseif($attribute.isSetterAccessibilityPublic())

        model1.${attribute.setter}(${attribute.values.dummy});
        model2.${attribute.setter}(${attribute.values.dummy});
#end
#end
        assertThat(model1.${identifiableProperty.iser}()).isTrue();
        assertThat(model2.${identifiableProperty.iser}()).isTrue();
        assertThat(model1.hashCode()).isEqualTo(model2.hashCode());
        assertThat(model1).isEqualTo(model2);
        assertThat(model2).isEqualTo(model1);
    }
 */    
#end
}