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
$output.java($Util, "StartupBeanExtension")##

$output.require("java.util.LinkedHashSet")##
$output.require("java.util.Set")##
$output.require("javax.enterprise.context.ApplicationScoped")##
$output.require("javax.enterprise.event.Observes")##
$output.require("javax.enterprise.inject.spi.AfterDeploymentValidation")##
$output.require("javax.enterprise.inject.spi.Bean")##
$output.require("javax.enterprise.inject.spi.BeanManager")##
$output.require("javax.enterprise.inject.spi.Extension")##
$output.require("javax.enterprise.inject.spi.ProcessBean")##
$output.require("javax.inject.Singleton")##

/**
 * https://gist.github.com/mojavelinux/635719
 */
public class StartupBeanExtension implements Extension {
    private final Set<Bean<?>> startupBeans = new LinkedHashSet<Bean<?>>();

    <X> void processBean(@Observes ProcessBean<X> event) {

        if (event.getAnnotated().isAnnotationPresent(Startup.class)
                && (event.getAnnotated().isAnnotationPresent(ApplicationScoped.class) || event.getAnnotated().isAnnotationPresent(Singleton.class))) {
            startupBeans.add(event.getBean());
        }
    }

    void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager manager) {
        for (Bean<?> bean : startupBeans) {
            // the call to toString() is a cheat to force the bean to be initialized
            manager.getReference(bean, bean.getBeanClass(), manager.createCreationalContext(bean)).toString();
        }
    }
}
