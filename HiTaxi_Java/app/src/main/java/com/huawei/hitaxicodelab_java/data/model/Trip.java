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

import java.io.Serializable;

public class Trip implements Serializable {
    Coordinate startingLocation;
    String startingAddress;
    Coordinate destinationLocation;
    String destinationAddress;
    Double cost;
    Route route;
    Driver driver;

    public Trip(Coordinate startingLocation, String startingAddress, Coordinate destinationLocation, String destinationAddress, Double cost, Route route, Driver driver) {
        this.startingLocation = startingLocation;
        this.startingAddress = startingAddress;
        this.destinationLocation = destinationLocation;
        this.destinationAddress = destinationAddress;
        this.cost = cost;
        this.route = route;
        this.driver = driver;
    }

    public Route getRoute() {
        return route;
    }

    public Driver getDriver() {
        return driver;
    }
}
