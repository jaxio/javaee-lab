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
$output.java($entity.repository)##

#if ($entity.hasUniqueBigIntegerAttribute())
$output.require("java.math.BigInteger")##
#end
#if ($entity.hasUniqueDateAttribute() || $entity.root.hasDatePk())
$output.require("java.util.Date")##
#end
$output.require("com.jaxio.jpa.querybyexample.GenericRepository")##
$output.require($entity.model)##
$output.require($entity.root.primaryKey)##
#foreach ($enumAttribute in $entity.uniqueEnumAttributes.list)
$output.require($enumAttribute)##
#end

/**
 * {@link GenericRepository} for {@link $entity.model.type} 
 */
$output.dynamicAnnotationTakeOver("javax.ejb.Singleton","javax.inject.Named")##
public ${output.abstractSpace} class $output.currentClass extends GenericRepository<$entity.model.type, $entity.root.primaryKey.type> {

    public ${output.currentClass}() {
#if($entity.root.primaryKey.isSimple())
#if($entity.root.primaryKey.type == "Integer" || $entity.root.primaryKey.type == "Long")
        super(${entity.model.type}.class, ${entity.root.primaryKey.type}::valueOf);
#elseif($entity.root.primaryKey.type == "String")
        super(${entity.model.type}.class,);

#end
#else
        super(${entity.model.type}.class, ${entity.root.primaryKey.type}::fromString);
#end
    }

    @Override
    public $entity.model.type getNew() {
        return new ${entity.model.type}();
    }

    @Override
    public $entity.model.type getNewWithDefaults() {
        return getNew().withDefaults();
    }
#if ($entity.isView())

	@Override
    public void save(Iterable<$entity.model.type> $entity.model.vars) {
        throw new IllegalStateException("You cannot save : $entity.model.type is a view");
    }
#end
#foreach ($uniqueAttribute in $entity.uniqueAttributes.list)

    /**
     * Return the persistent instance of {@link $entity.model.type} with the given unique property value ${uniqueAttribute.var},
     * or null if there is no such persistent instance.
     *
     * @param $uniqueAttribute.var the unique value
     * @return the corresponding {@link $entity.model.type} persistent instance or null
     */
    public $entity.model.type ${uniqueAttribute.uniqueGetter}($uniqueAttribute.type ${uniqueAttribute.var}) {
#if($uniqueAttribute.isInCpk())
    $entity.model.type $entity.model.var = new ${entity.model.type}();
    ${entity.model.var}.${identifiableProperty.getter}().${uniqueAttribute.with}(${uniqueAttribute.var});
    return findUniqueOrNone(${entity.model.var});
#else
    return findUniqueOrNone(new ${entity.model.type}().${uniqueAttribute.with}(${uniqueAttribute.var}));
#end
    }

    /**
     * Delete a {@link $entity.model.type} using the unique column $uniqueAttribute.var
     *
     * @param $uniqueAttribute.var the unique value
     */
    public void ${uniqueAttribute.uniqueDeleter}($uniqueAttribute.type $uniqueAttribute.var) {
        delete(${uniqueAttribute.uniqueGetter}($uniqueAttribute.var));
    }
#end
#if($entity.fileAttributes.isNotEmpty())
#foreach ($attribute in $entity.fileAttributes.list)
$output.require("org.hibernate.LazyInitializationException")##

    /**
     * Safe way to load the ${attribute.var} content. 
     */
    public $attribute.type ${attribute.getter}($entity.model.type $entity.model.var) {
        if (!${entity.model.var}.${identifiableProperty.iser}()) {
            return ${entity.model.var}.${attribute.getter}();
        }

        try {
            return ${entity.model.var}.${attribute.getter}();
        } catch (LazyInitializationException lie) { // _HACK_ as we still rely on hibernate here
            return get($entity.model.var).${attribute.getter}();
        }
    }
#end
#end
}