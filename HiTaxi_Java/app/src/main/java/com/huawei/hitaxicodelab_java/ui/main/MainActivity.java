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

package com.huawei.hitaxicodelab_java.ui.main;

import android.view.LayoutInflater;

import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import com.huawei.hitaxicodelab_java.R;
import com.huawei.hitaxicodelab_java.databinding.ActivityMainBinding;
import com.huawei.hitaxicodelab_java.ui.base.BaseActivity;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @NotNull
    @Override
    public ActivityMainBinding getActivityViewBinding(@NotNull LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    @Override
    public void setup() {
        super.setup();
        navigateUser();
    }

    private void navigateUser(){
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        NavInflater inflater = navHostFragment.getNavController().getNavInflater();
        navHostFragment.getNavController().setGraph(getNavGraph(inflater));
    }

    private NavGraph getNavGraph(NavInflater inflater) {
        return inflater.inflate(R.navigation.app_nav_graph);
    }
}