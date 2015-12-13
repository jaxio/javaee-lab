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
$output.java($WebConversation, "UnexpectedConversationException")##

/**
 * Exception thrown when the end user requests an unexpected URL, that is an URL which is out of sync with the current conversation.
 */
public class $output.currentClass extends Exception {
    private static final long serialVersionUID = 1L;
    private final String redirectUrl;

    public ${output.currentClass}(String reason, String unexpectedUrl, String redirectUrl) {
        super(reason + ". requested url: " + unexpectedUrl + " => we redirect her to " + redirectUrl);
        this.redirectUrl = redirectUrl;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
}