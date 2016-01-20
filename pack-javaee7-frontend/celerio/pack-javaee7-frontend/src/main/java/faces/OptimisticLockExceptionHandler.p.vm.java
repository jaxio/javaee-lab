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
$output.java($WebFaces, "OptimisticLockExceptionHandler")##

$output.require("javax.faces.context.ExceptionHandler")##
$output.require("javax.faces.context.ExceptionHandlerWrapper")##
$output.require("javax.faces.event.ExceptionQueuedEvent")##
$output.require("javax.persistence.OptimisticLockException")##
$output.require("com.google.common.base.Predicate")##
$output.require("com.google.common.base.Predicates")##
$output.require("com.google.common.collect.Iterables")##
$output.require($WebUtil, "ExceptionUtil")##

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
        Iterable<ExceptionQueuedEvent> optimisticLockEvents = Iterables.filter(getUnhandledExceptionQueuedEvents(), optimisticLockCausePredictate);
        
        if(!Iterables.isEmpty(optimisticLockEvents)){
            Iterables.removeIf(getUnhandledExceptionQueuedEvents(), Predicates.alwaysTrue());
        }
        
        wrapped.handle();
    }

    /**
     * <code>true</code> if exception cause is of type {@link OptimisticLockException}
     */
    private Predicate<ExceptionQueuedEvent> optimisticLockCausePredictate = new Predicate<ExceptionQueuedEvent>() {
            @Override
            public boolean apply(ExceptionQueuedEvent event) {
                Throwable exception = event.getContext().getException();
                return ExceptionUtil.isCausedBy(exception, OptimisticLockException.class);
            }
    };
    
    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }
}
