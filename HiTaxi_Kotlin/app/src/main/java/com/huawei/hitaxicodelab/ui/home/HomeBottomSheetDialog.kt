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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.huawei.hitaxicodelab.R
import com.huawei.hitaxicodelab.adapter.BottomSheetCarsAdapter
import com.huawei.hitaxicodelab.databinding.BottomSheetHomeBinding
import com.huawei.hitaxicodelab.listeners.IClickListener
import com.huawei.hitaxicodelab.data.model.BottomSheetButtonTypes
import com.huawei.hitaxicodelab.data.model.CarTypes


class HomeBottomSheetDialog(
    private val onItemClickListener: (BottomSheetButtonTypes) -> Unit
) : Fragment() {
    private lateinit var viewBinding: BottomSheetHomeBinding

    private var cars: ArrayList<CarTypes> = arrayListOf(
        CarTypes.CLASSIC,
        CarTypes.CABIN,
        CarTypes.PREMIUM
    )

    var selectedCar = CarTypes.CLASSIC

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = BottomSheetHomeBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.searchLayout.setOnClickListener {
            onItemClickListener(BottomSheetButtonTypes.FIRST_DESTINATION)
        }
        viewBinding.addressStart.setOnClickListener {
            onItemClickListener(BottomSheetButtonTypes.HOME)
        }
        viewBinding.addressDestination.setOnClickListener {
            onItemClickListener(BottomSheetButtonTypes.DESTINATION)
        }

        viewBinding.callTaxi.setOnClickListener {
            onItemClickListener(BottomSheetButtonTypes.CALL_TAXI)
        }
    }

    private fun setCars(cars: ArrayList<CarTypes>): HomeBottomSheetDialog {
        if (cars.size > 0) {
            this.cars = cars
            viewBinding.cars.apply {
                visibility = View.VISIBLE
                layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
                adapter =
                    BottomSheetCarsAdapter(cars, selectedCar, object : IClickListener<CarTypes> {
                        override fun onClick(pressed: CarTypes) {
                            selectedCar = pressed
                            onItemClickListener(BottomSheetButtonTypes.CAR_TYPE_SELECTED)
                        }
                    })
            }
        }
        return this
    }

    fun showAddressLayout(value: Boolean) {
        if (value) {
            viewBinding.addressLayout.visibility = View.VISIBLE
            viewBinding.searchLayout.visibility = View.GONE
        } else {
            viewBinding.addressLayout.visibility = View.GONE
            viewBinding.searchLayout.visibility = View.VISIBLE
        }
    }


    fun setStartingAddress(address: String) {
        viewBinding.addressStart.text = address
    }

    fun setDestinationAddress(address: String) {
        viewBinding.addressDestination.text = address
    }

    fun setTaxiInformationMessage(message: String) {
        viewBinding.taxiInfo.text = message
    }

    fun setTaxiArrivedTime(time: String) {
        viewBinding.taxiArrivedTime.text = time
    }

    fun setTaxiPrice(price: String) {
        viewBinding.taxiPrice.text = getString(R.string.taxi_price, price)
    }

    fun setTaxiDistance(distance: String) {
        viewBinding.taxiDistance.text = distance
    }

    fun setSelectedCarType(carType: CarTypes) {
        selectedCar = carType
        setCars(cars)
    }

}