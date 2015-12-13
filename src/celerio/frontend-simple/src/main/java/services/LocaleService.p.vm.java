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
$output.java($WebService, "LocaleService")##

$output.requireStatic("java.util.Locale.ENGLISH")##
$output.requireStatic("java.util.Locale.FRENCH")##
$output.require("java.io.Serializable")##
$output.require("java.util.Locale")##
$output.require("javax.enterprise.context.SessionScoped")##
$output.require("javax.faces.context.ExternalContext")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.inject.Inject")##
$output.require("javax.inject.Named")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("javax.servlet.http.HttpServletResponse")##
$output.require("com.jaxio.jpa.querybyexample.LocaleHolder")##
$output.require($WebFilter, "LocaleResolver")##

@Named
@SessionScoped
public class LocaleService implements Serializable {
$serialVersionUID
    
    @Inject
    private LocaleResolver localeResolver;

    public String getLocale() {
        return LocaleHolder.getLocale().toString();
    }

    public String getLanguage() {
        return LocaleHolder.getLocale().getLanguage();
    }

    public String switchToFrench() {
        return switchToLocale(FRENCH);
    }

    public String switchToEnglish() {
        return switchToLocale(ENGLISH);
    }

    private String switchToLocale(Locale locale) {
        updateJsfLocale(locale);
        updateResolverLocale(locale);
        return redirectToSelf();
    }

    private String redirectToSelf() {
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        return viewId + "?faces-redirect=true";
    }

    private void updateJsfLocale(Locale locale) {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    private void updateResolverLocale(Locale locale) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        localeResolver.setLocale((HttpServletRequest) externalContext.getRequest(), (HttpServletResponse) externalContext.getResponse(), locale);
        LocaleHolder.setLocale(locale);
    }

    public boolean isFrench() {
        // check 'fr_FR' or simply 'fr'
        return FRENCH.equals(LocaleHolder.getLocale()) || FRENCH.getLanguage().equals(getLanguage());
    }
}
