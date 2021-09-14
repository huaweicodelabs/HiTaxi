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

package com.huawei.hitaxicodelab_java.ui.splash;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;

import com.huawei.hitaxicodelab_java.R;
import com.huawei.hitaxicodelab_java.databinding.FragmentSplashBinding;
import com.huawei.hitaxicodelab_java.ui.base.BaseFragment;
import com.huawei.hitaxicodelab_java.utils.LocationUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashFragment extends BaseFragment<SplashViewModel, FragmentSplashBinding> {

    @NotNull
    @Override
    public FragmentSplashBinding getFragmentViewBinding(@NotNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentSplashBinding.inflate(inflater, container, false);
    }

    @NotNull
    @Override
    public SplashViewModel getViewModel() {
        return new SplashViewModel();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setupUi() {
        super.setupUi();

        setupAnimation();

        if (isLocationEnabled()) {
            new Handler().postDelayed(() -> getFragmentViewBinding().getRoot().getRootView().post(() -> {
                Navigation.findNavController(requireView()).navigate(R.id.action_splashFragment_to_homeFragment);
            }), 3000);
        } else {
            LocationUtil locationUtil = new LocationUtil(requireActivity());
            locationUtil.checkLocationSettings(latLng -> {
                while (!isLocationEnabled()) {
                    if (!isLocationEnabled()) {
                        isLocationEnabled();
                    } else {
                        new Handler().postDelayed(() -> requireView().post(() -> Navigation.findNavController(requireView()).navigate(R.id.action_splashFragment_to_homeFragment)), 3000);
                    }
                }
                if (!isLocationEnabled()) {
                    isLocationEnabled();
                } else {
                    new Handler().postDelayed(() -> requireView().post(() -> {
                        if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.splashFragment) {
                            Navigation.findNavController(requireView()).navigate(R.id.action_splashFragment_to_homeFragment);
                        }
                    }), 3000);

                }
            });
        }
    }

    private Boolean isLocationEnabled(){
        LocationManager lm = (LocationManager)( requireContext().getSystemService(Context.LOCATION_SERVICE));
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e("isLocationEnabled", ex.getMessage());
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.e("isLocationEnabled", ex.getMessage());
        }

        return !(!gpsEnabled && !networkEnabled);
    }


    private void setupAnimation() {
        Animation a = AnimationUtils.loadAnimation(this.getContext(), R.anim.splash_animation);
        a.reset();

        this.getFragmentViewBinding().textViewAnimation.clearAnimation();
        this.getFragmentViewBinding().textViewAnimation.startAnimation(a);
    }

}
