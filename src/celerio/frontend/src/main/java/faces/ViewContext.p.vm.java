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
$output.java($WebFaces, "ViewContext")##

$output.require("java.util.Map")##
$output.require("javax.enterprise.context.spi.Context")##
$output.require("javax.enterprise.context.spi.Contextual")##
$output.require("javax.enterprise.context.spi.CreationalContext")##
$output.require("javax.enterprise.inject.spi.Bean")##
$output.require("javax.faces.component.UIViewRoot")##
$output.require("javax.faces.context.FacesContext")##

public class $output.currentClass implements Context {

    @Override
    public Class<ViewScoped> getScope() {
        return ViewScoped.class;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Bean bean = (Bean) contextual;
        Map viewMap = getViewMap();
        if(viewMap.containsKey(bean.getName())) {
            return (T) viewMap.get(bean.getName());
        } else {
            T t = (T) bean.create(creationalContext);
            viewMap.put(bean.getName(), t);
            return t;
        }
    }

    @SuppressWarnings("rawtypes")
    private Map getViewMap() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = fctx.getViewRoot();
        return viewRoot.getViewMap(true);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T get(Contextual<T> contextual) {
        Bean bean = (Bean) contextual;
        Map viewMap = getViewMap();
        if(viewMap.containsKey(bean.getName())) {
            return (T) viewMap.get(bean.getName());
        } else {
            return null;
        }
    }

    public boolean isActive() {
        return true;
    }
}