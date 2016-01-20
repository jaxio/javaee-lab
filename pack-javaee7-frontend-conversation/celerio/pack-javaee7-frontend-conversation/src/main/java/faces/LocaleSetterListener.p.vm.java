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
$output.java($WebFaces, "LocaleSetterListener")##

$output.require("javax.faces.context.FacesContext")##
$output.require("javax.faces.event.PhaseEvent")##
$output.require("javax.faces.event.PhaseId")##
$output.require("org.omnifaces.eventlistener.DefaultPhaseListener")##
$output.require("com.jaxio.jpa.querybyexample.LocaleHolder")##
$output.require($WebFilter, "LocaleResolverRequestFilter")##

/**
 * Set the current locale to jsf from the resolver initialized in {@link LocaleResolverRequestFilter} filter.
 */
public class $output.currentClass extends DefaultPhaseListener {
    private static final long serialVersionUID = 1L;

    public ${output.currentClass}() {
        super(PhaseId.RESTORE_VIEW);
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        if (FacesContext.getCurrentInstance().getViewRoot() != null) {
            FacesContext.getCurrentInstance().getViewRoot().setLocale(LocaleHolder.getLocale());
        }
    }
}