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
$output.java($WebModelSupport, "GenericLazyDataModel")##

$output.requireStatic($WebConversation, "ConversationHolder.getCurrentConversation")##
$output.requireStatic("org.apache.commons.lang.StringUtils.isNotEmpty")##
$output.require("java.io.IOException")##
$output.require("java.io.Serializable")##
$output.require("java.text.SimpleDateFormat")##
$output.require("java.util.Date")##
$output.require("java.util.List")##
$output.require("java.util.Map")##
$output.require("javax.inject.Inject")##
$output.require("org.omnifaces.util.Faces")##
$output.require("org.primefaces.event.SelectEvent")##
$output.require("org.primefaces.model.LazyDataModel")##
$output.require("org.primefaces.model.SortOrder")##
$output.require("com.jaxio.jpa.querybyexample.OrderBy")##
$output.require("com.jaxio.jpa.querybyexample.OrderByDirection")##
$output.require("com.jaxio.jpa.querybyexample.SearchParameters")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require($PrinterSupport, "TypeAwarePrinter")##
$output.require("com.jaxio.jpa.querybyexample.GenericRepository")##
$output.require($Util, "ResourcesUtil")##
$output.require($WebConversation, "ConversationContext")##
$output.require($WebConversation, "ConversationCallBack")##
$output.require($WebUtil, "MessageUtil")##
$output.require($WebUtil, "PrimeFacesUtil")##

/**
 * Extends PrimeFaces {@link LazyDataModel} in order to support server-side pagination, row selection, multi select etc.
 */
public abstract class ${output.currentClass}<E extends Identifiable<PK>, PK extends Serializable, F extends GenericSearchForm<E, PK, F>> extends LazyDataModel<E> {
    private static final long serialVersionUID = 1L;

    @Inject
    protected transient ResourcesUtil resourcesUtil;
    @Inject
    protected transient MessageUtil messageUtil;
    @Inject 
    protected transient TypeAwarePrinter printer;

    private E selectedRow;
    private E[] selectedRows;
    private boolean bypassFirstOffset = true;

    protected transient GenericRepository<E, PK> repository;
    protected transient GenericController<E, PK> controller;
    protected transient GenericSearchForm<E, PK, F> searchForm;
    protected transient GenericExcelExporter<E> excelExporter;

    public ${output.currentClass}() {
        // mandatory no-args constructor to make this bean proxyable
    }
    
    public ${output.currentClass}(GenericRepository<E, PK> repository, GenericController<E, PK> controller, GenericSearchForm<E, PK, F> searchForm, GenericExcelExporter<E> excelExporter) {
        this.repository = repository;
        this.controller = controller;
        this.searchForm = searchForm;
        this.excelExporter = excelExporter;
    }

    @Override
    public List<E> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
        E example = searchForm.getEntity();
        SearchParameters sp = populateSearchParameters(first, pageSize, sortField, sortOrder, filters);        
        setRowCount(repository.findCount(example, sp));
        return repository.find(example, sp);
    }

    /**
     * _HACK_
     * Call it from your view when a <code>search</code> event is triggered to bypass offset sent by primefaces paginator.
     */
    public void onSearch() {
        bypassFirstOffset = true;
    }

    @Override
    public void setRowCount(int rowCount) {
        super.setRowCount(rowCount);
        PrimeFacesUtil.updateSearchResultsRegion(resourcesUtil.getPluralableProperty("search_results_status", rowCount), rowCount);
    }

   /**
    * Applies the passed parameters to the passed SearchParameters.
    * @return the passed searchParameters
    */
   protected SearchParameters populateSearchParameters(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
       SearchParameters sp = searchForm.toSearchParameters();
       sp.setFirst(bypassFirstOffset ? 0 : first);
       bypassFirstOffset = false;
       sp.setPageSize(pageSize);

       if (isNotEmpty(sortField)) {
           return sp.orderBy(new OrderBy(convert(sortOrder), sortField, repository.getType()));
       } else {
           return controller.defaultOrder(sp);
       }
    }

    // ---------------------
    // Select row
    // ---------------------

   /**
    * Returns the currently selected row.
    */
    public E getSelectedRow() {
        return selectedRow;
    }

    /**
     * Set the currently selected row.
     */
    public void setSelectedRow(E selectedRow) {
        this.selectedRow = selectedRow;
    }

    /**
     * Set the selectedRow to null.
     */
    public void resetSelectedRow() {
        this.selectedRow = null;
    }

    
    // ---------------------
    // Multi select
    // ---------------------
    
    public void setSelectedRows(E[] selectedRows) {
        this.selectedRows = selectedRows;
    }

    public E[] getSelectedRows() {
        return selectedRows;
    }

    public String multiSelect() {
        return getCallBack().selected(getSelectedRows());
    }

    /**
     * Convert PrimeFaces {@link SortOrder} to our {@link OrderByDirection}.
     */
    protected OrderByDirection convert(SortOrder order) {
        return order == SortOrder.DESCENDING ? OrderByDirection.DESC : OrderByDirection.ASC;
    }

    // ---------------------
    // Actions
    // ---------------------

    /**
     * Action to create a new entity.
     */
    public String create() {
        return controller.create();
    }

    /**
     * Action to edit the selected entity.
     */
    public String edit() {
        return controller.edit(getRowData());
    }

    /**
     * Action to view the selected entity.
     */
    public String view() {
        return controller.view(getRowData());
    }

    /**
     * Action invoked from sub search pages used to select the target of an association.
     */
    public String select() {
        return select(getRowData());
    }

    protected String select(E selectedRow) {
        return getCallBack().selected(selectedRow);
    }
    

    /**
     * React to mouse click and force the navigation depending on the context.
     * When in sub mode, the select action is invoked otherwise the edit action is invoked.
     */
    public void onRowSelect(SelectEvent event) {
        E selected = getSelectedRow();
        if (selected != null) {
            if (getCurrentConversation().getCurrentContext().isSub()) {
                Faces.navigate(controller.select(selected));
            } else if (controller.getPermission().canEdit(selected)) {
                Faces.navigate(controller.edit(selected));
            } else {
                Faces.navigate(controller.view(selected));
            }
        }
    }

    /**
     * Ajax action listener to delete the selected entity.
     */
    public void delete() {
        E selected = getSelectedRow();
        if (selected != null) {
            repository.delete(selected);
            messageUtil.infoEntity("status_deleted_ok", selected);
            resetSelectedRow();
        }
    }

    @Override
    public String getRowKey(E item) {
        return String.valueOf(item.hashCode());
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getRowData(String rowKey) {
        for (E item : ((List<E>) getWrappedData())) {
            if (rowKey.equals(getRowKey(item))) {
                return item;
            }
        }
        return null;
    }

    private ConversationCallBack<E> getCallBack() {
        return getCurrentConversation() //
                .<ConversationContext<E>> getCurrentContext() //
                .getCallBack();
    }

    public void onExcel() throws IOException {
        excelExporter.onExcel(getExcelFilename(), repository.find(searchForm.toSearchParameters()));
    }

    protected String getExcelFilename() {
        return repository.getType().getSimpleName() + "-" + new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss").format(new Date()) + ".xls";
    }
}