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

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.huawei.hitaxicodelab.R
import com.huawei.hitaxicodelab.custom.CustomDialog
import com.huawei.hitaxicodelab.custom.LottieDialog
import com.huawei.hitaxicodelab.data.model.*
import com.huawei.hitaxicodelab.databinding.FragmentHomeBinding
import com.huawei.hitaxicodelab.ui.base.BaseFragment
import com.huawei.hitaxicodelab.utils.*
import com.huawei.hms.location.*
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.*
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(), OnMapReadyCallback {
    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var huaweiMap: HuaweiMap
    private lateinit var homeBottomSheetDialog: HomeBottomSheetDialog

    private var calledMarker: Marker? = null
    private var marker: Marker? = null
    private var taxiFinalLocation: LatLng? = null
    private var userLocationTripMarker: Marker? = null

    private lateinit var tripBottomSheetDialog: TripBottomSheetDialog

    private val numberOfTaxiNearby = 6

    private lateinit var locationDialog: LottieDialog
    private lateinit var lottieDialog: LottieDialog
    private lateinit var spaciousLocationDialog: CustomDialog
    private lateinit var locationUtil: LocationUtil
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val widthPixels = Resources.getSystem().displayMetrics.widthPixels
    private val heightPixels = Resources.getSystem().displayMetrics.heightPixels

    override fun getViewModel(): HomeViewModel = homeViewModel

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationUtil = LocationUtil(requireActivity())

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create()
        locationRequest.apply {
            interval = 500
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (homeViewModel.isDestinationSelectedBefore.value == true) {
            getRoute()
            fragmentViewBinding.firstLocationImageView.visibility = View.GONE
        } else {
            fragmentViewBinding.firstLocationImageView.visibility = View.VISIBLE
        }
    }

    private fun getRoute() {
        try {
            homeViewModel.getRoute().observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> {
                        lottieDialog.showDialog()
                    }
                    Status.SUCCESSFUL -> {
                        it.data?.let { directionResponse ->
                            lottieDialog.dismissDialog()
                            homeViewModel.setTripRoute(directionResponse.routes[0])
                            addRouteOnScreen()
                        }
                    }
                    Status.ERROR -> {
                        lottieDialog.dismissDialog()
                        homeViewModel.setIsDestinationSelectedBefore(false)
                    }
                }
            }
        } catch (nullException: NullPointerException) {
            lottieDialog.dismissDialog()
            homeViewModel.setIsDestinationSelectedBefore(false)
        }
    }

    private fun addRouteOnScreen() {
        homeViewModel.tripRoute.value?.let { route ->
            val path = route.paths[0]
            val polylineOptions = PolylineOptions()
            polylineOptions.add(path.startLocation.toLatLng())
            path.steps.forEach { step ->
                step.polyline.forEach { polyline ->
                    polylineOptions.add(polyline.toLatLng())
                }
            }
            polylineOptions.apply {
                add(path.endLocation.toLatLng())
                color(Color.GREEN)
                width(6F)
            }

            huaweiMap.apply {
                clear()
                addPolyline(polylineOptions)
                setPadding(0, -(heightPixels * 0.3).toInt(), 0, (heightPixels * 0.3).toInt())
                moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        LatLngBounds(
                            route.bounds.southwest.toLatLng(),
                            route.bounds.northeast.toLatLng()
                        ),
                        widthPixels,
                        heightPixels - fragmentViewBinding.bottomSheet.measuredHeight,
                        0
                    )
                )
                addMarker(
                    MarkerOptions()
                        .position(path.startLocation.toLatLng())
                        .draggable(false)
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_start_location_pin
                                )?.toBitmap()
                            )
                        )
                        .title(path.startAddress)
                )
                addMarker(
                    MarkerOptions()
                        .position(path.endLocation.toLatLng())
                        .draggable(false)
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_end_location_pin
                                )?.toBitmap()
                            )
                        )
                        .title(path.endAddress)
                )
                addTaxi(LatLng(path.startLocation.lat, path.startLocation.lng))
                taxiFinalLocation = LatLng(path.startLocation.lat, path.startLocation.lng)
            }

            homeBottomSheetDialog.apply {
                setTaxiArrivedTime(path.durationInTrafficText)
                setTaxiDistance(path.distanceText)
                setTaxiPrice(getCost(path.distance).toString())
            }

            fragmentViewBinding.firstLocationImageView.visibility = View.GONE
        }
    }

    private fun addTaxi(latLng: LatLng) {
        if (marker != null) {
            marker!!.remove()
        }

        val startLat = latLng.latitude - 0.001
        val endLat = latLng.latitude + 0.001
        val startLng = latLng.longitude - 0.001
        val endLng = latLng.longitude + 0.001

        for (i in 0..5) {
            marker = huaweiMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        Random.nextDouble(startLat, endLat),
                        Random.nextDouble(startLng, endLng)
                    )
                )
                    .anchor(0.5f, 0.9f)
                    .title("HiTaxi")
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_taxi
                            )?.toBitmap()
                        )
                    )
            )
        }

        calledMarker = huaweiMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    Random.nextDouble(startLat, endLat),
                    Random.nextDouble(startLng, endLng)
                )
            )
                .anchor(0.5f, 0.9f)
                .title("HiTaxi")
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_taxi
                        )?.toBitmap()
                    )
                )
        )
    }


    private fun getCost(distance: Double): Double {
        return String.format(
            Locale.ENGLISH,
            "%.2f",
            homeViewModel.selectedCardType.value!!.car.costForKm * (distance / 1000)
        ).toDouble()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("UseRequireInsteadOfGet")
    override fun setupListeners() {
        super.setupListeners()

        fragmentViewBinding.myLocationButton.setOnClickListener {
            findLocation()
            locationDialog.showDialog()
        }

    }

    override fun setupObservers() {
        super.setupObservers()

        homeViewModel.isDestinationSelectedBefore.observe(
            viewLifecycleOwner
        ) { isDestinationSelectedBefore ->
            if (isDestinationSelectedBefore) {
                homeBottomSheetDialog.showAddressLayout(true)
                homeBottomSheetDialog.setTaxiInformationMessage(
                    getString(
                        R.string.info_nearby_taxi,
                        numberOfTaxiNearby
                    )
                )
                homeViewModel.startingAddress.observe(viewLifecycleOwner) {
                    homeBottomSheetDialog.setStartingAddress(it)
                }

                homeViewModel.destinationAddress.observe(viewLifecycleOwner) {
                    homeBottomSheetDialog.setDestinationAddress(it)
                }

                homeViewModel.startingLocation.observe(viewLifecycleOwner) {

                }

                homeViewModel.destinationLocation.observe(viewLifecycleOwner) {

                }
            } else {
                homeBottomSheetDialog.showAddressLayout(false)
            }
        }

        homeViewModel.selectedCardType.observe(viewLifecycleOwner) {
            homeBottomSheetDialog.setSelectedCarType(it)
        }

        homeViewModel.isTripFinished.observe(viewLifecycleOwner) {
            if (it) {
                homeViewModel.tripRoute.value?.let { route ->
                    navigateToEndOfTripFragment(createTrip(route))
                }
            }
        }

    }

    private fun navigateToEndOfTripFragment(trip: Trip) {
        val action = HomeFragmentDirections.actionHomeFragmentToEndOfTripFragment(trip)
        findNavController().navigate(action)
    }

    override fun setupUi() {
        super.setupUi()
        lottieDialog = LottieDialog.Builder().setContext(requireContext())
            .setLottieRawId(R.raw.route_animation).build()
        locationDialog = LottieDialog.Builder().setContext(requireContext())
            .setLottieRawId(R.raw.taxi_animation).build()
        fragmentViewBinding.huaweiMap.apply {
            onCreate(null)
            getMapAsync(this@HomeFragment)
            setupBookingBottomSheet()
        }

    }

    override fun onMapReady(map: HuaweiMap) {
        checkLocationPermission()
        huaweiMap = map
        tripNotStartedOnMap()
    }

    private fun setupBookingBottomSheet() {
        homeBottomSheetDialog = HomeBottomSheetDialog {
            when (it) {
                BottomSheetButtonTypes.FIRST_DESTINATION -> {
                    navigateToDestinationFragment(true)
                }
                BottomSheetButtonTypes.DESTINATION -> {
                    navigateToDestinationFragment(true)
                }
                BottomSheetButtonTypes.HOME -> {
                    navigateToDestinationFragment(false)
                }
                BottomSheetButtonTypes.CALL_TAXI -> {
                    if (homeViewModel.destinationLocation.value == null) {
                        requireContext().showToast(getString(R.string.warning_empty_destination))
                        return@HomeBottomSheetDialog
                    }
                    if (homeViewModel.startingLocation.value == null) {
                        requireContext().showToast(getString(R.string.warning_empty_starting_location))
                        return@HomeBottomSheetDialog
                    }
                    moveTaxi()
                }
                BottomSheetButtonTypes.CAR_TYPE_SELECTED -> {
                    homeViewModel.setSelectedCarType(homeBottomSheetDialog.selectedCar)
                    homeViewModel.tripRoute.value?.let { route ->
                        homeBottomSheetDialog.setTaxiPrice(getCost(route.paths[0].distance).toString())
                    }
                }
            }
        }
        changeBottomSheet(homeBottomSheetDialog)

    }

    private fun moveTaxi() {
        val startPosition: LatLng = calledMarker!!.position
        val finalPosition: LatLng = taxiFinalLocation!!
        val handler = Handler(Looper.getMainLooper())
        val start: Long = SystemClock.uptimeMillis()
        val interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = 3000.0F
        val hideMarker = false

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.driverData().observe(viewLifecycleOwner) {
                if (it.isStatusSuccess) {
                    it.data?.let { driver ->
                        homeViewModel.driver = driver

                        handler.post(object : Runnable {
                            var elapsed: Long = 0
                            var t: Float = 0.0f
                            var v: Float = 0.0f
                            override fun run() {
                                elapsed = SystemClock.uptimeMillis() - start
                                t = elapsed / durationInMs
                                v = interpolator.getInterpolation(t)

                                val currentPosition = LatLng(
                                    startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                                    startPosition.longitude * (1 - t) + finalPosition.longitude * t
                                )

                                calledMarker!!.position = currentPosition

                                if (t < 1) {
                                    handler.postDelayed(this, 16)
                                } else {
                                    if (hideMarker) {
                                        calledMarker!!.isVisible = false
                                    } else {
                                        calledMarker!!.isVisible = true


                                        homeViewModel.isTripStarted.value?.let { isTripStarted ->
                                            if (!isTripStarted) {
                                                showAlertDialog(
                                                    getString(R.string.taxi_arrived_title),
                                                    getString(R.string.taxi_arrived_message),
                                                    R.drawable.ic_taxi_dialog
                                                )
                                            }
                                        }

                                    }
                                }
                            }
                        })
                    }
                } else if (it.isStatusError) {
                    requireContext().showToast("An error occurred when calling Taxi")
                }
            }
        }
    }

    private fun showAlertDialog(title: String, message: String, icon: Int) {
        val customDialog = CustomDialog.getInstance(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setIcon(ContextCompat.getDrawable(requireContext(), icon))
            .setPositiveButton(
                getString(R.string.start_trip),
                object : CustomDialog.ICustomDialogClickListener {
                    override fun onClick() {
                        if (homeViewModel.isDestinationSelectedBefore.value == true) {
                            homeViewModel.tripRoute.value?.let { route ->
                                setTripBottomSheet(route)
                                homeViewModel.setIsTripStarted(true)
                            }

                        }
                    }
                })
            .setCancelButton(
                getString(R.string.answer_cancel)
            )
            .createDialog()
        customDialog.showDialog()
    }

    private fun setTripBottomSheet(route: Route) {
        tripBottomSheetDialog = TripBottomSheetDialog(createTrip(route)) {
            when (it) {
                TripListener.START_TRIP -> {
                    startLocationUpdates()
                    tripStartedOnMap()
                    moveTaxiWithRoute(route)
                }
            }
        }
        changeBottomSheet(tripBottomSheetDialog)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.lastLocation?.let {
                print("Location : $it")
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun moveTaxiWithRoute(route: Route) {
        val steps = route.paths[0].steps
        drawTripRoute()
        animateMarker(steps, route.paths[0].duration.toLong().secondToMillisecond())
    }

    private fun tripStartedOnMap() {
        fragmentViewBinding.myLocationButton.visibility = View.INVISIBLE
        huaweiMap.apply {

            setOnCameraMoveStartedListener {
            }

            setOnCameraIdleListener {
            }
        }
        observeAndAddTaxi()
    }

    private fun drawTripRoute() {
        homeViewModel.tripRoute.value?.let { route ->
            val path = route.paths[0]
            val polylineOptions = PolylineOptions()
            polylineOptions.add(path.startLocation.toLatLng())

            path.steps.forEach { step ->
                step.polyline.forEach { polyline ->
                    polylineOptions.add(polyline.toLatLng())
                }
            }
            polylineOptions.apply {
                add(path.endLocation.toLatLng())
                color(Color.BLUE)
                width(6F)
            }

            huaweiMap.apply {
                clear()
                addPolyline(polylineOptions)
                setPadding(0, -(heightPixels * 0.3).toInt(), 0, (heightPixels * 0.3).toInt())
                addMarker(
                    MarkerOptions().position(path.endLocation.toLatLng())
                        .anchorMarker(0.5F, 0.5F)
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_finish_flag
                                )?.toBitmap()
                            )
                        )
                )
            }
        }
    }

    private fun animateMarker(steps: List<Step>, duration: Long) {
        val handler = Handler(Looper.getMainLooper())
        val start = SystemClock.uptimeMillis()
        var nextStep = steps[0]
        val interpolator: android.view.animation.Interpolator = LinearInterpolator()
        handler.post(object : Runnable {
            var index = 0
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val time: Float = interpolator.getInterpolation(
                    elapsed.toFloat()
                            / duration
                )
                if (index < steps.size) {
                    nextStep = steps[index]
                    animateMarkerInStep(nextStep)
                    tripBottomSheetDialog.setStepData(index)
                }
                index++
                if (time < 1.0) {
                    handler.postDelayed(this, nextStep.duration.toLong().secondToMillisecond())
                } else {
                    tripHasFinished()
                }
            }
        })
    }

    fun animateMarkerInStep(currentStep: Step) {
        val startPosition: LatLng = currentStep.startLocation.toLatLng()
        val finalPosition: LatLng = currentStep.endLocation.toLatLng()
        val handler = Handler(Looper.getMainLooper())
        val start: Long = SystemClock.uptimeMillis()
        val durationInMs = currentStep.duration.toLong().secondToMillisecond().toFloat()

        handler.post(object : Runnable {
            var elapsed: Long = 0
            var time: Float = 0.0f
            override fun run() {
                elapsed = SystemClock.uptimeMillis() - start
                time = elapsed / durationInMs

                val currentPosition = LatLng(
                    startPosition.latitude * (1 - time) + finalPosition.latitude * time,
                    startPosition.longitude * (1 - time) + finalPosition.longitude * time
                )

                setupUserLocationTripMarker(
                    currentPosition,
                    getBearingBetweenTwoPoints(
                        currentStep.startLocation.toLatLng(),
                        currentStep.endLocation.toLatLng()
                    )
                )

                if (time < 1) {
                    handler.postDelayed(this, 1)
                }
            }
        })
    }

    private fun setupUserLocationTripMarker(userLatLng: LatLng, rotation: Double) {
        userLocationTripMarker?.let {
            it.position = userLatLng
            huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 17F))
            it.rotation = rotation.toFloat()
        } ?: kotlin.run {
            userLocationTripMarker = huaweiMap.addMarker(
                MarkerOptions().position(userLatLng)
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.trip_taxi
                            )?.toBitmap()
                        )
                    )
                    .rotation(rotation.toFloat())
            )
            huaweiMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 17F))
        }
    }

    private fun getBearingBetweenTwoPoints(locationOne: LatLng, locationTwo: LatLng): Double {
        val latitude1: Double = degreesToRadians(locationOne.latitude)
        val longitude1: Double = degreesToRadians(locationOne.longitude)
        val latitude2: Double = degreesToRadians(locationTwo.latitude)
        val longitude2: Double = degreesToRadians(locationTwo.longitude)
        val longitudeDifference = longitude2 - longitude1
        val yCoordinate = sin(longitudeDifference) * cos(latitude2)
        val xCoordinate =
            cos(latitude1) * sin(latitude2) - (sin(latitude1)
                    * cos(latitude2) * cos(longitudeDifference))
        val radiansBearing = atan2(yCoordinate, xCoordinate)
        return radiansToDegrees(radiansBearing)
    }

    private fun degreesToRadians(degrees: Double): Double {
        return degrees * Math.PI / 180.0
    }

    private fun radiansToDegrees(radians: Double): Double {
        return radians * 180.0 / Math.PI
    }

    private fun tripHasFinished() {
        stopLocationUpdates()
        tripNotStartedOnMap()
        homeViewModel.setIsTripFinished(true)
    }

    private fun changeBottomSheet(fragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.bottomSheet, fragment)
            commit()
        }
    }

    private fun showBottomSheet() {
        fragmentViewBinding.bottomSheet.animate()
            .translationY(0.0F)
            .duration = 500
    }

    private fun hideBottomSheet() {
        fragmentViewBinding.bottomSheet.animate()
            .translationY(fragmentViewBinding.bottomSheet.height.toFloat())
            .duration = 500
    }

    private fun tripNotStartedOnMap() {
        huaweiMap.apply {
            isMyLocationEnabled = false
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isZoomControlsEnabled = false

            setOnCameraMoveStartedListener {

                fragmentViewBinding.myLocationButton.visibility = View.INVISIBLE
                hideBottomSheet()
            }

            setOnCameraIdleListener {

                fragmentViewBinding.myLocationButton.visibility = View.VISIBLE
                showBottomSheet()
            }
        }
        observeAndAddTaxi()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun checkLocationPermission() {
        if (homeViewModel.checkPermissions()) {
            locationDialog.showDialog()
            findLocation()
        } else {
            findNavController().navigate(R.id.action_homeFragment_to_permissionFragment)
        }
    }

    private fun findLocation() {
        getLastKnownLocation()
        object : CountDownTimer(30_000, 1_000) {
            override fun onFinish() {
                locationDialog.dismissDialog()
                moveSpaciousLocationDialog()
            }

            override fun onTick(millisUntilFinished: Long) {
                homeViewModel.lastKnownLocation.value?.let {
                    cancel()
                }
            }

        }.start()
    }

    private fun moveSpaciousLocationDialog() {
        if (!this::spaciousLocationDialog.isInitialized) {
            spaciousLocationDialog = CustomDialog.getInstance(requireContext())
                .setTitle(getString(R.string.app_name))
                .setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_error))
                .setMessage(getString(R.string.location_place_error_message))
                .setPositiveButton(
                    getString(R.string.try_again),
                    object : CustomDialog.ICustomDialogClickListener {
                        override fun onClick() {
                            locationDialog.showDialog()
                            findLocation()
                        }
                    })
                .createDialog()
        }
        spaciousLocationDialog.showDialog()
    }

    private fun createTrip(route: Route): Trip {
        val path = route.paths[0]
        return Trip(
            startingLocation = path.startLocation, startingAddress = path.startAddress,
            destinationLocation = path.endLocation, destinationAddress = path.endAddress,
            cost = getCost(path.distance),
            route = route,
            driver = homeViewModel.driver
        )
    }

    private fun getLastKnownLocation() {
        homeViewModel.locationService.getLastLocation { lastLocation ->
            if (lastLocation.latitude != 0.0) {
                homeViewModel.setLastKnownLocation(lastLocation)
                locationDialog.dismissDialog()
            } else {
                locationUtil.checkLocationSettings {
                    homeViewModel.setLastKnownLocation(it)
                    locationDialog.dismissDialog()
                }
            }
        }
    }

    private fun navigateToDestinationFragment(isFromDestination: Boolean) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToDestinationFragment(isFromDestination)
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        fragmentViewBinding.huaweiMap.onStart()
    }

    override fun onStop() {
        super.onStop()
        fragmentViewBinding.huaweiMap.onStop()
    }

    override fun onResume() {
        super.onResume()
        fragmentViewBinding.huaweiMap.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentViewBinding.huaweiMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        fragmentViewBinding.huaweiMap.onLowMemory()
    }


    private fun observeAndAddTaxi() {
        homeViewModel.lastKnownLocation.observe(viewLifecycleOwner) {
            it?.let { latLang ->
                if (homeViewModel.isDestinationSelectedBefore.value == false) {
                    huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, 18f))
                    huaweiMap.clear()
                    addTaxi(latLang)
                    taxiFinalLocation = latLang
                }
            }
        }
    }

}
