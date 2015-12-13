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
$output.java($WebModelSupport, "GenericExcelExporter")##

$output.requireStatic("com.google.common.base.Throwables.propagate")##
$output.require("java.io.IOException")##
$output.require("java.io.Serializable")##
$output.require("java.util.Date")##
$output.require("java.util.List")##
$output.require("java.util.Collection")##
$output.require("javax.faces.context.ExternalContext")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.inject.Inject")##
$output.require("com.jaxio.jpa.querybyexample.LabelizedEnum")##
$output.require("org.apache.poi.hssf.usermodel.HSSFWorkbook")##
$output.require("org.apache.poi.ss.usermodel.Cell")##
$output.require("org.apache.poi.ss.usermodel.CellStyle")##
$output.require("org.apache.poi.ss.usermodel.DataFormat")##
$output.require("org.apache.poi.ss.usermodel.Font")##
$output.require("org.apache.poi.ss.usermodel.IndexedColors")##
$output.require("org.apache.poi.ss.usermodel.Row")##
$output.require("org.apache.poi.ss.usermodel.Sheet")##
$output.require("org.apache.poi.ss.usermodel.Workbook")##
$output.require("java.time.LocalDate")##
$output.require("java.time.LocalDateTime")##
$output.require("java.time.ZoneId")##
$output.require($Context, "UserContext")##
$output.require("com.jaxio.jpa.querybyexample.PropertySelector")##
$output.require("com.jaxio.jpa.querybyexample.Range")##
$output.require($PrinterSupport, "TypeAwarePrinter")##
$output.require($Util, "ResourcesUtil")##
$output.require($WebUtil, "DownloadUtil")##

public abstract class ${output.currentClass}<E> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String EXCEL_CONTENT_TYPE = "application/vns.ms-excel";

    @Inject
    protected transient ResourcesUtil msg;

    @Inject
    protected transient TypeAwarePrinter printer;

    protected String[] resultHeaderKeys;
    protected transient Workbook wb;
    protected transient Sheet results;
    protected transient Sheet criteria;
    protected transient Sheet activeSheet;
    protected transient DataFormat dataFormat;
    private transient CellStyle topHeaderStyle;
    private transient CellStyle leftHeaderStyle;
    private transient CellStyle dateStyle;
    private transient CellStyle dateTimeStyle;
    private transient CellStyle timeStyle;
    

    public ${output.currentClass}() {
        // mandatory no-args constructor to make this bean proxyable
    }

    public ${output.currentClass}(String... resultHeaderKeys) {
        this.resultHeaderKeys = resultHeaderKeys;
    }

    /**
     * Lazy resest/init workbook and sheet.
     */
    protected void reset() {
        wb = new HSSFWorkbook();
        results = wb.createSheet(msg.getProperty("search_results"));
        criteria = wb.createSheet(msg.getProperty("search_criteria"));
        activeSheet = null;
        dataFormat = wb.createDataFormat();
        topHeaderStyle = null;
        leftHeaderStyle = null;
        dateStyle = null;
        dateTimeStyle = null;
        timeStyle = null;
    }

    public void useResultsSheet() {
        activeSheet = results;
    }

    public void useCriteriaSheet() {
        activeSheet = criteria;
    }

    public Workbook getWorkbook() {
        return wb;
    }

    /**
     * Write the passed search result item at the passed row index.
     */
    protected abstract void fillResultItem(int row, E item);

    /**
     * Write the search criteria, starting at the passed row index.
     */
    public abstract void fillSearchCriteria(int row);

    protected int fillCommonSearchCriteria(int totalCount) {
        useCriteriaSheet();
        int row = 0;
        setLeftHeader(row, 0, "search_date");
        setDateTime(row++, 1, new Date());
        setLeftHeader(row, 0, "search_by");
        setValue(row++, 1, UserContext.getUsername());
        setLeftHeader(row, 0, "search_nb_results");
        setValue(row++, 1, totalCount);        
        getRow(row++); // leave a blank non null line
        setLeftHeader(row++, 0, "search_criteria");
        return row;
    }

    protected void fillSearchResult(List<E> items) {
        useResultsSheet();

        // header
        int col = 0;
        for (String key : resultHeaderKeys) {
            setTopHeader(0, col++, key);
        }

        // data
        int row = 1;
        for (E item : items) {
            fillResultItem(row++, item);
        }
    }

    // -----------------------------------------------------
    // Stream workbook to client
    // -----------------------------------------------------

    public void onExcel(String filename, List<E> results) {
        reset();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType(EXCEL_CONTENT_TYPE);
        externalContext.setResponseHeader("Content-Disposition", "attachement; filename=\"" + filename + "\"");
        DownloadUtil.forceResponseHeaderForDownload();

        try {
            int row = fillCommonSearchCriteria(results.size());
            fillSearchCriteria(row);
            fillSearchResult(results);
            autoSizeColumns();
            wb.write(externalContext.getResponseOutputStream());
        } catch (IOException e) {
            throw propagate(e);
        } finally {
            facesContext.responseComplete();
        }
    }

    // -----------------------------------------------------
    // Helper to set values in cell
    // -----------------------------------------------------

    protected void setTopHeader(int row, int col, String headerKey) {
        setValue(row, col, msg.getProperty(headerKey));
        getCell(row, col).setCellStyle(getTopHeaderStyle());
    }

    protected void setLeftHeader(int row, int col, String headerKey) {
        setValue(row, col, msg.getProperty(headerKey));
        getCell(row, col).setCellStyle(getLeftHeaderStyle());
    }
    
    protected void setSelector(int row, int col, String labelKey, PropertySelector<? super E, ?> selector) {
        setLeftHeader(row, col++, labelKey);

        if (selector.getSelected() == null) {
            return;
        }

        if (selector.isBoolean()) {
            for (Object o : selector.getSelected()) {
                if (o == null) {
                    setValue(row, col++, msg.getProperty("null_value"));
                } else {
                    setValue(row, col++, (Boolean) o);
                }
            }
        } else if (selector.isLabelizedEnum()) {
            for (Object o : selector.getSelected()) {
                if (o == null) {
                    setValue(row, col++, msg.getProperty("null_value"));
                } else {
                    setValue(row, col++, (LabelizedEnum) o);
                }
            }
        } else if (selector.isString()) {
            for (Object o : selector.getSelected()) {
                setValue(row, col++, (String) o);
            }
        } else if (selector.isNumber()) {
            for (Object o : selector.getSelected()) {
                setValue(row, col++, (Number) o);
            }
        } else {
            throw new IllegalStateException("fix me");
        }
    }
