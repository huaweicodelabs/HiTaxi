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

import android.app.Activity;
import android.content.IntentSender;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.model.LatLng;

import java.util.function.Consumer;

public class LocationUtil{

    private final Activity context;

    public LocationUtil(Activity contex){
        this.context = contex;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void checkLocationSettings(Consumer<LatLng> function) {
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        FusedLocationProviderClient fusedLocationProviderClient;
        LocationRequest mLocationRequest;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        LocationCallback mLocationCallback;
        mLocationCallback = new LocationCallback(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                function.accept(new LatLng(
                        locationResult.getLastHWLocation().getLatitude(),
                        locationResult.getLastHWLocation().getLongitude()
                ));
            }
        };

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mLocationRequest = new LocationRequest();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(it ->
                        fusedLocationProviderClient
                                .requestLocationUpdates(
                                        mLocationRequest,
                                        mLocationCallback,
                                        Looper.getMainLooper()
                                ))
                .addOnFailureListener(e -> {
                    int statusCode = ((ApiException)e).getStatusCode();
                    if (statusCode == com.huawei.hms.common.api.CommonStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            ResolvableApiException rae = ((ResolvableApiException) e);
                            rae.startResolutionForResult(context, 0);
                            function.accept(new LatLng(0.0, 0.0));
                        } catch (IntentSender.SendIntentException ise) {
                            function.accept(new LatLng(0.0, 0.0));
                        }
                    }
                });
    }
}
