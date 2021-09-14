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
package com.huawei.hitaxicodelab.data.model

import android.graphics.Color
import com.huawei.hitaxicodelab.HiTaxiCodelabApplication
import com.huawei.hitaxicodelab.R

enum class CarTypes(val car: Car) {
    CLASSIC(
        Car(
            R.drawable.ic_classic_car,
            HiTaxiCodelabApplication.instance.getString(R.string.classic_car),
            Color.YELLOW,
            3.50
        )
    ),
    CABIN(
        Car(
            R.drawable.ic_economic_car,
            HiTaxiCodelabApplication.instance.getString(R.string.cabin_car),
            Color.GRAY,
            4.50
        )
    ),
    PREMIUM(
        Car(
            R.drawable.ic_premium_car,
            HiTaxiCodelabApplication.instance.getString(R.string.premium_car),
            Color.BLACK,
            5.50
        )
    );

    companion object {
        fun getCarType(value: Int): CarTypes {
            return when (value) {
                1 -> CLASSIC
                2 -> CABIN
                3 -> PREMIUM
                else -> CLASSIC
            }
        }
    }
}