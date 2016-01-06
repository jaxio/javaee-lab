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
$output.java($WebUtil, "MessagesHelper")##

$output.requireStatic("com.google.common.collect.Collections2.filter")##
$output.requireStatic("com.google.common.collect.Collections2.transform")##
$output.requireStatic("com.google.common.collect.Iterables.getFirst")##
$output.requireStatic("com.google.common.collect.Iterables.toArray")##
$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.requireStatic("com.google.common.collect.Sets.newHashSet")##
$output.requireStatic("java.util.Arrays.asList")##
$output.requireStatic("java.util.Collections.reverseOrder")##
$output.require("java.util.Collection")##
$output.require("java.util.Collections")##
$output.require("java.util.Comparator")##
$output.require("java.util.Iterator")##
$output.require("java.util.List")##
$output.require("javax.inject.Inject")##
$output.require("javax.faces.application.FacesMessage")##
$output.require("javax.faces.application.FacesMessage.Severity")##
$output.require("javax.faces.context.FacesContext")##
$output.require("com.google.common.base.Function")##
$output.require("com.google.common.base.Predicate")##
$output.require($Util, "ResourcesUtil")##

/**
 * Helper used from the <code>appcc:messages</code> composite component.
 */
$output.dynamicAnnotationTakeOver("javax.enterprise.context.ApplicationScoped","javax.inject.Named")##
public class $output.currentClass {

    @Inject
    protected ResourcesUtil resourcesUtil;

    public String getMaxSeverityNonGlobalMessages() {
        Severity maxSeverity = getFirst(getSeveritiesNonGlobalMessages(), FacesMessage.SEVERITY_INFO);
        return MessageUtil.toCssFriendly(maxSeverity);
    }

    public String getCssFriendlySeverity(Severity severity) {
        return MessageUtil.toCssFriendly(severity);
    }

    /**
     * @return sorted list of severities in descending order
     */
    public List<Severity> getSeveritiesGlobalMessages() {
        return getSeverities(asList(getGlobalMessages()));
    }

    /**
     * @return sorted list of severities in descending order
     */
    public List<Severity> getSeveritiesNonGlobalMessages() {
        return getSeverities(asList(getNonGlobalMessages()));
    }

    /**
     * @return descending sorted and distinct list of severities
     */
    protected List<Severity> getSeverities(Collection<Message> messages) {
        Collection<Severity> severities = transform(messages, messageFunction);
        List<Severity> sortedSeverities = newArrayList(newHashSet(severities));
        Collections.sort(sortedSeverities, reverseOrder(severityComparator));
        return sortedSeverities;
    }

    private static Function<Message, Severity> messageFunction = new Function<Message, Severity>() {
        @Override
        public Severity apply(Message input) {
            return input.getFacesMessage().getSeverity();
        }
    };

    private static Comparator<Severity> severityComparator = new Comparator<Severity>() {
        @Override
        public int compare(Severity s1, Severity s2) {
            return Integer.valueOf(s1.getOrdinal()).compareTo(s2.getOrdinal());
        }
    };

    public Message[] getGlobalMessages() {
        List<Message> res = newArrayList();
        Iterator<FacesMessage> msgs = FacesContext.getCurrentInstance().getMessages(null);
        while (msgs.hasNext()) {
            res.add(new Message(null, msgs.next()));
        }
        return toArray(res, Message.class);
    }

    public Message[] getGlobalMessages(Severity severity) {
        Collection<Message> res = filter(asList(getGlobalMessages()), newSeverityPredicate(severity));
        return toArray(res, Message.class);
    }

    public Message getSingleGlobalMessage(Severity severity) {
        Message[] messages = getGlobalMessages(severity);
        return messages != null && messages.length == 1 ? messages[0] : null;
    }

    private Predicate<Message> newSeverityPredicate(final Severity severity){
        return new Predicate<Message>() {
            @Override
            public boolean apply(Message input) {
                return severity == input.getFacesMessage().getSeverity();
            }
        };
    }

    public Message[] getNonGlobalMessages() {
        List<Message> res = newArrayList();

        Iterator<String> ids = FacesContext.getCurrentInstance().getClientIdsWithMessages();
        while (ids.hasNext()) {
            String clientId = ids.next();
            if (clientId != null && !clientId.equals("null")) { /* the 'null' string is pretty disturbing */
                Iterator<FacesMessage> msgs = FacesContext.getCurrentInstance().getMessages(clientId);
                while (msgs.hasNext()) {
                    res.add(new Message(clientId, msgs.next()));
                }
            }
        }

        return toArray(res, Message.class);
    }

    public boolean hasGlobalMessages() {
        return FacesContext.getCurrentInstance().getMessages(null).hasNext();
    }

    public boolean hasMultipleGlobalMessages(Severity severity) {
        return getGlobalMessages(severity).length > 1;
    }
    
    public boolean hasSingleGlobalMessage(Severity severity) {
        return getGlobalMessages(severity).length == 1;
    }

    public boolean hasNonGlobalMessages() {
        Iterator<String> ids = FacesContext.getCurrentInstance().getClientIdsWithMessages();
        while (ids.hasNext()) {
            String clientId = ids.next();
            if (clientId != null && !clientId.equals("null")) { /* the 'null' string is pretty disturbing */
                return true;
            }
        }
        return false;
    }

    public boolean hasNoMessages() {
        return !(hasGlobalMessages() || hasNonGlobalMessages());
    }

    public boolean hasOnlyGlobalMessages() {
        return hasGlobalMessages() && !hasNonGlobalMessages();
    }

    public String getNonGlobalMessagesIntro() {
        return resourcesUtil.getPluralableProperty("form_error_status", nonGlobalMessagesCount());
    }

    public int nonGlobalMessagesCount() {
        int count = 0;
        Iterator<String> ids = FacesContext.getCurrentInstance().getClientIdsWithMessages();
        while (ids.hasNext()) {
            String clientId = ids.next();
            if (clientId != null && !clientId.equals("null")) { /* the 'null' string is pretty disturbing */
                count += FacesContext.getCurrentInstance().getMessageList(clientId).size();
            }
        }
        return count;
    }
}
