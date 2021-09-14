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

package com.huawei.hitaxicodelab_java.utils;

import android.graphics.Color;

import com.huawei.hitaxicodelab_java.R;
import com.huawei.hitaxicodelab_java.data.model.Car;


public enum CarTypes {
    CLASSIC,
    CABIN,
    PREMIUM;


    public static CarTypes getCarType(Integer value) {
        CarTypes carTypes;
        switch (value) {
            case 1:
                carTypes = CarTypes.CLASSIC;
                break;
            case 2:
                carTypes = CarTypes.CABIN;
                break;
            case 3:
                carTypes = CarTypes.PREMIUM;
                break;
            default:
                carTypes = CarTypes.CLASSIC;
        }
        return carTypes;
    }

    public Car getCar() {
        Car car;

        switch (this) {
            case CLASSIC:
                car = new Car(
                        R.drawable.ic_classic_car,
                        R.string.classic_car,
                        Color.YELLOW,
                        3.50
                );
                break;
            case CABIN:
                car = new Car(
                        R.drawable.ic_economic_car,
                        R.string.cabin_car,
                        Color.GRAY,
                        4.50
                );
                break;
            case PREMIUM:
                car = new Car(
                        R.drawable.ic_premium_car,
                        R.string.premium_car,
                        Color.BLACK,
                        5.50
                );
                break;
            default:
                car = new Car(
                        R.drawable.ic_classic_car,
                        R.string.classic_car,
                        Color.YELLOW,
                        3.50
                );
                break;
        }
        return car;

    }

}
