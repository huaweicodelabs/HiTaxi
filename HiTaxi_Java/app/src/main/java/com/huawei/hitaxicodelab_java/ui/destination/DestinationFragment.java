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

package com.huawei.hitaxicodelab_java.ui.destination;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.huawei.hitaxicodelab_java.BuildConfig;
import com.huawei.hitaxicodelab_java.R;
import com.huawei.hitaxicodelab_java.databinding.FragmentDestinationBinding;
import com.huawei.hitaxicodelab_java.ui.base.BaseFragment;
import com.huawei.hitaxicodelab_java.ui.home.HomeViewModel;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.widget.SearchIntent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DestinationFragment extends BaseFragment<HomeViewModel, FragmentDestinationBinding>
        implements OnMapReadyCallback {

    HuaweiMap huaweiMap;
    SearchIntent searchIntent;
    HomeViewModel homeViewModel;

    String tempStartingAddress;
    LatLng tempStartingLocation;
    String tempDestinationAddress;
    LatLng tempDestinationLocation;

    @NotNull
    @Override
    public HomeViewModel getViewModel() {
        return homeViewModel;
    }

    @NotNull
    @Override
    public FragmentDestinationBinding getFragmentViewBinding(
            @NotNull LayoutInflater inflater,
            @Nullable ViewGroup container) {
        return FragmentDestinationBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSearchIntent();
    }

    @Override
    public void setupListeners() {
        super.setupListeners();
        getFragmentViewBinding().searchLayout.setOnClickListener(view -> showSiteSearchFragment());

        getFragmentViewBinding().myLocationButton.setOnClickListener(view -> focusUserCurrentLocation());

        getFragmentViewBinding().setDestinationButton.setOnClickListener(view -> {
            if (DestinationFragmentArgs.fromBundle(getArguments()).getIsFromDestination()) {
                if (Boolean.FALSE.equals(homeViewModel.getIsDestinationSelectedBefore().getValue())) {
                    homeViewModel.setIsDestinationSelectedBefore(true);
                    homeViewModel.setStartingAddress(getString(R.string.your_location));
                    if (homeViewModel.getLastKnownLocation().getValue() != null) {
                        homeViewModel.setStartingLocation(homeViewModel.getLastKnownLocation().getValue());
                    }
                }
                LatLng location = tempDestinationLocation;
                String address = tempDestinationAddress;
                if (location != null && address != null) {
                    homeViewModel.setDestinationLocation(location);
                    homeViewModel.setDestinationAddress(address);
                    NavHostFragment.findNavController(this).navigate(R.id.action_destinationFragment_to_homeFragment);
                }
            } else {
                LatLng location = tempStartingLocation;
                String address = tempStartingAddress;
                if (location != null && address != null) {
                    homeViewModel.setStartingLocation(location);
                    homeViewModel.setStartingAddress(address);
                    NavHostFragment.findNavController(this).navigate(R.id.action_destinationFragment_to_homeFragment);
                }
            }
        });
    }

    @Override
    public void setupUi() {
        super.setupUi();
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        getFragmentViewBinding().huaweiMap.onCreate(null);
        getFragmentViewBinding().huaweiMap.getMapAsync(this);
        changeLocationPinColor();
    }

    @Override
    public void onMapReady(HuaweiMap map) {
        huaweiMap = map;

        huaweiMap.getUiSettings().setZoomControlsEnabled(false);
        huaweiMap.setOnCameraIdleListener(() -> {
            double latitude = huaweiMap.getCameraPosition().target.latitude;
            double longitude = huaweiMap.getCameraPosition().target.longitude;
            LatLng latLng = new LatLng(latitude, longitude);
            saveAndShowAddress(latLng);
        });

        if (DestinationFragmentArgs.fromBundle(getArguments()).getIsFromDestination()) {
            if (homeViewModel.getDestinationLocation().getValue() == null) {
                focusUserCurrentLocation();
            } else {
                LatLng location = homeViewModel.getDestinationLocation().getValue();
                huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f));
            }
        } else {
            if (homeViewModel.getStartingLocation().getValue() == null) {
                focusUserCurrentLocation();
            } else {
                LatLng location = homeViewModel.getStartingLocation().getValue();
                huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f));
            }
        }
    }

    private void initSearchIntent() {
        searchIntent = new SearchIntent();
        try {
            final String encodedPath = URLEncoder.encode(BuildConfig.API_KEY, "UTF-8");
            searchIntent.setApiKey(encodedPath);
        }catch(UnsupportedEncodingException ec) {
            Log.d("TAG", ec.getLocalizedMessage());
        }
    }

    private void changeLocationPinColor() {
        if (!DestinationFragmentArgs.fromBundle(getArguments()).getIsFromDestination()) {
            getFragmentViewBinding().locationPinImageView.setImageDrawable(
                    ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_start_location_pin));
        }
    }

    private void focusUserCurrentLocation() {
        if (homeViewModel.getLastKnownLocation().getValue() != null) {
            huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeViewModel.getLastKnownLocation().getValue(), 18f));
        }
    }

    private void showSiteSearchFragment() {
        Intent intent = searchIntent.getIntent(requireActivity());
        startActivityForResult(intent, SearchIntent.SEARCH_REQUEST_CODE);
    }

    @Override
    @Deprecated
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (SearchIntent.SEARCH_REQUEST_CODE == requestCode && SearchIntent.isSuccess(resultCode)) {
            Site site = searchIntent.getSiteFromIntent(data);
            LatLng latLng = new LatLng(site.location.lat, site.location.lng);
            huaweiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
            saveAndShowAddress(latLng);
        }
    }

    private void saveAndShowAddress(LatLng latLng) {
        getFragmentViewBinding().searchAddressText.setText(homeViewModel.getAddress(latLng));

        if (DestinationFragmentArgs.fromBundle(getArguments()).getIsFromDestination()) {
            tempDestinationAddress = homeViewModel.getAddress(latLng);
            tempDestinationLocation = latLng;
        } else {
            tempStartingAddress = homeViewModel.getAddress(latLng);
            tempStartingLocation = latLng;
        }
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
    public void onDestroy() {
        super.onDestroy();
        getFragmentViewBinding().huaweiMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        getFragmentViewBinding().huaweiMap.onLowMemory();
    }
}
