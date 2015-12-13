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
$output.java($WebComponent, "DatePicker")##

$output.requireStatic($WebComponent, "DatePickerHelper.normalize")##
$output.require("java.io.IOException")##
$output.require("java.util.Calendar")##
$output.require("java.util.Date")##
$output.require("javax.faces.component.FacesComponent")##
$output.require("javax.faces.component.NamingContainer")##
$output.require("javax.faces.component.UIInput")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.faces.convert.Converter")##
$output.require("javax.faces.convert.ConverterException")##
$output.require("javax.faces.convert.DateTimeConverter")##
$output.require("java.time.LocalDate")##
$output.require("java.time.LocalDateTime")##
$output.require($WebConverterSupport, "LocalDateConverter")##
$output.require($WebConverterSupport, "LocalDateTimeConverter")##

/**
 * Java side of the <code>datePicker</code> composite component that supports
 * <ul>
 * <li>{@link Date}</li>
 * <li>{@link LocalDate}</li>
 * <li>{@link LocalDateTime}</li>
 * </ul>
 * This component exists solely to make accessible ARIA compatible date components
 *
 * @see http://weblogs.java.net/blog/cayhorstmann/archive/2010/01/30/composite-input-components-jsf
 * @see http://www.w3.org/WAI/intro/aria.php
 * @see the followings jsf components : inputDateAria, inputDateTimeAria, inputLocalDateAria, inputLocalDateTimeAria
 */
@FacesComponent("components.DatePicker")
public class $output.currentClass extends UIInput implements NamingContainer {

    @Override
    public String getFamily() {
        return "javax.faces.NamingContainer";
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        UIInput year = findUIInput("year");
        UIInput month = findUIInput("month");
        UIInput day = findUIInput("day");
        UIInput hour = findUIInput("hour");
        UIInput min = findUIInput("minute");

        Converter converter = getConverter();
        if (converter instanceof LocalDateConverter) {
            encode(year, month, day, (LocalDate) getValue());
        } else if (converter instanceof LocalDateTimeConverter) {
            encode(year, month, day, hour, min, (LocalDateTime) getValue());
        } else if (converter instanceof DateTimeConverter) {
            encode(year, month, day, hour, min, (Date) getValue());
        }

        super.encodeBegin(context);
    }
    
    private void encode(UIInput year, UIInput month, UIInput day, LocalDate localDate) {
        if (localDate == null) {
            return;
        }

        year.setValue(normalize(localDate.getYear()));
        month.setValue(normalize(localDate.getMonthValue()));
        day.setValue(normalize(localDate.getDayOfMonth()));
    }    

    private void encode(UIInput year, UIInput month, UIInput day, UIInput hour, UIInput min, LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return;
        }

        year.setValue(normalize(localDateTime.getYear()));
        month.setValue(normalize(localDateTime.getMonthValue()));
        day.setValue(normalize(localDateTime.getDayOfMonth()));
        hour.setValue(normalize(localDateTime.getHour()));
        min.setValue(normalize(localDateTime.getMinute()));
    }

    private void encode(UIInput year, UIInput month, UIInput day, UIInput hour, UIInput min, Date date) {
        if (date == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        year.setValue(normalize(calendar.get(Calendar.YEAR)));
        month.setValue(normalize(calendar.get(Calendar.MONTH) + 1));
        day.setValue(normalize(calendar.get(Calendar.DAY_OF_MONTH)));
        hour.setValue(normalize(calendar.get(Calendar.HOUR_OF_DAY)));
        min.setValue(normalize(calendar.get(Calendar.MINUTE)));
    }

    @Override
    protected Object getConvertedValue(FacesContext context, Object newSubmittedValue) throws ConverterException {
        return getConverter().getAsObject(context, this, (String) newSubmittedValue);
    }

    /**
     * Construct submitted value as <code>yyyy-MM-dd</code>, <code>yyyy-MM-dd HH:mm</code> or <code>HH:mm</code> The returned string is used by getConvertedValue...
     */
    @Override
    public Object getSubmittedValue() {
        UIInput year = findUIInput("year");
        UIInput month = findUIInput("month");
        UIInput day = findUIInput("day");
        UIInput hour = findUIInput("hour");
        UIInput minute = findUIInput("minute");

        StringBuilder sb = new StringBuilder();
        if (isConvertible(year) && isConvertible(month) && isConvertible(day)) {
            sb.append(year.getSubmittedValue());
            sb.append("-").append(month.getSubmittedValue());
            sb.append("-").append(day.getSubmittedValue());
            if (hour.isRendered() && minute.isRendered()) {
                sb.append(" ");
            }
        }

        if (isConvertible(hour) && isConvertible(minute)) {
            sb.append(hour.getSubmittedValue());
            sb.append(":").append(minute.getSubmittedValue());
        }

        return sb.toString();
    }

    private UIInput findUIInput(String id) {
        return (UIInput) findComponent(id);
    }
    
    private boolean isConvertible(UIInput input) {
        return input.isRendered() && input.getSubmittedValue() != null && !((String)input.getSubmittedValue()).isEmpty();
    }
}