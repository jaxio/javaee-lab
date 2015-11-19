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
$output.java($WebModelSupport, "SelectableListDataModel")##

$output.require("java.io.Serializable")##
$output.require("java.util.List")##
$output.require("javax.faces.model.ListDataModel")##
$output.require("org.primefaces.model.SelectableDataModel")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##

public class ${output.currentClass}<E extends Identifiable<?>> extends ListDataModel<E> implements SelectableDataModel<E>, Serializable {
    private static final long serialVersionUID = 1L;
    private E selectedRow;

    public ${output.currentClass}() {
    }

    public ${output.currentClass}(List<E> data) {
        super(data);
    }

    /**
     * Returns the currently selected row. To be called from your flow upon a <code>selectXxx</code> transition.
     */
    public E getSelectedRow() {
        return selectedRow;
    }

    /**
     * Set the currently selected row. To be called from your dataTable.
     */
    public void setSelectedRow(E selectedRow) {
        this.selectedRow = selectedRow;
    }

    /**
     * Set the selectedRow to null. To be called from your flow.
     */
    public void resetSelectedRow() {
        this.selectedRow = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getRowData(String rowKey) {
        for (E item : (List<E>) getWrappedData()) {
            if (rowKey.equals(String.valueOf(item.hashCode()))) {
                return item;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(E item) {
        return String.valueOf(item.hashCode());
    }
}
