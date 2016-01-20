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
$output.java($WebFaces, "CustomExceptionHandler")##

$output.require("java.util.Iterator")##
$output.require("javax.faces.context.ExceptionHandler")##
$output.require("javax.faces.context.ExceptionHandlerWrapper")##
$output.require("javax.faces.event.ExceptionQueuedEvent")##
$output.require("javax.persistence.OptimisticLockException")##
$output.require($WebUtil, "ExceptionUtil")##
$output.require($WebUtil, "MessageUtil")##

/**
 * Exception handling is configured here, in web.xml (see error-page tag) and in faces-config.xml.
 */
public class $output.currentClass extends ExceptionHandlerWrapper {

    private ExceptionHandler wrapped;

    /**
     * Construct a new exception handler around the given wrapped exception handler.
     * @param wrapped The wrapped exception handler.
     */
    public ${output.currentClass}(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void handle() {
        Iterator<ExceptionQueuedEvent> unhandledExceptionQueuedEvents = getUnhandledExceptionQueuedEvents().iterator();

        if (unhandledExceptionQueuedEvents.hasNext()) {
            Throwable e = unhandledExceptionQueuedEvents.next().getContext().getException();

            // map general purpose exception to error message
            if (ExceptionUtil.isCausedBy(e, OptimisticLockException.class)) {
                MessageUtil.getInstance().error("error_concurrent_modification");
                unhandledExceptionQueuedEvents.remove();
            }
//            else if (ExceptionUtil.isCausedBy(e, DataIntegrityViolationException.class)) {
//                MessageUtil.getInstance().error("error_data_integrity_violation");
//                unhandledExceptionQueuedEvents.remove();
//            } else if (ExceptionUtil.isCausedBy(e, AccessDeniedException.class)) {
//                // works only if the spring security filter is before the exception filter, 
//                // that is if the exception filter handles the exception first.
//                MessageUtil.getInstance().error("error_access_denied");
//                unhandledExceptionQueuedEvents.remove();
//            }
            // exception will be handled by the wrapped exception handler.
        }

        wrapped.handle();
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }
}
