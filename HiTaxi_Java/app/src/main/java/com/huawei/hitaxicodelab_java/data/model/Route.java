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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.huawei.hms.site.api.model.CoordinateBounds;

import java.util.List;

public class Route {
    @SerializedName("paths")
    @Expose
    public List<Path> paths;
    @SerializedName("optimizedWaypoints")
    @Expose
    public List<Integer> optimizedWaypoints;
    @SerializedName("bounds")
    @Expose
    public CoordinateBounds bounds;
    @SerializedName("hasRestrictedRoad")
    @Expose
    public Integer hasRestrictedRoad;
    @SerializedName("dstInRestrictedArea")
    @Expose
    public Integer dstInRestrictedArea;
    @SerializedName("crossCountry")
    @Expose
    public Integer crossCountry;
    @SerializedName("crossMultiCountries")
    @Expose
    public Integer crossMultiCountries;
    @SerializedName("hasRoughRoad")
    @Expose
    public Integer hasRoughRoad;
    @SerializedName("dstInDiffTimeZone")
    @Expose
    public Integer dstInDiffTimeZone;
    @SerializedName("hasFerry")
    @Expose
    public Integer hasFerry;
    @SerializedName("hasTrafficLight")
    @Expose
    public Integer hasTrafficLight;
    @SerializedName("hasTolls")
    @Expose
    public Integer hasTolls;
    @SerializedName("trafficLightNum") @Expose public Integer trafficLightNum;
}
