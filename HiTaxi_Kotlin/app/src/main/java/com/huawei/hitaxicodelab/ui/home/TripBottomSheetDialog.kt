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
package com.huawei.hitaxicodelab.ui.home

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.huawei.hitaxicodelab.R
import com.huawei.hitaxicodelab.data.model.Gender
import com.huawei.hitaxicodelab.data.model.RouteAction
import com.huawei.hitaxicodelab.data.model.Trip
import com.huawei.hitaxicodelab.data.model.TripListener
import com.huawei.hitaxicodelab.databinding.BottomSheetTripBinding
import com.huawei.hitaxicodelab.data.model.CarTypes

class TripBottomSheetDialog(
    private val trip: Trip,
    private val tripListener: (TripListener) -> Unit
) : Fragment() {
    private lateinit var viewBinding: BottomSheetTripBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = BottomSheetTripBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        setDriverData()
        setLocationData()
        tripListener(TripListener.START_TRIP)
    }

    private fun setDriverData() {
        viewBinding.apply {

//            Glide.with(requireContext())
//                .load(trip.driver.picture)
//                .into(driverImage)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                driverName.text = Html.fromHtml(
                    getString(
                        R.string.driver_name,
                        "${trip.driver.name} ${trip.driver.surname}"
                    ), Html.FROM_HTML_MODE_LEGACY
                )
                driverGender.text = Html.fromHtml(
                    getString(
                        R.string.driver_gender,
                        Gender.getGenderText(trip.driver.gender)
                    ), Html.FROM_HTML_MODE_LEGACY
                )
                driverAge.text = Html.fromHtml(
                    getString(R.string.driver_age, trip.driver.age.toString()),
                    Html.FROM_HTML_MODE_LEGACY
                )
                driverCarType.text = Html.fromHtml(
                    getString(
                        R.string.driver_car_type,
                        CarTypes.getCarType(trip.driver.carType).car.title
                    ), Html.FROM_HTML_MODE_LEGACY
                )
                driverVehicle.text = Html.fromHtml(
                    getString(R.string.driver_car, trip.driver.carModel),
                    Html.FROM_HTML_MODE_LEGACY
                )
                driverVehiclePlate.text = Html.fromHtml(
                    getString(R.string.driver_car_plate, trip.driver.carPlate),
                    Html.FROM_HTML_MODE_LEGACY
                )
            } else {
                driverName.text =
                    getString(R.string.driver_name, "${trip.driver.name} ${trip.driver.surname}")
                driverGender.text =
                    getString(R.string.driver_gender, Gender.getGenderText(trip.driver.gender))
                driverAge.text = getString(R.string.driver_age, trip.driver.age.toString())
                driverCarType.text =
                    getString(R.string.driver_car_type, CarTypes.getCarType(trip.driver.carType))
                driverVehicle.text = getString(R.string.driver_car, trip.driver.carModel)
                driverVehiclePlate.text =
                    getString(R.string.driver_car_plate, trip.driver.carPlate)
            }
        }
    }

    private fun setLocationData() {
        viewBinding.apply {
            startLocation.locationItemIcon.setImageResource(R.drawable.ic_start_location_pin)
            startLocation.locationItemTitle.text = getString(R.string.pick_up)
            startLocation.locationItem.text = trip.route.paths[0].startAddress

            endLocation.locationItemIcon.setImageResource(R.drawable.ic_end_location_pin)
            endLocation.locationItemTitle.text = getString(R.string.drop_off)
            endLocation.locationItem.text = trip.route.paths[0].endAddress
        }
    }

    fun setStepData(index: Int) {
        if (index < trip.route.paths[0].steps.size) {
            val step = trip.route.paths[0].steps[index]

            viewBinding.apply {
                stepInstruction.text = step.instruction
                stepRoadName.text = step.roadName
                val text = "${step.distanceText} - ${step.durationText}"
                stepDistanceTime.text = text
                stepActionIcon.setImageResource(RouteAction.getIconByAction(step.action))
            }
        }
    }
}