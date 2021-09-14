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
package com.huawei.hitaxicodelab.service

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.huawei.hmf.tasks.Task
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val TAG = "LocationService"

    fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    fun getLastLocation(lastKnownLocation: (LatLng) -> Unit) {
        val mFusedLocationProviderClient: FusedLocationProviderClient? = LocationServices.getFusedLocationProviderClient(context)

        var latLng: LatLng
        try {
            val lastLocation: Task<Location> = mFusedLocationProviderClient!!.lastLocation
            lastLocation.addOnSuccessListener {
                if (it == null) {
                    latLng = LatLng(0.0, 0.0)
                    lastKnownLocation(latLng)
                } else {
                    latLng = LatLng(it.latitude, it.longitude)
                    lastKnownLocation(latLng)
                }
            }.addOnFailureListener {
                Log.e(TAG, "getLastLocation exception:" + it.message)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getLastLocation exception:" + e.message)
        }
    }
}