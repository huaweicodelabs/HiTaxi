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

package com.huawei.hitaxicodelab_java.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.huawei.hitaxicodelab_java.adapter.BottomSheetCarsAdapter;
import com.huawei.hitaxicodelab_java.data.model.BottomSheetButtonTypes;
import com.huawei.hitaxicodelab_java.databinding.BottomSheetHomeBinding;
import com.huawei.hitaxicodelab_java.listeners.IClickListener;
import com.huawei.hitaxicodelab_java.utils.CarTypes;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.jvm.functions.Function1;

@AndroidEntryPoint
public class HomeBottomSheetDialog extends Fragment {

    private BottomSheetHomeBinding viewBinding;

    @NotNull
    ArrayList<CarTypes> cars;
    @NotNull
    CarTypes selectedCar;
    Function1 onItemClickListener;

    public HomeBottomSheetDialog(@NotNull Function1 onItemClickListener) {
        super();
        this.onItemClickListener = onItemClickListener;
        this.cars = kotlin.collections.CollectionsKt.arrayListOf(CarTypes.CLASSIC, CarTypes.CABIN, CarTypes.PREMIUM);
        this.selectedCar = CarTypes.CLASSIC;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        viewBinding = BottomSheetHomeBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.searchLayout.setOnClickListener(view1 ->
                HomeBottomSheetDialog.this.onItemClickListener.invoke(BottomSheetButtonTypes.FIRST_DESTINATION)
        );
        viewBinding.addressStart.setOnClickListener(view1 ->
                HomeBottomSheetDialog.this.onItemClickListener.invoke(BottomSheetButtonTypes.HOME)
        );
        viewBinding.addressDestination.setOnClickListener(view1 ->
                HomeBottomSheetDialog.this.onItemClickListener.invoke(BottomSheetButtonTypes.DESTINATION)
        );
        viewBinding.callTaxi.setOnClickListener(view1 ->
                HomeBottomSheetDialog.this.onItemClickListener.invoke(BottomSheetButtonTypes.CALL_TAXI)
        );
    }

    public void showAddressLayout(Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            viewBinding.addressLayout.setVisibility(View.VISIBLE);
            viewBinding.searchLayout.setVisibility(View.GONE);
        } else {
            viewBinding.addressLayout.setVisibility(View.GONE);
            viewBinding.searchLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setStartingAddress(String address) {
        viewBinding.addressStart.setText(address);
    }

    public void setDestinationAddress(String address) {
        viewBinding.addressDestination.setText(address);
    }

    public void setTaxiInformationMessage(String message) {
        viewBinding.taxiInfo.setText(message);
    }

    public void setTaxiArrivedTime(String time) {
        viewBinding.taxiArrivedTime.setText(time);
    }

    public void setTaxiPrice(String price) {
        viewBinding.taxiPrice.setText("$" + price);
    }

    public void setTaxiDistance(String distance) {
        viewBinding.taxiDistance.setText(distance);
    }

    public void setSelectedCarType(CarTypes carType) {
        selectedCar = carType;
        setCars(cars);
    }

    public HomeBottomSheetDialog setCars(List<CarTypes> cars) {
        if (!cars.isEmpty()) {
            this.cars = (ArrayList<CarTypes>) cars;
            viewBinding.cars.setVisibility(View.VISIBLE);
            viewBinding.cars.setLayoutManager(new GridLayoutManager(this.getContext(), 1, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
            viewBinding.cars.setAdapter(new BottomSheetCarsAdapter(
                    cars, selectedCar, (IClickListener<CarTypes>) car -> {
                selectedCar = car;
                HomeBottomSheetDialog.this.onItemClickListener.invoke(BottomSheetButtonTypes.CAR_TYPE_SELECTED);
            }
            ));
        }
        return this;
    }
}
