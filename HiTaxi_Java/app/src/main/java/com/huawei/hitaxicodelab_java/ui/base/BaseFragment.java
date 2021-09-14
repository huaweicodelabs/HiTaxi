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

package com.huawei.hitaxicodelab_java.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseFragment<VM extends ViewModel, VB extends ViewBinding> extends Fragment {

    VB fragmentViewBinding;

    public VB getFragmentViewBinding() {
        return fragmentViewBinding;
    }

    public void setFragmentViewBinding(VB fragmentViewBinding) {
        this.fragmentViewBinding = fragmentViewBinding;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.fragmentViewBinding = this.getFragmentViewBinding(inflater, container);
        return this.fragmentViewBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setup();
    }

    @NotNull
    public abstract VM getViewModel();

    @NotNull
    public abstract VB getFragmentViewBinding(@NotNull LayoutInflater inflater, @Nullable ViewGroup container);

    private void setup() {
        this.setupUi();
        this.setupListeners();
        this.setupObservers();
    }

    public void setupListeners() {
    }

    public void setupObservers() {
    }

    public void setupUi() {
    }
}
