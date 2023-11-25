package com.yujieliu.apimonitor.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class SimpleAPI extends BaseAPI {

    Request request;

    Response response;

    public SimpleAPI(String name, String schema, String server, String method, int status){
        super(name, schema);
        this.request = new Request();
        request.server = server;
        request.method = method;
        this.response = new Response();
        response.status = status;
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
