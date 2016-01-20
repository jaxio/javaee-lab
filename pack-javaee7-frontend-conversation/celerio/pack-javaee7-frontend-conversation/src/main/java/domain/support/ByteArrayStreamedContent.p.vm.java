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
$output.java($WebModelSupport, "ByteArrayStreamedContent")##
$output.require("java.io.ByteArrayInputStream")##
$output.require("java.io.InputStream")##
$output.require("org.primefaces.model.StreamedContent")##
$output.require($WebUtil, "DownloadUtil")##

/**
 * StreamedContent that lazily loads the binary content.
 */
public abstract class $output.currentClass implements StreamedContent {
    private String contentType = "application/download";
    private String name;
    private String contentEncoding;

    @Override
    public InputStream getStream() {
        DownloadUtil.forceResponseHeaderForDownload();        
        return new ByteArrayInputStream(getByteArray()); 
    }

    /**
     * Lazily load the binary content.
     */
    protected abstract byte[] getByteArray();

    @Override
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }    

    @Override
    public String getContentEncoding() {
        return contentEncoding;
    }
}