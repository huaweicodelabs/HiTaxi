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

package com.huawei.hitaxicodelab_java.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huawei.hitaxicodelab_java.data.model.DirectionRequest;
import com.huawei.hitaxicodelab_java.data.model.DirectionResponse;
import com.huawei.hitaxicodelab_java.data.model.Driver;
import com.huawei.hitaxicodelab_java.data.model.Resource;
import com.huawei.hitaxicodelab_java.data.model.Route;
import com.huawei.hitaxicodelab_java.data.model.Status;
import com.huawei.hitaxicodelab_java.data.repository.MapRepositoryImpl;
import com.huawei.hitaxicodelab_java.service.LocationService;
import com.huawei.hitaxicodelab_java.ui.base.BaseViewModel;
import com.huawei.hitaxicodelab_java.utils.CarTypes;
import com.huawei.hitaxicodelab_java.utils.GeocoderHelper;
import com.huawei.hitaxicodelab_java.utils.Utils;
import com.huawei.hms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Observable;

@HiltViewModel
public class HomeViewModel extends BaseViewModel {

    private final MutableLiveData<Boolean> isDestinationSelectedBefore;
    private final MutableLiveData<String> startingAddress;
    private final MutableLiveData<LatLng> startingLocation;
    private final MutableLiveData<String> destinationAddress;
    private final MutableLiveData<LatLng> destinationLocation;
    private final MutableLiveData<LatLng> lastKnownLocation;
    private final MutableLiveData<CarTypes> selectedCarType;
    private final MutableLiveData<Boolean> isTripStarted;
    private final MutableLiveData<Route> tripRoute;
    LocationService locationService;
    MapRepositoryImpl mapRepositoryImpl;
    GeocoderHelper geocoderHelper;
    Driver driver;
    private MutableLiveData<Boolean> isTripFinished;

    @Inject
    public HomeViewModel(LocationService locationService,
                         MapRepositoryImpl mapRepositoryImpl,
                         GeocoderHelper geocoderHelper) {

        this.locationService = locationService;
        this.mapRepositoryImpl = mapRepositoryImpl;
        this.geocoderHelper = geocoderHelper;
        this.isDestinationSelectedBefore = new MutableLiveData<>(false);
        this.startingAddress = new MutableLiveData<>();
        this.startingLocation = new MutableLiveData<>();
        this.destinationAddress = new MutableLiveData<>();
        this.destinationLocation = new MutableLiveData<>();
        this.lastKnownLocation = new MutableLiveData<>();
        this.selectedCarType = new MutableLiveData<>(CarTypes.CLASSIC);
        this.isTripStarted = new MutableLiveData<>(false);
        this.isTripFinished = new MutableLiveData<>(false);
        this.tripRoute = new MutableLiveData<>();
    }

    public LiveData<LatLng> getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(LatLng latLng) {
        lastKnownLocation.setValue(latLng);
    }

    public LiveData<String> getStartingAddress() {
        return startingAddress;
    }

    public void setStartingAddress(String address) {
        startingAddress.setValue(address);
    }

    public LiveData<String> getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String address) {
        destinationAddress.setValue(address);
    }

    public LiveData<LatLng> getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(LatLng latLng) {
        startingLocation.setValue(latLng);
    }

    public LiveData<LatLng> getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(LatLng latLng) {
        destinationLocation.setValue(latLng);
    }

    public LiveData<Boolean> getIsTripStarted() {
        return isTripStarted;
    }

    public void setIsTripStarted(boolean isTripStarted) {
        this.isTripStarted.setValue(isTripStarted);
    }

    public LiveData<Boolean> getIsDestinationSelectedBefore() {
        if (isDestinationSelectedBefore == null) {
            setIsDestinationSelectedBefore(false);
        }
        return isDestinationSelectedBefore;
    }

    public void setIsDestinationSelectedBefore(Boolean value) {
        isDestinationSelectedBefore.setValue(value);
    }

    public LiveData<CarTypes> getSelectedCarType() {
        if (selectedCarType == null) {
            setSelectedCarType(CarTypes.CLASSIC);
        }
        return selectedCarType;
    }

    public void setSelectedCarType(CarTypes carType) {
        selectedCarType.setValue(carType);
    }

    @NotNull
    public LiveData<Boolean> getIsTripFinished() {
        return isTripFinished;
    }

    public void setIsTripFinished(boolean isTripFinished) {
        this.isTripFinished.setValue(isTripFinished);
    }

    public LiveData<Route> getTripRoute() {
        return tripRoute;
    }

    public void setTripRoute(Route route) {
        this.tripRoute.setValue(route);
    }

    public Observable<DirectionResponse> getRoute() {

        return mapRepositoryImpl.getRoute(
                new DirectionRequest(
                        Utils.toCoordinate(getStartingLocation().getValue()),
                        Utils.toCoordinate(getDestinationLocation().getValue())
                ));
    }

    public Resource<Driver> driverData(int driverId) {
        return new Resource<>(Status.SUCCESSFUL,
                new Driver(driverId,
                        "John",
                        "Adams",
                        "picture",
                        24,
                        1,
                        4.8F,
                        42,
                        1,
                        "34 XYZ 123",
                        4.2F,
                        55,
                        "Toyota Corolla")
        );
    }

    public String getAddress(LatLng location) {
        return geocoderHelper.getNameFromLocation(location);
    }

    public boolean checkPermissions() {
        return locationService.checkPermission();
    }

    public void clearViewModel() {
        setIsDestinationSelectedBefore(false);
        setStartingAddress("");
        setDestinationAddress("");
        setStartingLocation(null);
        setDestinationLocation(null);
        setLastKnownLocation(null);
        setSelectedCarType(CarTypes.CLASSIC);
        setIsTripStarted(false);
        setIsTripFinished(false);
    }
}
