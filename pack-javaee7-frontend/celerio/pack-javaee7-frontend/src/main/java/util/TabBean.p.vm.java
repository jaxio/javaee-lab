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
$output.java($WebUtil, "TabBean")##

$output.require("java.io.Serializable")##

/**
 * Tracks the <code>p:tabView</code> active index.
 */
public class ${output.currentClass} implements Serializable {
    private static final long serialVersionUID = 1L;

    private int activeIndex = 0;

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public int getActiveIndex() {
        return activeIndex;
    }
}
