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
$output.java($WebConversation, "ConversationPhaseListener")##

$output.requireStatic("javax.faces.event.PhaseId.ANY_PHASE")##
$output.requireStatic("javax.faces.event.PhaseId.RENDER_RESPONSE")##
$output.requireStatic("javax.faces.event.PhaseId.RESTORE_VIEW")##
$output.require("javax.faces.event.PhaseEvent")##
$output.require("javax.faces.event.PhaseId")##
$output.require("javax.faces.event.PhaseListener")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("org.apache.deltaspike.core.api.provider.BeanProvider")##
$output.require("org.omnifaces.util.Faces")##
$output.require($WebConversation,"ConversationBean")##
$output.require($WebModelSupport,"GenericController")##

public class $output.currentClass implements PhaseListener {
$serialVersionUID
    private static final String CONVERSATION_CONTEXT_ID_PARAMETER = "ccid";
    private static final String BEGIN_CREATE_PARAMETER = "beginCreate";
    private static final String BEGIN_SEARCH_PARAMETER = "beginSearch";

    public PhaseId getPhaseId() {
        return ANY_PHASE;
    }
    
    public void beforePhase(PhaseEvent phaseEvent) {
        if (phaseEvent.getPhaseId().equals(RESTORE_VIEW) || ConversationHolder.getCurrentConversation() == null) {
            HttpServletRequest request = (HttpServletRequest)Faces.getContext().getExternalContext().getRequest();

            String ccid = request.getParameter(CONVERSATION_CONTEXT_ID_PARAMETER);
            String beginCreate = request.getParameter(BEGIN_CREATE_PARAMETER);
            String beginSearch = request.getParameter(BEGIN_SEARCH_PARAMETER);

            try {
                if (ccid != null) {
                    getConversationManager().resumeConversation(ccid);
                } else if (beginSearch != null) {
                    BeanProvider.getContextualReference(beginSearch, false, GenericController.class).beginSearch();
                    redirectToConversationUrl();
                } else if (beginCreate != null) {
                    BeanProvider.getContextualReference(beginCreate, false, GenericController.class).beginCreate();
                    redirectToConversationUrl();
                }
            } catch (UnexpectedConversationException e) {
                // FIXME - handle invalid requested ccid
                ConversationUtil.redirect(e.getRedirectUrl());
            }
        }
    }

    public void afterPhase(PhaseEvent phaseEvent) {
        if (phaseEvent.getPhaseId().equals(RENDER_RESPONSE) || phaseEvent.getFacesContext().getResponseComplete()) {
            getConversationManager().pauseCurrentConversation();
        }
    }
    
    private void redirectToConversationUrl() {
        ConversationUtil.redirect(BeanProvider.getContextualReference(ConversationBean.class, false).nextUrl());
    }


    private ConversationManager getConversationManager() {
        return BeanProvider.getContextualReference(ConversationManager.class, false);        
    }
}