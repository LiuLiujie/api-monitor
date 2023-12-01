/*
 *
 *    Copyright 2023 Yujie Liu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.yujieliu.apimonitor.communication.domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SimpleHTTPAPI extends BaseAPI {

    Request request;

    Response response;

    public SimpleHTTPAPI(String id, String name, String schema, String version, String server, String method, int status){
        super(id, name, schema, version);
        this.request = new Request();
        this.request.server = server;
        this.request.method = method;
        this.response = new Response();
        this.response.status = status;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Request {
        String server;

        String method;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Response {
        Integer status;
    }
}
