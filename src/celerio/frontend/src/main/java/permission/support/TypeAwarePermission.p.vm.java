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
$output.java($WebPermissionSupport, "TypeAwarePermission")##

$output.requireStatic("com.google.common.collect.Maps.newHashMap")##
$output.requireStatic("org.hibernate.proxy.HibernateProxyHelper.getClassWithoutInitializingProxy")##
$output.require("java.io.Serializable")##
$output.require("java.util.Map")##
$output.require("javax.enterprise.inject.Instance")##
$output.require("javax.inject.Inject")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##

/**
 * Permission service that should be used only in certain cases (e.g. from facelet tags). 
 * 
 * @see GenericPermission
 */
$output.dynamicAnnotationTakeOver('javax.inject.Named("permission")')##
@SuppressWarnings("rawtypes")
public class ${output.currentClass} {
    private Map<Class, GenericPermission<?>> permissions = newHashMap();

    @Inject
    void buildCache(Instance<GenericPermission<?>> registredPermissions) {
        for (GenericPermission<?> permission : registredPermissions) {
            permissions.put(permission.getTarget(), permission);
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends Identifiable<? extends Serializable>> GenericPermission<E> getPermission(E entity) {
        // note: getClassWithoutInitializingProxy expects a non null object
        // _HACK_ as we depend on hibernate here.
        return (GenericPermission<E>) permissions.get(getClassWithoutInitializingProxy(entity));
    }
    
    // --------------------------------------------------------------
    // Permission shortcut methods that can be used from facelet tags
    // --------------------------------------------------------------
    
    public <E extends Identifiable<?>> boolean canView(E e) {
        return e == null ? false : getPermission(e).canView(e);
    }

    public <E extends Identifiable<?>> boolean canEdit(E e) {
        return e == null ? false : getPermission(e).canEdit(e);
    }

    public <E extends Identifiable<?>> boolean canDelete(E e) {
        return e == null ? false : getPermission(e).canDelete(e);
    }

    public <E extends Identifiable<?>> boolean canSearch(E e) {
        return e == null ? false : getPermission(e).canSearch(e);
    }

    public <E extends Identifiable<?>> boolean canSelect(E e) {
        return e == null ? false : getPermission(e).canSelect(e);
    }

    public <E extends Identifiable<?>> boolean canUse(E e) {
        return e == null ? false : getPermission(e).canUse(e);
    }
}