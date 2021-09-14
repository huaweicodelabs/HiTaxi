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
package com.huawei.hitaxicodelab.ui.main

import android.view.LayoutInflater
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import com.huawei.hitaxicodelab.R
import com.huawei.hitaxicodelab.databinding.ActivityMainBinding
import com.huawei.hitaxicodelab.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getActivityViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setup() {
        super.setup()
        navigateUser()
    }

    private fun navigateUser(){
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        navHostFragment.navController.graph = getNavGraph(inflater)
    }

    private fun getNavGraph(inflater: NavInflater): NavGraph {
        return inflater.inflate(R.navigation.app_nav_graph)
    }
}