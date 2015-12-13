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
$output.java($WebPermissionSupport, "GenericPermission")##

$output.require("java.io.Serializable")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##

public abstract class ${output.currentClass}<E extends Identifiable<? extends Serializable>> {
    private Class<E> clazz;

    public void init(Class<E> clazz) {
        this.clazz = clazz;
    }

    public Class<E> getTarget() {
        return clazz;
    }

    public boolean canCreate() {
        return true;
    }

    public boolean canView() {
        return true;
    }

    public boolean canView(E e) {
        return e == null ? false : canView();
    }

    public boolean canEdit() {
        return true;
    }

    public boolean canEdit(E e) {
        return e == null ? false : canEdit();
    }

    public boolean canDelete() {
        return true;
    }

    public boolean canDelete(E e) {
        return canDelete();
    }

    public boolean canSearch() {
        return canView();
    }

    public boolean canSearch(E e) {
        return canSearch();
    }

    public boolean canSelect() {
        return canSearch();
    }

    public boolean canSelect(E e) {
        return canSelect();
    }
    
    public boolean canUse() {
        return canView() || canSelect() || canDelete() || canEdit();
    }

    public boolean canUse(E e) {
        return canView(e) || canSelect(e) || canDelete(e) || canEdit(e);
    }
}
