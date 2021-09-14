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
package com.huawei.hitaxicodelab.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.huawei.hitaxicodelab.data.model.*
import com.huawei.hitaxicodelab.data.repository.MapRepositoryImpl
import com.huawei.hitaxicodelab.service.LocationService
import com.huawei.hitaxicodelab.ui.base.BaseViewModel
import com.huawei.hitaxicodelab.data.model.CarTypes
import com.huawei.hitaxicodelab.utils.GeocodeHelper
import com.huawei.hitaxicodelab.utils.toCoordinate
import com.huawei.hms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val locationService: LocationService,
    private val mapRepositoryImpl: MapRepositoryImpl,
    private val geocodeHelper: GeocodeHelper
) : BaseViewModel() {

    lateinit var driver: Driver

    private val _isDestinationSelectedBefore = MutableLiveData(false)
    val isDestinationSelectedBefore: LiveData<Boolean>
        get() = _isDestinationSelectedBefore

    private val _startingAddress = MutableLiveData<String>()
    val startingAddress: LiveData<String>
        get() = _startingAddress

    private val _startingLocation = MutableLiveData<LatLng>()
    val startingLocation: LiveData<LatLng>
        get() = _startingLocation

    private val _destinationAddress = MutableLiveData<String>()
    val destinationAddress: LiveData<String>
        get() = _destinationAddress

    private val _destinationLocation = MutableLiveData<LatLng>()
    val destinationLocation: LiveData<LatLng>
        get() = _destinationLocation

    private val _lastKnownLocation = MutableLiveData<LatLng?>()
    val lastKnownLocation: LiveData<LatLng?>
        get() = _lastKnownLocation

    private val _selectedCarType = MutableLiveData(CarTypes.CLASSIC)
    val selectedCardType: LiveData<CarTypes>
        get() = _selectedCarType

    private val _isTripStarted = MutableLiveData(false)
    val isTripStarted: LiveData<Boolean>
        get() = _isTripStarted

    private val _isTripFinished = MutableLiveData(false)
    val isTripFinished: LiveData<Boolean>
        get() = _isTripFinished

    fun getRoute() = liveData(Dispatchers.IO) {
        emit(Resource.progress())
        try {
            emit(
                Resource.success(
                    data = mapRepositoryImpl.getRoute(
                        DirectionRequest(
                            startingLocation.value.toCoordinate(),
                            destinationLocation.value.toCoordinate()
                        )
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error<DirectionResponse>(Error()))
        }
    }

    private val _tripRoute = MutableLiveData<Route>()
    val tripRoute: LiveData<Route>
        get() = _tripRoute

    fun setTripRoute(route: Route?) {
        _tripRoute.value = route
    }

    fun setIsDestinationSelectedBefore(value: Boolean) {
        _isDestinationSelectedBefore.value = value
    }

    fun setStartingAddress(address: String) {
        _startingAddress.value = address
    }

    fun setDestinationAddress(address: String) {
        _destinationAddress.value = address
    }

    fun setStartingLocation(latLng: LatLng?) {
        _startingLocation.value = latLng
    }

    fun setDestinationLocation(latLng: LatLng?) {
        _destinationLocation.value = latLng
    }

    fun setLastKnownLocation(latLng: LatLng?) {
        _lastKnownLocation.value = latLng
    }

    fun setSelectedCarType(carType: CarTypes) {
        _selectedCarType.value = carType
    }



    fun setIsTripStarted(isTripStarted: Boolean) {
        _isTripStarted.value = isTripStarted
    }

    fun setIsTripFinished(isTripFinished: Boolean) {
        _isTripFinished.value = isTripFinished
    }

    fun getAddress(location: LatLng) = geocodeHelper.getNameFromLocation(location)

    fun checkPermissions() = locationService.checkPermission()

    fun clearViewModel() {
        setIsDestinationSelectedBefore(false)
        setStartingAddress("")
        setDestinationAddress("")
        setStartingLocation(null)
        setDestinationLocation(null)
        setLastKnownLocation(null)
        setSelectedCarType(CarTypes.CLASSIC)
        setIsTripStarted(false)
        setIsTripFinished(false)
    }

    fun driverData() = liveData(Dispatchers.IO) {
        val a : Resource<Driver> = Resource(status = Status.SUCCESSFUL,
            data = selectedCardType.value?.ordinal?.let {
                Driver(1,"John", "Adams", "picture", 24, 1, 4.8F, 42,
                    it+1, "34 XYZ 123", 4.2F, 55, "Toyota Corolla")
            },
            error = Error(27,"Test Error Message"))
        emit(a)
    }

}