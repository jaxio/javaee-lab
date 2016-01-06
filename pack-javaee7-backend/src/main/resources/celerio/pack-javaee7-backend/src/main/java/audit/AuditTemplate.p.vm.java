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
$output.java($Audit, "AuditTemplate")##

$output.require("javax.persistence.PostUpdate")##
$output.require("javax.persistence.PreUpdate")##

/**
 * Execute an {@link AuditCallback} to enable/disable audit in {@link PreUpdate} and {@link PostUpdate} actions and/or force username to be used.
 * The {@link AuditTemplate} will be in charge of cleaning up the {@link AuditContextHolder} state.
 */
public class $output.currentClass {
	public interface AuditCallback<T> {
		T doInAudit() throws Exception;
	}

	final boolean auditing;
	final String username;

	/**
	 * Enable or not audit
	 */
	public AuditTemplate(boolean auditing) {
		this(auditing, null);
	}

	/**
	 * Enable audit, and specify a username to be used
	 */
	public AuditTemplate(String username) {
		this(true, username);
	}

	private AuditTemplate(boolean auditing, String username) {
		this.auditing = auditing;
		this.username = username;
	}

	public <T> T execute(AuditCallback<T> callback) throws Exception {
		boolean previousState = AuditContextHolder.audit();
		String previousUsername = AuditContextHolder.username();
		AuditContextHolder.setAudit(auditing);
		AuditContextHolder.setUsername(username);
		try {
			return callback.doInAudit();
		} finally {
			AuditContextHolder.setAudit(previousState);
			AuditContextHolder.setUsername(previousUsername);
		}
	}
}