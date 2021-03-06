/*
**********************************************************************************
|                                                                                |
| Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.             |
|                                                                                |
| Licensed under the Apache License, Version 2.0 (the "License");                |
| you may not use this file except in compliance with the License.               |
| You may obtain a copy of the License at                                        |
|                                                                                |
| http://www.apache.org/licenses/LICENSE-2.0                                     |
|                                                                                |
| Unless required by applicable law or agreed to in writing, software            |
| distributed under the License is distributed on an "AS IS" BASIS,              |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.       |
| See the License for the specific language governing permissions and            |
| limitations under the License.                                                 |
|                                                                                |
**********************************************************************************
*/

package com.huawei.hitaxicodelab_java.data.model;

import kotlin.jvm.JvmStatic;

public class Resource<T> {
    Status status;
    T data = null;
    Error error = null;

    boolean isStatusSuccess = status == Status.SUCCESSFUL;
    boolean isStatusLoading = status == Status.LOADING;
    boolean isStatusError = status == Status.ERROR;

    public Resource(Status status, T data) {
        this.status = status;
        this.data = data;
    }

    public static Resource<Driver> success(Status successful, Driver driver, Error testErrorMessage) {
        return Resource.success(successful, driver, testErrorMessage);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public Error getError() {
        return error;
    }

    public boolean isStatusSuccess() {
        return isStatusSuccess;
    }

    public boolean isStatusLoading() {
        return isStatusLoading;
    }

    public boolean isStatusError() {
        return isStatusError;
    }

    public static Resource success(DirectionRequest directionRequest) {
        return Resource.success(directionRequest);
    }

    @JvmStatic
    public Resource success(T data) {
        return new Resource(Status.SUCCESSFUL, data);
    }


    @JvmStatic
    public Resource<T> progress(T data) {
        return new Resource(Status.LOADING, data);
    }


    @JvmStatic
    public Resource<T> error(T data) {
        return new Resource(Status.ERROR, data);
    }
}

