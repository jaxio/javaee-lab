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
$output.java($WebConverterSupport, "LocalDateTimeConverter")##

$output.require("javax.faces.component.PartialStateHolder")##
$output.require("javax.faces.component.UIComponent")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.faces.convert.Converter")##
$output.require("javax.faces.convert.FacesConverter")##
$output.require("java.time.LocalDateTime")##
$output.require("java.time.format.DateTimeFormatter")##
$output.require("com.jaxio.jpa.querybyexample.LocaleHolder")##

/**
 * JSF converter for JodaTime {@link LocalDateTime}.
 */
@FacesConverter(forClass = LocalDateTime.class, value = "localDateTimeConverter")
public class LocalDateTimeConverter implements Converter, PartialStateHolder {

    private static final String pattern = "yyyy-MM-dd HH:mm";
    private DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);

    /**
     * <code>p:calendar</code> must to use the same pattern as the converter, so we provide it here.
     */
    public String getPattern() {
        return pattern;
    }

    @Override
    public Object getAsObject(FacesContext pFacesCtx, UIComponent pComponent, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        return formatter.withLocale(LocaleHolder.getLocale()).parseDateTime(value).toLocalDateTime();
    }

    @Override
    public String getAsString(FacesContext pFacesCtx, UIComponent pComponent, Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof LocalDateTime) {
            return formatter.withLocale(LocaleHolder.getLocale()).print((LocalDateTime) value);
        }

        throw new IllegalArgumentException("Expecting a LocalDateTime instance but received "
                + value.getClass().getName());
    }
    // PartialStateHolder implementation

    @Override
    public Object saveState(FacesContext context) {
        return "";
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
    }

    private boolean transientFlag = true;

    @Override
    public boolean isTransient() {
        return transientFlag;
    }

    @Override
    public void setTransient(boolean transientFlag) {
        this.transientFlag = transientFlag;
    }

    private boolean initialState;

    @Override
    public void markInitialState() {
        initialState = true;
    }

    @Override
    public boolean initialStateMarked() {
        return initialState;
    }

    @Override
    public void clearInitialState() {
        initialState = false;
    }
}