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

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.huawei.hitaxicodelab_java.R;
import com.huawei.hitaxicodelab_java.custom.CustomDialog;
import com.huawei.hitaxicodelab_java.custom.LottieDialog;
import com.huawei.hitaxicodelab_java.data.model.BottomSheetButtonTypes;
import com.huawei.hitaxicodelab_java.data.model.Coordinate;
import com.huawei.hitaxicodelab_java.data.model.DirectionResponse;
import com.huawei.hitaxicodelab_java.data.model.Path;
import com.huawei.hitaxicodelab_java.data.model.Route;
import com.huawei.hitaxicodelab_java.data.model.Status;
import com.huawei.hitaxicodelab_java.data.model.Step;
import com.huawei.hitaxicodelab_java.data.model.Trip;
import com.huawei.hitaxicodelab_java.data.model.TripListener;
import com.huawei.hitaxicodelab_java.databinding.FragmentHomeBinding;
import com.huawei.hitaxicodelab_java.ui.base.BaseFragment;
import com.huawei.hitaxicodelab_java.utils.LocationUtil;
import com.huawei.hitaxicodelab_java.utils.Utils;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.LatLngBounds;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.random.Random;

@AndroidEntryPoint
public class HomeFragment extends BaseFragment<HomeViewModel, FragmentHomeBinding> implements OnMapReadyCallback {

    HomeViewModel homeViewModel;
    private HuaweiMap huaweiMap;

    private Marker calledMarker = null;
    private Marker marker = null;
    private LatLng taxiFinalLocation = null;
    private Marker userLocationTripMarker = null;

    private TripBottomSheetDialog tripBottomSheetDialog;

    private LottieDialog locationDialog;
    private LottieDialog lottieDialog;
    private CustomDialog spaciousLocationDialog;
    private LocationUtil locationUtil;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private HomeBottomSheetDialog homeBottomSheetDialog;
    Bundle mapViewBundle = null;
    private final int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
    private final int heightPixels = Resources.getSystem().getDisplayMetrics().heightPixels;

    private BottomSheetButtonTypes bottomSheetButtonTypes;
    private TripListener tripListener;

    int numberOfTaxiNearby = 6;

    @NotNull
    @Override
    public HomeViewModel getViewModel() {
        return homeViewModel;
    }

