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

package com.yujieliu.apimonitor.communication.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestResponseEntity<T> {
    /**
     * Status Code
     */
    protected Code code;

    /**
     * Message
     */
    protected String msg;

    /**
     * Data
     */
    protected T data;

    private RestResponseEntity(Code code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private RestResponseEntity(Code code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> RestResponseEntity<T> sendOK(T data) {
        return new RestResponseEntity<>(Code.OK, "ok", data);
    }

    public static <T> RestResponseEntity<T> sendBadOperation(String msg) {
        return new RestResponseEntity<>(Code.BAD_OPERATION, msg);
    }

    public static <T> RestResponseEntity<T> sendBadRequest(String msg) {
        return new RestResponseEntity<>(Code.BAD_REQUEST, msg);
    }
}
