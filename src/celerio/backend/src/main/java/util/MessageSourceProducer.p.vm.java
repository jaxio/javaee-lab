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
$output.java($Util, "MessageSourceProducer")##

$output.require("javax.enterprise.context.ApplicationScoped")##
$output.require("javax.enterprise.inject.Produces")##

public class $output.currentClass {

    @Produces
    @ApplicationScoped
    public MessageSource createMessageSource() {
        MessageSource messageSource = new DefaultMessageSource();
        messageSource.setBasenames( //
                // global
                "localization/messages", //
                "localization/application", //                
#foreach($entity in $project.withoutManyToManyJoinEntities.list)
#if ($velocityCount == 1)
                // entities
#end
                "localization/domain/$entity.model.type", //
#end
#foreach($enumType in $project.enumTypes)
#if ($velocityCount == 1)
                // enums
#end
                "localization/domain/$enumType.model.type", //
#end
                // pages
                "localization/pages/concurrentModificationResolution", //
                "localization/pages/home", // 
                "localization/pages/login", //
                // validation
                "ValidationMessages", //
                "javax/faces/Messages", // 
                "org/hibernate/validator/ValidationMessages");
        return messageSource;
    }
}