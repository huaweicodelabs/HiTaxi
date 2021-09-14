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

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.huawei.hitaxicodelab_java.R;
import com.huawei.hms.maps.model.LatLng;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class GeocoderHelper {
    private Context context;
    private Geocoder geocoder;

    @Inject
    public GeocoderHelper(Geocoder geocoder, @ApplicationContext Context context) {
        this.geocoder = geocoder;
        this.context = context;
    }

    public String getNameFromLocation(LatLng latLng) {
        String result = context.getString(R.string.unknown_location);
        try {
            Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
            if (address != null) {
                result = address.getSubLocality() + ", " + address.getThoroughfare() + ", " + address.getSubThoroughfare();
                return result.replace(", null", "");
            } else {
                return result;
            }
        } catch (Exception e) {
            return result;
        }
    }

}
