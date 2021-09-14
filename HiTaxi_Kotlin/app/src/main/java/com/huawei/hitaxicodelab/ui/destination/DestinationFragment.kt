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
package com.huawei.hitaxicodelab.ui.destination

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.huawei.hitaxicodelab.BuildConfig
import com.huawei.hitaxicodelab.R
import com.huawei.hitaxicodelab.databinding.FragmentDestinationBinding
import com.huawei.hitaxicodelab.ui.base.BaseFragment
import com.huawei.hitaxicodelab.ui.home.HomeViewModel
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.OnMapReadyCallback
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.site.widget.SearchIntent
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder

@AndroidEntryPoint
class DestinationFragment : BaseFragment<HomeViewModel, FragmentDestinationBinding>(),
    OnMapReadyCallback {

    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun getViewModel(): HomeViewModel = homeViewModel

    private lateinit var searchIntent: SearchIntent
    private lateinit var huaweiMap: HuaweiMap

    private val args: DestinationFragmentArgs by navArgs()

    var tempStartingAddress: String? = null
    var tempStartingLocation: LatLng? = null
    var tempDestinationAddress: String? = null
    var tempDestinationLocation: LatLng? = null

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDestinationBinding {
        return FragmentDestinationBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchIntent()
    }

    override fun setupUi() {
        super.setupUi()
        fragmentViewBinding.huaweiMap.apply {
            onCreate(null)
            getMapAsync(this@DestinationFragment)
        }
        changeLocationPinColor()
    }

    override fun setupListeners() {
        fragmentViewBinding.searchLayout.setOnClickListener {
            showSiteSearchFragment()
        }

        fragmentViewBinding.myLocationButton.setOnClickListener {
            focusUserCurrentLocation()
        }

        fragmentViewBinding.setDestinationButton.setOnClickListener {

            if (args.isFromDestination) {
                if (homeViewModel.isDestinationSelectedBefore.value == false) {
                    homeViewModel.setIsDestinationSelectedBefore(true)
                    homeViewModel.setStartingAddress(getString(R.string.your_location))
                    homeViewModel.lastKnownLocation.value?.let {
                        homeViewModel.setStartingLocation(it)
                    }
                }
                val location = tempDestinationLocation
                val address = tempDestinationAddress
                if (location != null && address != null) {
                    homeViewModel.setDestinationLocation(location)
                    homeViewModel.setDestinationAddress(address)
                    findNavController().navigate(R.id.action_destinationFragment_to_homeFragment)
                }
            } else {
                val location = tempStartingLocation
                val address = tempStartingAddress
                if (location != null && address != null) {
                    homeViewModel.setStartingLocation(location)
                    homeViewModel.setStartingAddress(address)
                    findNavController().navigate(R.id.action_destinationFragment_to_homeFragment)
                }
            }
        }
    }


    private fun saveAndShowAddress(latLng: LatLng) {
        fragmentViewBinding.searchAddressText.text = homeViewModel.getAddress(latLng)

        if (args.isFromDestination) {
            tempDestinationAddress = homeViewModel.getAddress(latLng)
            tempDestinationLocation = latLng
        } else {
            tempStartingAddress = homeViewModel.getAddress(latLng)
            tempStartingLocation = latLng
        }
    }


    override fun onMapReady(map: HuaweiMap) {
        huaweiMap = map

        huaweiMap.apply {
            uiSettings.isZoomControlsEnabled = false
            setOnCameraIdleListener {
                val latitude = huaweiMap.cameraPosition.target.latitude
                val longitude = huaweiMap.cameraPosition.target.longitude
                val latLng = LatLng(latitude, longitude)
                saveAndShowAddress(latLng)
            }
        }


        if (args.isFromDestination) {
            if (homeViewModel.destinationLocation.value == null) {
                focusUserCurrentLocation()
            } else {
                val location = homeViewModel.destinationLocation.value
                huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f))
            }
        } else {
            if (homeViewModel.startingLocation.value == null) {
                focusUserCurrentLocation()
            } else {
                val location = homeViewModel.startingLocation.value
                huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (SearchIntent.SEARCH_REQUEST_CODE == requestCode) {
            if (SearchIntent.isSuccess(resultCode)) {
                val site = searchIntent.getSiteFromIntent(data)
                val latLng = LatLng(site.location.lat, site.location.lng)
                huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                saveAndShowAddress(latLng)
            }
        }
    }

    private fun initSearchIntent() {
        searchIntent = SearchIntent()
        searchIntent.setApiKey(URLEncoder.encode(BuildConfig.API_KEY, "UTF-8"))
    }

    private fun showSiteSearchFragment() {
        val intent = searchIntent.getIntent(requireActivity())
        startActivityForResult(intent, SearchIntent.SEARCH_REQUEST_CODE)
    }


    private fun changeLocationPinColor() {
        if (args.isFromDestination.not()) {
            fragmentViewBinding.locationPinImageView.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_start_location_pin
                )
            )
        }
    }

    private fun focusUserCurrentLocation() {
        homeViewModel.lastKnownLocation.value?.let {
            huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 18f))
        }
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
}