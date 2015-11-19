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
$output.java($WebUtil, "MessageUtil")##

$output.requireStatic("javax.faces.application.FacesMessage.SEVERITY_ERROR")##
$output.requireStatic("javax.faces.application.FacesMessage.SEVERITY_FATAL")##
$output.requireStatic("javax.faces.application.FacesMessage.SEVERITY_INFO")##
$output.requireStatic("javax.faces.application.FacesMessage.SEVERITY_WARN")##
$output.require("javax.faces.application.FacesMessage")##
$output.require("javax.faces.application.FacesMessage.Severity")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.inject.Inject")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require($PrinterSupport, "TypeAwarePrinter")##
$output.require($Util, "ResourcesUtil")##

/**
 * Convenient bean to create JSF info/warn/error messages.
 * Business exceptions can be mapped to user friendly messages inside the {@link ${pound}error(Throwable)} method. 
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named","${Util.packageName}.Startup")##
public class $output.currentClass {
    private static ${output.currentClass} instance;
    
    @Inject
    private ResourcesUtil resourcesUtil;
    @Inject
    private TypeAwarePrinter printer;

    public ${output.currentClass}() {
        instance = this;
    }
    
    public static ${output.currentClass} getInstance() {
        return instance;
    }

    public static String toCssFriendly(Severity severity) {
        if (severity.equals(SEVERITY_INFO)) {
            return "info";
        } else if (severity.equals(SEVERITY_WARN)) {
            return "warn";
        } else if (severity.equals(SEVERITY_ERROR)) {
            return "error";
        } else if (severity.equals(SEVERITY_FATAL)) {
            return "fatal";
        }

        throw new IllegalStateException("Unexpected message severity: " + severity.toString());
    }

    // -- info

    public void info(String summaryKey, Object... args) {
        addFacesMessageUsingKey(SEVERITY_INFO, summaryKey, args);
    }

    public void infoEntity(String summaryKey, Identifiable<?> entity) {
        addFacesMessageUsingKey(SEVERITY_INFO, summaryKey, printer.print(entity));
    }

    public FacesMessage newInfo(String summaryKey, Object... args) {
        return newFacesMessageUsingKey(SEVERITY_INFO, summaryKey, args);
    }

    // -- warning

    public void warning(String summaryKey, Object... args) {
        addFacesMessageUsingKey(SEVERITY_WARN, summaryKey, args);
    }

    public FacesMessage newWarning(String summaryKey, Object... args) {
        return newFacesMessageUsingKey(SEVERITY_WARN, summaryKey, args);
    }

    // -- error

    public void error(String summaryKey, Object... args) {
        addFacesMessageUsingKey(SEVERITY_ERROR, summaryKey, args);
    }

    public FacesMessage newError(String summaryKey, Object... args) {
        return newFacesMessageUsingKey(SEVERITY_ERROR, summaryKey, args);
    }

    private void addFacesMessage(FacesMessage fm) {
        if (fm != null) {
            FacesContext.getCurrentInstance().addMessage(null, fm);
        }
    }

    private void addFacesMessageUsingKey(Severity severity, String summaryKey, Object arg) {
        addFacesMessageUsingKey(severity, summaryKey, new Object[] {arg});
    }

    private void addFacesMessageUsingKey(Severity severity, String summaryKey, Object[] args) {
        addFacesMessage(newFacesMessageUsingKey(severity, summaryKey, args));
    }

    private FacesMessage newFacesMessageUsingKey(Severity severity, String summaryKey, Object[] args) {
        return newFacesMessageUsingText(severity, resourcesUtil.getPropertyWithArrayArg(summaryKey, args));
    }

    private FacesMessage newFacesMessageUsingText(Severity severity, String text) {
        FacesMessage fm = new FacesMessage(text);
        fm.setSeverity(severity);
        return fm;
    }
}