#if($project.hibernateSearchUsed)
$output.require("com.jaxio.jpa.querybyexample.TermSelector")##

    protected void setTermSelector(int row, int col, String labelKey, TermSelector selector) {
        setLeftHeader(row, col++, labelKey);

        if (selector.getSelected() == null) {
            return;
        }

        for (String o : selector.getSelected()) {
            setValue(row, col++, o);
        }
    }
#end

    protected void setSelectedEntities(int row, int col, String labelKey, Collection<?> entities) {
        setLeftHeader(row, col++, labelKey);

        if (entities == null) {
            return;
        }

        for (Object entity : entities) {
            setValue(row, col++, printer.print(entity));
        }
    }

    protected void setRangeNumber(int row, int col, String labelKey, Range<? super E, ? extends Number> range) {
        setRangeNumberHeader(row, col, labelKey);
        setValue(row, col + 2, range.getFrom());
        setValue(row, col + 4, range.getTo());
    }

    protected void setRangeDate(int row, int col, String labelKey, Range<? super E, ? extends Date> range) {
        setRangeDateHeader(row, col, labelKey);
        setValue(row, col + 2, range.getFrom());
        setValue(row, col + 4, range.getTo());
    }

    protected void setRangeDateTime(int row, int col, String labelKey, Range<? super E, ? extends Date> range) {
        setRangeDateHeader(row, col, labelKey);
        setDateTime(row, col + 2, range.getFrom());
        setDateTime(row, col + 4, range.getTo());
    }

    protected void setRangeLocalDate(int row, int col, String labelKey, Range<? super E, ? extends LocalDate> range) {
        setRangeDateHeader(row, col, labelKey);
        setValue(row, col + 2, range.getFrom());
        setValue(row, col + 4, range.getTo());
    }

    protected void setRangeLocalDateTime(int row, int col, String labelKey, Range<? super E, ? extends LocalDateTime> range) {
        setRangeDateHeader(row, col, labelKey);
        setValue(row, col + 2, range.getFrom());
        setValue(row, col + 4, range.getTo());
    }

    protected void setRangeDateHeader(int row, int col, String labelKey) {
        setLeftHeader(row, col, labelKey);
        setLeftHeader(row, col + 1, "date_range_from");
        setLeftHeader(row, col + 3, "date_range_to");
    }

    protected void setRangeNumberHeader(int row, int col, String labelKey) {
        setLeftHeader(row, col, labelKey);
        setLeftHeader(row, col + 1, "range_from");
        setLeftHeader(row, col + 3, "range_to");
    }

    protected void setValue(int row, int col, String value) {
        if (value != null) {
            Cell cell = getCell(row, col);
            cell.setCellValue(value);
        }
    }

    protected void setValue(int row, int col, Date value) {
        if (value != null) {
            Cell cell = getCell(row, col);
            cell.setCellValue(value);
            cell.setCellStyle(getDateStyle());
        }
    }

    protected void setDateTime(int row, int col, Date value) {
        if (value != null) {
            Cell cell = getCell(row, col);
            cell.setCellValue(value);
            cell.setCellStyle(getDateTimeStyle());
        }
    }

    protected void setTime(int row, int col, Date value) {
        if (value != null) {
            Cell cell = getCell(row, col);
            cell.setCellValue(value);
            cell.setCellStyle(getTimeStyle());
        }
    }
    
    protected void setValue(int row, int col, LocalDate value) {
        if (value != null) {
            Date date = Date.from(value.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            setValue(row, col, date);
        }
    }

    protected void setValue(int row, int col, LocalDateTime value) {
        if (value != null) {
            Date date = Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
            setDateTime(row, col, date);
        }
    }

    protected void setValue(int row, int col, Boolean value) {
        if (value != null) {
            getCell(row, col).setCellValue(value);
        }
    }

    protected void setValue(int row, int col, Number value) {
        if (value != null) {
            getCell(row, col).setCellValue(value.doubleValue());
        }
    }

    protected void setValue(int row, int col, LabelizedEnum value) {
        if (value != null) {
            getCell(row, col).setCellValue(value.getLabel());
        }
    }

    protected void setEntity(int row, int col, Object value) {
        if (value != null) {
            getCell(row, col).setCellValue(printer.print(value));
        }
    }

    // --------------------------------------------
    // Cell utils
    // --------------------------------------------

    protected Row getRow(int index) {
        Row row = activeSheet.getRow(index);
        return row == null ? activeSheet.createRow(index) : row;
    }

    protected Cell getCell(int rowIndex, int col) {
        Row row = getRow(rowIndex);
        Cell cell = row.getCell(col);
        return cell == null ? row.createCell(col) : cell;
    }

    // --------------------------------------------
    // Cell Styles
    // --------------------------------------------
    
    protected CellStyle getTopHeaderStyle() {
        if (topHeaderStyle == null) {
            topHeaderStyle = wb.createCellStyle();
            applyCommonHeaderStyle(topHeaderStyle);
            topHeaderStyle.setAlignment(CellStyle.ALIGN_CENTER);
        }

        return topHeaderStyle;
    }

    protected CellStyle getLeftHeaderStyle() {
        if (leftHeaderStyle == null) {
            leftHeaderStyle = wb.createCellStyle();
            applyCommonHeaderStyle(leftHeaderStyle);
        }

        return leftHeaderStyle;
    }

    protected void applyCommonHeaderStyle(CellStyle style) {
        // font
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(headerFont);

        // color
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        // border
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
    }

    protected CellStyle getDateStyle() {
        if (dateStyle == null) {
            dateStyle = wb.createCellStyle();
            dateStyle.setDataFormat(dataFormat.getFormat(msg.getProperty("excel_date_format")));
        }

        return dateStyle;
    }

    protected CellStyle getDateTimeStyle() {
        if (dateTimeStyle == null) {
            dateTimeStyle = wb.createCellStyle();
            dateTimeStyle.setDataFormat(dataFormat.getFormat(msg.getProperty("excel_date_time_format")));
        }

        return dateTimeStyle;
    }

    protected CellStyle getTimeStyle() {
        if (timeStyle == null) {
            timeStyle = wb.createCellStyle();
            timeStyle.setDataFormat(dataFormat.getFormat(msg.getProperty("excel_time_format")));
        }

        return timeStyle;
    }
    
    protected void autoSizeColumns() {
        useResultsSheet();
        int lastCellNum = getRow(0).getLastCellNum();
        for (int i = 0; i <= lastCellNum; i++) {
            results.autoSizeColumn(i);
        }

        useCriteriaSheet();
        int max = maxCriteriaCellNum();
        for (int i = 0; i <= max; i++) {
            criteria.autoSizeColumn(i);
        }
    }

    protected int maxCriteriaCellNum() {
        int max = 0;

        for (int i = criteria.getFirstRowNum(); i <= criteria.getLastRowNum(); i++) {
            max = Math.max(max, criteria.getRow(i).getLastCellNum());
        }

        return max;
    }    
}