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
$output.java($WebFaces, "CustomExceptionHandlerFactory")##

$output.require("javax.faces.context.ExceptionHandler")##
$output.require("javax.faces.context.ExceptionHandlerFactory")##

public class $output.currentClass extends ExceptionHandlerFactory{

    private ExceptionHandlerFactory wrapped;

    /**
     * Construct a new full conversation aware exception handler factory around the given wrapped factory.
     * @param wrapped The wrapped factory.
     */
    public ${output.currentClass}(ExceptionHandlerFactory wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Returns a new instance of {@link CustomExceptionHandler} which wraps the original exception handler.
     */
    @Override
    public ExceptionHandler getExceptionHandler() {
        return new CustomExceptionHandler(wrapped.getExceptionHandler());
    }

    /**
     * Returns the wrapped factory.
     */
    @Override
    public ExceptionHandlerFactory getWrapped() {
        return wrapped;
    }
}