    @NotNull
    @Override
    public FragmentHomeBinding getFragmentViewBinding(
            @NotNull LayoutInflater inflater,
            @Nullable ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.i("LocationCallback", "Location : $locationResult.getLastLocation()");
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationUtil = new LocationUtil(requireActivity());

        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext());
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey");
        }

        if (Boolean.TRUE.equals(homeViewModel.getIsDestinationSelectedBefore().getValue())) {
            getRoute();
            getFragmentViewBinding().firstLocationImageView.setVisibility(View.GONE);
        } else {
            getFragmentViewBinding().firstLocationImageView.setVisibility(View.VISIBLE);
            hidePetalMapsButton();
        }
    }

    private void getRoute() {

        homeViewModel.getRoute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DirectionResponse>() {

                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        //onSubscribe
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull DirectionResponse directionResponse) {
                        lottieDialog.dismissDialog();
                        homeViewModel.setTripRoute(directionResponse.routes.get(0));
                        addRouteOnScreen();
                        showPetalMapsButton();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        lottieDialog.dismissDialog();
                        homeViewModel.setIsDestinationSelectedBefore(false);
                    }

                    @Override
                    public void onComplete() {
                        //onComplete
                    }
                });
    }

    private void addRouteOnScreen() {
        Route route = homeViewModel.getTripRoute().getValue();
        if (route != null) {
            Path path = route.paths.get(0);
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.add(Utils.toLatLng(path.startLocation));
            for (Step step : path.steps) {
                for (Coordinate coordinate : step.polyline) {
                    polylineOptions.add(Utils.toLatLng(coordinate));
                }
            }
            polylineOptions
                    .add(Utils.toLatLng(path.endLocation))
                    .color(Color.GREEN)
                    .width(6F);
            huaweiMap.clear();
            huaweiMap.addPolyline(polylineOptions);
            huaweiMap.setPadding(0, ((int) -(heightPixels * 0.3)), 0, ((int) (heightPixels * 0.3)));
            huaweiMap.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                            new LatLngBounds(
                                    Utils.toLatLng(route.bounds.southwest),
                                    Utils.toLatLng(route.bounds.northeast)
                            ),
                            widthPixels,
                            heightPixels - getFragmentViewBinding().bottomSheet.getMeasuredHeight(),
                            0
                    )
            );
            huaweiMap.addMarker(
                    new MarkerOptions()
                            .position(Utils.toLatLng(path.startLocation))
                            .draggable(false)
                            .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                            Utils.toBitmap(ContextCompat.getDrawable(
                                                    requireContext(),
                                                    R.drawable.ic_start_location_pin
                                                    )
                                            )
                                    )
                            )
                            .title(path.startAddress)
            );
            huaweiMap.addMarker(
                    new MarkerOptions()
                            .position(Utils.toLatLng(path.endLocation))
                            .draggable(false)
                            .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                            Utils.toBitmap(ContextCompat.getDrawable(
                                                    requireContext(),
                                                    R.drawable.ic_end_location_pin
                                                    )
                                            )
                                    )
                            )
                            .title(path.endAddress)
            );
            LatLng latLng = new LatLng(path.startLocation.lat, path.startLocation.lng);
            addTaxi(latLng);
            taxiFinalLocation = latLng;
            homeBottomSheetDialog.setTaxiArrivedTime(path.durationInTrafficText);
            homeBottomSheetDialog.setTaxiDistance(path.distanceText);
            homeBottomSheetDialog.setTaxiPrice(getCost(path.distance).toString());
            getFragmentViewBinding().firstLocationImageView.setVisibility(View.GONE);
        }
    }

    private void showPetalMapsButton() {
        getFragmentViewBinding().petalMapsButton.setVisibility(View.VISIBLE);
        getFragmentViewBinding().petalMapsText.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            getFragmentViewBinding().petalMapsText
                    .animate()
                    .alpha(0.0F)
                    .setDuration(1000L);
            getFragmentViewBinding().petalMapsText.setVisibility(View.INVISIBLE);
        }, 10_000);
    }

    private void addTaxi(LatLng latLng) {
        if (marker != null) {
            marker.remove();
        }
        double startLat = latLng.latitude - 0.001;
        double endLat = latLng.latitude + 0.001;
        double startLng = latLng.longitude - 0.001;
        double endLng = latLng.longitude + 0.001;

        for (int i = 0; i < 5; i++) {
            marker = huaweiMap.addMarker(
                    new MarkerOptions()
                            .position(
                                    new LatLng(
                                            Random.Default.nextDouble(startLat, endLat),
                                            Random.Default.nextDouble(startLng, endLng)
                                    )
                            )
                            .anchor(0.5f, 0.9f)
                            .title("HiTaxi")
                            .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                            Utils.toBitmap(
                                                    ContextCompat.getDrawable(
                                                            requireContext(),
                                                            R.drawable.ic_taxi
                                                    )
                                            )
                                    )
                            )
            );
        }

        calledMarker = huaweiMap.addMarker(
                new MarkerOptions()
                        .position(
                                new LatLng(
                                        Random.Default.nextDouble(startLat, endLat),
                                        Random.Default.nextDouble(startLng, endLng)
                                )
                        )
                        .anchor(0.5f, 0.9f)
                        .title("HiTaxi")
                        .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                        Utils.toBitmap(
                                                ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_taxi
                                                )
                                        )
                                )
                        )
        );
    }

    private Double getCost(Double distance) {
        return Double.parseDouble(String.format(
                Locale.ENGLISH,
                "%.2f",
                homeViewModel.getSelectedCarType().getValue().getCar().getCostForKm() * (distance / 1000)
        ));
    }

    private void hidePetalMapsButton() {
        getFragmentViewBinding().petalMapsButton.setVisibility(View.INVISIBLE);
        getFragmentViewBinding().petalMapsText.setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setupListeners() {
        super.setupListeners();
        getFragmentViewBinding().myLocationButton.setOnClickListener(v -> {
            // show user location on map
            findLocation();
            locationDialog.showDialog();
        });
        getFragmentViewBinding().petalMapsButton.setOnClickListener(v -> {
            if (homeViewModel.getTripRoute().getValue() != null) {
                showRouteOnPetalMaps(
                        homeViewModel.getTripRoute().getValue().paths.get(0).startLocation,
                        homeViewModel.getTripRoute().getValue().paths.get(0).endLocation
                );
            } else {
                hidePetalMapsButton();
            }
        });
    }

    private void navigateToHome() {
        homeViewModel.clearViewModel();
        NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_self);
    }

    @Override
    public void setupObservers() {
        super.setupObservers();
        homeViewModel.getIsDestinationSelectedBefore().observe(getViewLifecycleOwner(), isDestinationSelectedBefore -> {
            if (Boolean.TRUE.equals(isDestinationSelectedBefore)) {
                homeBottomSheetDialog.showAddressLayout(true);
                homeBottomSheetDialog.setTaxiInformationMessage(
                        getString(
                                R.string.info_nearby_taxi,
                                numberOfTaxiNearby
                        )
                );
                homeViewModel.getStartingAddress().observe(getViewLifecycleOwner(), it -> homeBottomSheetDialog.setStartingAddress(it));
                homeViewModel.getDestinationAddress().observe(getViewLifecycleOwner(), it -> homeBottomSheetDialog.setDestinationAddress(it));
                homeViewModel.getStartingLocation().observe(getViewLifecycleOwner(), it -> {

                });
                homeViewModel.getDestinationLocation().observe(getViewLifecycleOwner(), it -> {

                });
            } else {
                homeBottomSheetDialog.showAddressLayout(false);
            }

            homeViewModel.getSelectedCarType().observe(getViewLifecycleOwner(), it -> homeBottomSheetDialog.setSelectedCarType(it));

            homeViewModel.getIsTripFinished().observe(getViewLifecycleOwner(), it -> {
                if (Boolean.TRUE.equals(it) && homeViewModel.getTripRoute().getValue() != null) {
                    navigateToHome();
                }
            });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(HuaweiMap map) {
        checkLocationPermission();
        huaweiMap = map;
        tripNotStartedOnMap();
    }

    @Override
    public void setupUi() {
        super.setupUi();
        getFragmentViewBinding().huaweiMap.onCreate(mapViewBundle);
        getFragmentViewBinding().huaweiMap.getMapAsync(this);
        lottieDialog = new LottieDialog(R.raw.route_animation, requireContext());
        locationDialog = new LottieDialog(R.raw.taxi_animation, requireContext());
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        setupBookingBottomSheet();
    }

    private void moveTaxi() {
        LatLng startPosition = calledMarker.getPosition();
        LatLng finalPosition = taxiFinalLocation;
        Handler handler = new Handler();
        Long start = SystemClock.uptimeMillis();
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        float durationInMs = 3000.0F;
        boolean hideMarker = false;
        if (homeViewModel.driverData(1).getStatus() == Status.SUCCESSFUL) {
            homeViewModel.driver = homeViewModel.driverData(1).getData();
            handler.post(new Runnable() {
                long elapsed = 0;
                float t = 0.0f;
                float v = 0.0f;

                @Override
                public void run() {
                    elapsed = SystemClock.uptimeMillis() - start;
                    t = elapsed / durationInMs;
                    v = interpolator.getInterpolation(t);
                    LatLng currentPosition = new LatLng(
                            startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                            startPosition.longitude * (1 - t) + finalPosition.longitude * t
                    );
                    calledMarker.setPosition(currentPosition);
                    if (t < 1) {
                        handler.postDelayed(this, 16);
                    } else {
                        if (hideMarker) {
                            calledMarker.setVisible(false);
                        } else {
                            calledMarker.setVisible(true);
                            boolean isTripStarted = homeViewModel.getIsTripFinished().getValue();
                            if (!isTripStarted) {
                                showAlertDialog(
                                        getString(R.string.taxi_arrived_title),
                                        getString(R.string.taxi_arrived_message),
                                        R.drawable.ic_taxi_dialog
                                );
                            }
                        }
                    }
                }
            });
        }
    }

    private void setupBookingBottomSheet() {
        homeBottomSheetDialog = new HomeBottomSheetDialog(new Function1() {
            @Override
            public Object invoke(Object o) {
                this.invoke((BottomSheetButtonTypes) o);
                return Unit.INSTANCE;
            }

            public final void invoke(@NotNull BottomSheetButtonTypes it) {
                bottomSheetButtonTypes = it;
                switch (bottomSheetButtonTypes) {
                    case HOME: {
                        navigateToDestinationFragment(false);
                        break;
                    }
                    case DESTINATION:
                    case FIRST_DESTINATION: {
                        navigateToDestinationFragment(true);
                        break;
                    }
                    case CALL_TAXI: {
                        if (homeViewModel.getDestinationLocation().getValue() == null) {
                            Toast.makeText(requireContext(), getString(R.string.warning_empty_destination), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (homeViewModel.getStartingLocation().getValue() == null) {
                            Toast.makeText(requireContext(), getString(R.string.warning_empty_starting_location), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        moveTaxi();
                        break;
                    }
                    case CAR_TYPE_SELECTED: {
                        homeViewModel.setSelectedCarType(homeBottomSheetDialog.selectedCar);
                        if (homeViewModel.getTripRoute().getValue() != null) {
                            homeBottomSheetDialog.setTaxiPrice(
                                    getCost(
                                            homeViewModel.getTripRoute().getValue().paths.get(0).distance
                                    ).toString()
                            );
                        }
                        break;
                    }
                    default:
                }
                changeBottomSheet(homeBottomSheetDialog);
            }
        });
        changeBottomSheet(homeBottomSheetDialog);
    }

    private void showAlertDialog(String title, String message, int icon) {
        CustomDialog customDialog = new CustomDialog(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setIcon(ContextCompat.getDrawable(requireContext(), icon))
                .setPositiveButton(
                        getString(R.string.start_trip),
                        () -> {
                            if (Boolean.TRUE.equals(homeViewModel.getIsDestinationSelectedBefore().getValue()) &&
                                    homeViewModel.getTripRoute().getValue() != null) {
                                hidePetalMapsButton();
                                setTripBottomSheet(homeViewModel.getTripRoute().getValue());
                                homeViewModel.setIsTripStarted(true);
                            }
                        })
                .setCancelButton(
                        getString(R.string.answer_cancel)
                )
                .createDialog();
        customDialog.showDialog();
    }

    private void setTripBottomSheet(Route route) {
        tripBottomSheetDialog = new TripBottomSheetDialog(createTrip(route), new Function1() {
            @Override
            public Object invoke(Object o) {
                this.invoke((TripListener) o);
                return Unit.INSTANCE;
            }

            public final void invoke(@NotNull TripListener it) {
                tripListener = it;
                if (tripListener == TripListener.START_TRIP) {
                    startLocationUpdates();
                    tripStartedOnMap();
                    moveTaxiWithRoute(route);
                }
            }
        });
        changeBottomSheet(tripBottomSheetDialog);
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private void moveTaxiWithRoute(Route route) {
        List<Step> steps = route.paths.get(0).steps;
        drawTripRoute();
        animateMarker(steps, Utils.secondToMillisecond(route.paths.get(0).duration.longValue()));
    }

    private void tripStartedOnMap() {
        getFragmentViewBinding().myLocationButton.setVisibility(View.INVISIBLE);
        huaweiMap.setOnCameraMoveStartedListener(i -> {

        });
        huaweiMap.setOnCameraIdleListener(() -> {

        });
        observeAndAddTaxi();
    }

    private void drawTripRoute() {
        Route route = homeViewModel.getTripRoute().getValue();
        if (route != null) {
            Path path = route.paths.get(0);
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.add(Utils.toLatLng(path.startLocation));
            for (Step step : path.steps) {
                for (Coordinate coordinate : step.polyline) {
                    polylineOptions.add(Utils.toLatLng(coordinate));
                }
            }
            polylineOptions
                    .add(Utils.toLatLng(path.endLocation))
                    .color(Color.BLUE)
                    .width(6F);
            huaweiMap.clear();
            huaweiMap.addPolyline(polylineOptions);
            huaweiMap.setPadding(0, ((int) -(heightPixels * 0.3)), 0, ((int) (heightPixels * 0.3)));
            huaweiMap.addMarker(
                    new MarkerOptions()
                            .position(Utils.toLatLng(path.endLocation))
                            .anchorMarker(0.5F, 0.5F)
                            .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                            Utils.toBitmap(ContextCompat.getDrawable(
                                                    requireContext(),
                                                    R.drawable.ic_finish_flag
                                                    )
                                            )
                                    )
                            )
            );
        }
    }

    private void animateMarker(List<Step> steps, Long duration) {
        Handler handler = new Handler();
        Long start = SystemClock.uptimeMillis();
        final Step[] nextStep = {steps.get(0)};
        LinearInterpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            int index = 0;

            @Override
            public void run() {
                Long elapsed = SystemClock.uptimeMillis() - start;
                float time = interpolator.getInterpolation(
                        elapsed.floatValue() / duration
                );
                if (index < steps.size()) {
                    nextStep[0] = steps.get(index);
                    animateMarkerInStep(nextStep[0]);
                    tripBottomSheetDialog.setStepData(index);
                }
                index++;
                if (time < 1.0) {
                    handler.postDelayed(this, Utils.secondToMillisecond(nextStep[0].duration.longValue()));
                } else {
                    tripHasFinished();
                }
            }
        });
    }

    private void animateMarkerInStep(Step currentStep) {
        LatLng startPosition = Utils.toLatLng(currentStep.startLocation);
        LatLng finalPosition = Utils.toLatLng(currentStep.endLocation);
        Handler handler = new Handler();
        Long start = SystemClock.uptimeMillis();
        float durationInMs = Utils.secondToMillisecond(currentStep.duration.longValue()).floatValue();
        handler.post(new Runnable() {
            long elapsed = 0;
            float time = 0.0f;

            @Override
            public void run() {
                elapsed = SystemClock.uptimeMillis() - start;
                time = elapsed / durationInMs;
                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - time) + finalPosition.latitude * time,
                        startPosition.longitude * (1 - time) + finalPosition.longitude * time
                );
                setupUserLocationTripMarker(
                        currentPosition,
                        getBearingBetweenTwoPoints(
                                Utils.toLatLng(currentStep.startLocation),
                                Utils.toLatLng(currentStep.endLocation)
                        )
                );
                if (time < 1) {
                    handler.postDelayed(this, 1);
                }
            }
        });
    }

    private void setupUserLocationTripMarker(LatLng userLatLng, double rotation) {
        if (userLocationTripMarker != null) {
            userLocationTripMarker.setPosition(userLatLng);
            huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 17F));
            userLocationTripMarker.setRotation(((float) rotation));
        } else {
            userLocationTripMarker = huaweiMap.addMarker(
                    new MarkerOptions()
                            .position(userLatLng)
                            .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                            Utils.toBitmap(
                                                    ContextCompat.getDrawable(
                                                            requireContext(),
                                                            R.drawable.trip_taxi
                                                    )
                                            )
                                    )
                            )
                            .rotation(((float) rotation))
            );
            huaweiMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(userLatLng, 17F)
            );
        }
    }

    private double getBearingBetweenTwoPoints(LatLng locationOne, LatLng locationTwo) {
        double latitude1 = degreesToRadians(locationOne.latitude);
        double longitude1 = degreesToRadians(locationOne.longitude);
        double latitude2 = degreesToRadians(locationTwo.latitude);
        double longitude2 = degreesToRadians(locationTwo.longitude);
        double longitudeDifference = longitude2 - longitude1;
        double yCoordinate = Math.sin(longitudeDifference) * Math.cos(latitude2);
        double xCoordinate =
                Math.cos(latitude1) * Math.sin(latitude2) - (Math.sin(latitude1)
                        * Math.cos(latitude2) * Math.cos(longitudeDifference));
        double radiansBearing = Math.atan2(yCoordinate, xCoordinate);
        return radiansToDegrees(radiansBearing);
    }

    private double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }

    private double radiansToDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }

    private void tripHasFinished() {
        stopLocationUpdates();
        tripNotStartedOnMap();
        homeViewModel.setIsTripFinished(true);
    }

    private void changeBottomSheet(Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.bottomSheet, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomSheet() {
        getFragmentViewBinding().bottomSheet.animate()
                .translationY(0.0F)
                .setDuration(500);
    }

    private void hideBottomSheet() {
        getFragmentViewBinding().bottomSheet.animate()
                .translationY((float) (getFragmentViewBinding().bottomSheet.getHeight()))
                .setDuration(500);
    }

    private void tripNotStartedOnMap() {
        huaweiMap.setMyLocationEnabled(false);
        huaweiMap.getUiSettings().setMyLocationButtonEnabled(false);
        huaweiMap.getUiSettings().setZoomControlsEnabled(false);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            huaweiMap.setOnCameraMoveStartedListener(i -> {
                getFragmentViewBinding().myLocationButton.setVisibility(View.INVISIBLE);
                hideBottomSheet();
            });
            huaweiMap.setOnCameraIdleListener(() -> {
                getFragmentViewBinding().myLocationButton.setVisibility(View.VISIBLE);
                showBottomSheet();
            });
        }, 2000);
        observeAndAddTaxi();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void checkLocationPermission() {
        if (homeViewModel.checkPermissions()) {
            locationDialog.showDialog();
            findLocation();
        } else {
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_permissionFragment);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void findLocation() {
        getLastKnownLocation();
        new CountDownTimer(30_000, 1_000) {
            @Override
            public void onFinish() {
                locationDialog.dismissDialog();
                moveSpaciousLocationDialog();
            }

            @Override
            public void onTick(long l) {
                if (homeViewModel.getLastKnownLocation().getValue() != null) {
                    cancel();
                }
            }
        }.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void moveSpaciousLocationDialog() {
        if (spaciousLocationDialog == null) {
            spaciousLocationDialog = new CustomDialog(requireContext())
                    .setTitle(getString(R.string.app_name))
                    .setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_error))
                    .setMessage(getString(R.string.location_place_error_message))
                    .setPositiveButton(
                            getString(R.string.try_again),
                            () -> {
                                locationDialog.showDialog();
                                findLocation();
                            }).createDialog();
        }
        spaciousLocationDialog.showDialog();
        homeViewModel.getLastKnownLocation().getValue();
    }

    private Trip createTrip(Route route) {
        Path path = route.paths.get(0);
        return new Trip(
                path.startLocation,
                path.startAddress,
                path.endLocation,
                path.endAddress,
                getCost(path.distance),
                route,
                homeViewModel.driver
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getLastKnownLocation() {
        homeViewModel.locationService.getLastLocation(lastlocation -> {
            if (lastlocation.latitude != 0.0) {
                homeViewModel.setLastKnownLocation(lastlocation);
                locationDialog.dismissDialog();
            } else {
                locationUtil.checkLocationSettings(it -> {
                    homeViewModel.setLastKnownLocation(it);
                    locationDialog.dismissDialog();
                });
            }
        });
    }

    private void navigateToDestinationFragment(boolean isFromDestination) {
        NavDirections action = (NavDirections) HomeFragmentDirections.actionHomeFragmentToDestinationFragment().setIsFromDestination(isFromDestination);
        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onStart() {
        super.onStart();
        getFragmentViewBinding().huaweiMap.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        getFragmentViewBinding().huaweiMap.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        getFragmentViewBinding().huaweiMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getFragmentViewBinding().huaweiMap.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getFragmentViewBinding().huaweiMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        getFragmentViewBinding().huaweiMap.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setFragmentViewBinding(null);
    }

    private void showRouteOnPetalMaps(Coordinate startLocation, Coordinate endLocation) {
        Uri contentUri = Uri.parse(
                "mapapp://navigation?saddr=" + startLocation.lat
                        + "," + startLocation.lng + "&daddr=" + endLocation.lat + ","
                        + endLocation.lng + "&language=en&type=drive"
        );
        Intent petalMapsIntent = new Intent(Intent.ACTION_VIEW, contentUri);
        if (petalMapsIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(petalMapsIntent);
        }
    }

    private void observeAndAddTaxi() {
        homeViewModel.getLastKnownLocation().observe(getViewLifecycleOwner(), latLng -> {
                    if (latLng != null && Boolean.FALSE.equals(homeViewModel.getIsDestinationSelectedBefore().getValue())) {
                        huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
                        huaweiMap.clear();
                        addTaxi(latLng);
                        taxiFinalLocation = latLng;
                    }
                }
        );
    }

}


