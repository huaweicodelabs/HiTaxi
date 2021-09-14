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
package com.huawei.hitaxicodelab.ui.splash

import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.huawei.hitaxicodelab.R
import com.huawei.hitaxicodelab.databinding.FragmentSplashBinding
import com.huawei.hitaxicodelab.ui.base.BaseFragment
import com.huawei.hitaxicodelab.ui.home.HomeViewModel
import com.huawei.hitaxicodelab.utils.LocationUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<HomeViewModel, FragmentSplashBinding>() {

    private val splashViewModel: HomeViewModel by viewModels()
    private lateinit var locationUtil: LocationUtil

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun getViewModel(): HomeViewModel = splashViewModel

    override fun setupUi() {
        super.setupUi()

        setupAnimation()

        if (isLocationEnabled()) {
            Handler(Looper.getMainLooper()).postDelayed({
                view?.post {
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
            }, 3000)
        } else {
            locationUtil = LocationUtil(requireActivity())
            locationUtil.checkLocationSettings {
                while (!isLocationEnabled()) {
                    if (!isLocationEnabled()) {
                        isLocationEnabled()
                    } else {

                        Handler(Looper.getMainLooper()).postDelayed({
                            view?.post {
                                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                            }
                        }, 3000)
                    }
                }
                if (!isLocationEnabled()) {
                    isLocationEnabled()
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        view?.post {
                            findNavController().currentDestination?.let {
                                if (it.id == R.id.splashFragment) {
                                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                                }
                            }
                        }
                    }, 3000)

                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
            print(ex.message)
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
            print(ex.message)
        }

        return !(!gpsEnabled && !networkEnabled)
    }

    private fun setupAnimation() {

        val a: Animation = AnimationUtils.loadAnimation(context, R.anim.splash_animation)
        a.reset()

        fragmentViewBinding.textViewAnimation.clearAnimation()
        fragmentViewBinding.textViewAnimation.startAnimation(a)
    }
}