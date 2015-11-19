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
$output.generateIf($project.isAuditLogPresent())##
$output.java($Audit, "AuditLogListener")##

$output.requireStatic("com.google.common.collect.Lists.newArrayList")##
$output.requireStatic($Service, "AuditEvent.Creation")##
$output.requireStatic($Service, "AuditEvent.Deletion")##
$output.requireStatic($Service, "AuditEvent.Modification")##
$output.require("java.util.List")##
$output.require("org.apache.deltaspike.core.api.provider.BeanProvider")##
$output.require("org.hibernate.action.spi.AfterTransactionCompletionProcess")##
$output.require("org.hibernate.engine.spi.SessionImplementor")##
$output.require("org.hibernate.event.spi.AbstractEvent")##
$output.require("org.hibernate.event.spi.PostDeleteEvent")##
$output.require("org.hibernate.event.spi.PostDeleteEventListener")##
$output.require("org.hibernate.event.spi.PostInsertEvent")##
$output.require("org.hibernate.event.spi.PostInsertEventListener")##
$output.require("org.hibernate.event.spi.PreUpdateEvent")##
$output.require("org.hibernate.event.spi.PreUpdateEventListener")##
$output.require("org.hibernate.persister.entity.EntityPersister")##
$output.require($Service, "AuditLogService")##
$output.require($Context, "UserContext")##
$output.require($ModelSupport, "Identifiable")##
$output.require($project.auditLog.model)##
$output.require($Service, "AuditEvent")##
$output.require($Service, "AuditLogService")##

/**
 * This class is declared in persistence.xml.
 */
public class $output.currentClass implements PreUpdateEventListener, PostDeleteEventListener, PostInsertEventListener {
	private static final long serialVersionUID = 1L;
	private AuditLogService auditLogService; // will be lazily initialized
	protected List<String> skipProperties = newArrayList("version", "lastModificationAuthor", "lastModificationDate", "creationDate", "creationAuthor");
	protected List<String> skipClasses = newArrayList("${project.auditLog.model.type}");

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		audit(event, Deletion, event.getEntity());
	}

	@Override
	public void onPostInsert(PostInsertEvent event) {
		audit(event, Creation, event.getEntity());
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		String updateMessage = buildUpdateMessage(event);
		if (!updateMessage.isEmpty()) {
			audit(event, Modification, event.getEntity(), updateMessage);
		}
		return false;
	}

	private String buildUpdateMessage(PreUpdateEvent event) {
		String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
		Object[] oldStates = event.getOldState();
		Object[] newStates = event.getState();
		int index = 0;
		StringBuilder message = new StringBuilder(128);
		for (String propertyName : propertyNames) {
			message.append(message(propertyName, oldStates[index], newStates[index]));
			index++;
		}
		return message.toString();
	}

	private String message(String propertyName, Object oldState, Object newState) {
		if (newState instanceof Identifiable) {
			// no need to track objects
			return "";
		} else if (skipProperties.contains(propertyName)) {
			// no need to track version and lastModificationDates as they add no value
			return "";
		} else if (oldState == null && newState == null) {
			return "";
		} else if (oldState == null && newState != null) {
			return propertyName + " set to [" + newState + "]\n";
		} else if (oldState != null && newState == null) {
			return propertyName + " reseted to null\n";
		} else if (!oldState.toString().equals(newState.toString())) {
			return propertyName + " changed from [" + oldState.toString() + "] to [" + newState.toString() + "]\n";
		} else {
			return "";
		}
	}

    private void audit(AbstractEvent hibernateEvent, AuditEvent auditEvent, Object object) {
        audit(hibernateEvent, auditEvent, object, null);
	}

    private void audit(AbstractEvent hibernateEvent, AuditEvent auditEvent, Object object, String attribute) {
		String className = object.getClass().getSimpleName();
		if (skipClasses.contains(className)) {
			return;
		}

		${project.auditLog.model.type} ${project.auditLog.model.var} = new ${project.auditLog.model.type}();
		${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.author.setter}(UserContext.getUsername());
        ${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.event.setter}(auditEvent.name());
$output.require("$project.auditLog.auditLogAttributes.eventDate.fullType")##
        ${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.eventDate.setter}(new ${project.auditLog.auditLogAttributes.eventDate.type}());
		${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.stringAttribute1.setter}(className);
		${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.stringAttribute2.setter}(((Identifiable<?>) object).${identifiableProperty.getter}().toString());
		${project.auditLog.model.var}.${project.auditLog.auditLogAttributes.stringAttribute3.setter}(attribute);
		audit(hibernateEvent, ${project.auditLog.model.var});
	}

	private void audit(AbstractEvent hibernateEvent, final ${project.auditLog.model.type} ${project.auditLog.model.var}) {
		hibernateEvent.getSession().getActionQueue().registerProcess(new AfterTransactionCompletionProcess() {
			public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
				if (success) {
				    if (auditLogService == null) {
				        // lazy init as CDI is not ready at instanciation time!
				        auditLogService = BeanProvider.getContextualReference(AuditLogService.class, false);
				    }
					auditLogService.log(${project.auditLog.model.var});
				}
			}
		});
	}
	
    @Override
    public boolean requiresPostCommitHanding(EntityPersister arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}