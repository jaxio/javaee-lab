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
$output.java($WebComponent, "DatePickerHelper")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.requireStatic("java.util.Calendar.DAY_OF_MONTH")##
$output.requireStatic("java.util.Calendar.DAY_OF_WEEK")##
$output.requireStatic("java.util.Calendar.LONG")##
$output.requireStatic("java.util.Calendar.MONTH")##
$output.requireStatic("java.util.Calendar.YEAR")##
$output.require("java.util.Calendar")##
$output.require("java.util.List")##
$output.require("javax.faces.component.UIInput")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.faces.model.SelectItem")##
$output.require("com.jaxio.jpa.querybyexample.LocaleHolder")##

/**
 * Helper used from the {@link DatePicker} composite component.
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class $output.currentClass {

    public List<SelectItem> getYears(int min, int max) {
        List<SelectItem> result = newArrayList();
        for (int i = min; i <= max; i++) {
            String year = String.valueOf(i);
            result.add(new SelectItem(year, year));
        }

        return result;
    }

    public List<SelectItem> getMonths() {
        List<SelectItem> result = newArrayList();

        Calendar c = Calendar.getInstance();
        c.set(DAY_OF_MONTH, 1); // prevent potential month shifting when reseting month below

        for (int i = 1; i <= 12; i++) {
            c.set(MONTH, i - 1);
            String label = c.getDisplayName(MONTH, LONG, LocaleHolder.getLocale());
            result.add(new SelectItem(normalize(i), label));
        }

        return result;
    }

    public List<SelectItem> getDays(String ccClientId, boolean appendDayOfWeek) {
        UIInput ccDatepicker = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent(ccClientId);
        UIInput year = (UIInput) ccDatepicker.findComponent("year");
        UIInput month = (UIInput) ccDatepicker.findComponent("month");

        Calendar c = Calendar.getInstance();
        c.set(DAY_OF_MONTH, 1); // prevent potential month shifting when reseting month below
        try {
            c.set(YEAR, Integer.parseInt((String) year.getLocalValue()));
            c.set(MONTH, Integer.parseInt((String) month.getLocalValue()) - 1);
        }catch(Exception e) {
            //
        }

        List<SelectItem> result = newArrayList();

        int max = c.getActualMaximum(DAY_OF_MONTH);
        for (int i = 1; i <= max; i++) {
            String day = normalize(i);
            c.set(DAY_OF_MONTH, i);
            StringBuilder sb = new StringBuilder();
            sb.append(day);
            if (appendDayOfWeek) {
                sb.append(" ").append(c.getDisplayName(DAY_OF_WEEK, LONG, LocaleHolder.getLocale()));
            }
            result.add(new SelectItem(day, sb.toString()));
        }

        return result;
    }

    public List<SelectItem> getHours() {
        List<SelectItem> result = newArrayList();
        for (int i = 0; i < 24; i++) {
            String hour = normalize(i);
            result.add(new SelectItem(hour, hour));
        }
        return result;
    }

    public List<SelectItem> getMinutes() {
        List<SelectItem> result = newArrayList();
        result.add(new SelectItem("00", "00"));
        result.add(new SelectItem("15", "15"));
        result.add(new SelectItem("30", "30"));
        result.add(new SelectItem("45", "45"));
        return result;
    }

    public static String normalize(int i) {
        return i < 10 ? "0" + i : "" + i;
    }
}