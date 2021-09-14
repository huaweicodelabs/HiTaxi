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

package com.huawei.hitaxicodelab.ui.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.huawei.hitaxicodelab.databinding.FragmentPermissionBinding
import com.huawei.hitaxicodelab.ui.base.BaseFragment
import com.huawei.hitaxicodelab.utils.LocationUtil
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PermissionFragment : BaseFragment<PermissionViewModel, FragmentPermissionBinding>() {
    private val permissionViewModel: PermissionViewModel by viewModels()

    private var context: PermissionFragment? = null
    private lateinit var locationUtil: LocationUtil

    private val TAG = "PermissionFragment"

    override fun getViewModel(): PermissionViewModel = permissionViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        locationUtil = LocationUtil(requireActivity())
    }

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPermissionBinding {
        context = this
        setNav()
        return FragmentPermissionBinding.inflate(inflater, container, false)
    }


    @SuppressLint("UseRequireInsteadOfGet")
    override fun setupListeners() {
        super.setupListeners()


        fragmentViewBinding.btnAllow.setOnClickListener {
            //Allow Here


            getPermission()
            val handler = android.os.Handler(Looper.getMainLooper())
            handler.postDelayed({ setNav() }, 3000)

            locationUtil.checkLocationSettings {

            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    fun getPermission() {
        if (ActivityCompat.checkSelfPermission(
                getContext()!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                getContext()!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                activity!!, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                2
            )
        } else {
            Log.e(TAG, "PERMISSION GRANTED")
        }
    }


    @SuppressLint("UseRequireInsteadOfGet")
    fun setNav() {
        if (permissionViewModel.checkPermissions()) {
            findNavController().popBackStack()
        }
    }

}