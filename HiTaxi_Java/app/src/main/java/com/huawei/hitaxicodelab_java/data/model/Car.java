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

public class Car {

    int iconId;
    Integer title;
    Integer separatorColorId;
    Double costForKm;

    public Car(int iconId, int title, Integer separatorColorId, Double costForKm) {
        this.iconId = iconId;
        this.title = title;
        this.separatorColorId = separatorColorId;
        this.costForKm = costForKm;
    }


    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public Integer getTitle() {
        return title;
    }

    public void setTitle(Integer title) {
        this.title = title;
    }

    public Integer getSeparatorColorId() {
        return separatorColorId;
    }

    public void setSeparatorColorId(Integer separatorColorId) {
        this.separatorColorId = separatorColorId;
    }

    public Double getCostForKm() {
        return costForKm;
    }

    public void setCostForKm(Double costForKm) {
        this.costForKm = costForKm;
    }
}
