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
$output.java($WebConversation, "ConversationCallBack")##

$output.requireStatic($WebConversation, "ConversationHolder.getCurrentConversation")##
$output.require("java.io.Serializable")##

/**
 * CallBacks should be invoked at the end of a @{link ConversationContext} life.
 * A CallBack allows the creator of the conversation context to know which action led to the context termination and to retrieve 
 * any relevant output.
 * For example, a conversation context can be created to let a user select an entity among a list of entities. When the user selects 
 * an entity, the action invokes the selected(T entity) method which let the creator of the context do something with the selected 
 * entity (e.g add it or set it somewhere...).
 */
public class ${output.currentClass}<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public ${output.currentClass}() {        
    }

    public final String ok(T entity) {
        incrementPopContextOnNextPauseCounter();
        onOk(entity);
        return nextView();
    }

    /**
     * The given entity has been oked. Example: it could mean
     * that it is a newly created entity, that it was validated (but not saved) and
     * that it is up to you to decide what to do with it.
     */
    protected void onOk(T entity) {
    }

    public final String selected(T entity) {
        incrementPopContextOnNextPauseCounter();
        onSelected(entity);
        return nextView();
    }

    public final String selected(T... entities) {
        incrementPopContextOnNextPauseCounter();
        for (T entity : entities) {
            onSelected(entity);
        }
        return nextView();
    }

    /**
     * The given entity has been selected.
     */
    protected void onSelected(T entity) {
    }

    public final String saved(T entity) {
        incrementPopContextOnNextPauseCounter();
        onSaved(entity);
        return nextView();
    }

    /**
     * The given entity has just been saved.
     */
    protected void onSaved(T entity) {
    }

    public final String notSaved(T entity) {
        incrementPopContextOnNextPauseCounter();
        onNotSaved(entity);
        return nextView();
    }

    /**
     * The given entity has not been saved.
     */
    protected void onNotSaved(T entity) {
    }

    public final String deleted(T entity) {
        incrementPopContextOnNextPauseCounter();
        onDeleted(entity);
        return nextView();
    }

    /**
     * The given entity has just been deleted.
     */
    protected void onDeleted(T entity) {
    }

    public final String back() {
        incrementPopContextOnNextPauseCounter();
        onBack();
        return nextView();
    }

    /**
     * No real action was performed, the user just asked to go back.
     */
    protected void onBack() {
    }

    // Context utils

    private void incrementPopContextOnNextPauseCounter() {
        getCurrentConversation().incrementPopContextOnNextPauseCounter();
    }

    private String nextView() {
        return getCurrentConversation().nextView();
    }
}