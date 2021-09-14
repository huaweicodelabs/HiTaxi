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

package com.huawei.hitaxicodelab_java.service;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.maps.model.LatLng;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class LocationService {

    private Context context;
    private final String tag = "LocationService";

    @Inject
    public LocationService
            (
                    @ApplicationContext Context context

            ) {
        this.context = context;

    }



    public Boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    || androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getLastLocation(Consumer<LatLng> function ) {
        FusedLocationProviderClient mFusedLocationProviderClient;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);


        try {
            Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();
            lastLocation.addOnSuccessListener(it -> {
                if (it == null) {
                    function.accept(new LatLng(0.0, 0.0));
                } else {
                    function.accept(new LatLng(it.getLatitude(), it.getLongitude()));
                }
            }).addOnFailureListener(it -> Log.e(tag, "getLastLocation exception:" + it.getMessage()));
        } catch (Exception e) {
            Log.e(tag, "getLastLocation exception:" + e.getMessage());
        }
    }
}
