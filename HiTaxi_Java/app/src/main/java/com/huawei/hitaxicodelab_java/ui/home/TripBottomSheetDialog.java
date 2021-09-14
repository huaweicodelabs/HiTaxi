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

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.huawei.hitaxicodelab_java.R;
import com.huawei.hitaxicodelab_java.data.model.Gender;
import com.huawei.hitaxicodelab_java.data.model.RouteAction;
import com.huawei.hitaxicodelab_java.data.model.Step;
import com.huawei.hitaxicodelab_java.data.model.Trip;
import com.huawei.hitaxicodelab_java.data.model.TripListener;
import com.huawei.hitaxicodelab_java.databinding.BottomSheetTripBinding;
import com.huawei.hitaxicodelab_java.utils.CarTypes;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.jvm.functions.Function1;

@AndroidEntryPoint
public class TripBottomSheetDialog extends Fragment {
    private Trip trip;
    private Function1 tripListener;
    private BottomSheetTripBinding viewBinding;

    public TripBottomSheetDialog(Trip trip, Function1 tripListener) {
        super();
        this.trip = trip;
        this.tripListener = tripListener;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        viewBinding = BottomSheetTripBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUi();
    }

    public void setupUi() {
        setDriverData();
        setLocationData();
        tripListener.invoke(TripListener.START_TRIP);
    }

    private void setDriverData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewBinding.driverName.setText(
                    Html.fromHtml(
                            getString(
                                    R.string.driver_name,
                                    trip.getDriver().getName() + " " + trip.getDriver().getSurname()
                            ), Html.FROM_HTML_MODE_LEGACY
                    )
            );
            viewBinding.driverGender.setText(
                    Html.fromHtml(
                            getString(
                                    R.string.driver_gender,
                                    Gender.getGenderText(trip.getDriver().getGender())
                            ), Html.FROM_HTML_MODE_LEGACY
                    )
            );
            viewBinding.driverAge.setText(
                    Html.fromHtml(
                            getString(R.string.driver_age, trip.getDriver().getAge().toString()),
                            Html.FROM_HTML_MODE_LEGACY
                    )
            );
            viewBinding.driverCarType.setText(
                    Html.fromHtml(
                            getString(
                                    R.string.driver_car_type,
                                    CarTypes.getCarType(trip.getDriver().getCarType()).getCar().getTitle().toString()
                            ), Html.FROM_HTML_MODE_LEGACY)
            );
            viewBinding.driverVehicle.setText(
                    Html.fromHtml(
                            getString(R.string.driver_car, trip.getDriver().getCarModel()),
                            Html.FROM_HTML_MODE_LEGACY
                    )
            );
            viewBinding.driverVehiclePlate.setText(
                    Html.fromHtml(
                            getString(R.string.driver_car_plate, trip.getDriver().getCarPlate()),
                            Html.FROM_HTML_MODE_LEGACY
                    )
            );
        } else {
            viewBinding.driverName.setText(
                    getString(R.string.driver_name, trip.getDriver().getName() + " " + trip.getDriver().getSurname())
            );

            viewBinding.driverGender.setText(
                    getString(R.string.driver_gender, Gender.getGenderText(trip.getDriver().getGender()))
            );

            viewBinding.driverAge.setText(
                    getString(R.string.driver_age, trip.getDriver().getAge().toString())
            );
            viewBinding.driverCarType.setText(
                    getString(R.string.driver_car_type, CarTypes.getCarType(trip.getDriver().getCarType()))
            );
            viewBinding.driverVehicle.setText(
                    getString(R.string.driver_car, trip.getDriver().getCarModel())
            );
            viewBinding.driverVehiclePlate.setText(
                    getString(R.string.driver_car_plate, trip.getDriver().getCarPlate())
            );
        }
    }

    private void setLocationData() {
        viewBinding.startLocation.locationItemIcon.setImageResource(R.drawable.ic_start_location_pin);
        viewBinding.startLocation.locationItemTitle.setText(getString(R.string.pick_up));
        viewBinding.startLocation.locationItem.setText(trip.getRoute().paths.get(0).startAddress);
        viewBinding.endLocation.locationItemIcon.setImageResource(R.drawable.ic_end_location_pin);
        viewBinding.endLocation.locationItemTitle.setText(getString(R.string.drop_off));
        viewBinding.endLocation.locationItem.setText(trip.getRoute().paths.get(0).endAddress);
    }

    public void setStepData(int index) {
        if (index < trip.getRoute().paths.get(0).steps.size()) {
            Step step = trip.getRoute().paths.get(0).steps.get(index);
            viewBinding.stepInstruction.setText(step.instruction);
            viewBinding.stepRoadName.setText(step.roadName);
            viewBinding.stepDistanceTime.setText(step.distanceText + " - " + step.durationText);
            viewBinding.stepActionIcon.setImageResource(RouteAction.getIconByAction(step.action));
        }
    }


}
