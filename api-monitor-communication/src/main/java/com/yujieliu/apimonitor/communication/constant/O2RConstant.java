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

package com.yujieliu.apimonitor.communication.constant;

public class O2RConstant {

    public static final String SIMPLE_HTTP_API_TOPIC = "SimpleHttpAPI";

    public static final String SIMPLE_HTTP_RESULT_TOPIC = "SimpleHttpResult";

    public static final String RUNNER_ERROR_TOPIC = "RunnerError";

    public static final String API_ERROR = "Error when handling API";

    public static final String RESULT_ERROR = "Error when constructing result";

    public static final String HTTP_REGISTER_API = "/runner/http/register";

    public static final String HTTP_HEARTBEAT_SIMPLE_HTTP_API = "/runner/http/heartbeat/http/simple";

    public static final String HTTP_REGISTER_ERROR = "Invalid Registration";

    public static final String HTTP_HEARTBEAT_ERROR = "Invalid HeartBeat";

    public static final String HTTP_AUTH_FAILURE = "Auth Failure";
}
