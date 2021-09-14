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

package com.huawei.hitaxicodelab_java.ui.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.navigation.Navigation;

import com.huawei.hitaxicodelab_java.databinding.FragmentPermissionBinding;
import com.huawei.hitaxicodelab_java.service.LocationService;
import com.huawei.hitaxicodelab_java.ui.base.BaseFragment;
import com.huawei.hitaxicodelab_java.utils.LocationUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class PermissionFragment extends BaseFragment<PermissionViewModel, FragmentPermissionBinding> {

    private LocationUtil locationUtil;

    private final String tag = "PermissionFragment";

    @NotNull
    @Override
    public FragmentPermissionBinding getFragmentViewBinding(@NotNull LayoutInflater inflater, @Nullable ViewGroup container) {
        setNav();
        return FragmentPermissionBinding.inflate(inflater, container, false);
    }

    @NotNull
    @Override
    public PermissionViewModel getViewModel() {
        return new PermissionViewModel(new LocationService(requireContext()));
    }

    @Override
    @Deprecated
    public void onActivityCreated(@androidx.annotation.Nullable @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationUtil = new LocationUtil(requireActivity());

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setupListeners() {
        super.setupListeners();


        getFragmentViewBinding().btnAllow.setOnClickListener(view ->
        {
            getPermission();
            Handler handler = new Handler();
            handler.postDelayed(this::setNav, 3000);

            locationUtil.checkLocationSettings(latLng -> {
            });
        });
    }

    private void getPermission() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && androidx.core.content.ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            Log.e(tag, "PERMISSION GRANTED");
        }
    }

    private void setNav() {
        if (getViewModel().checkPermissions()) {
            Navigation.findNavController(requireView()).popBackStack();
        }
    }
}