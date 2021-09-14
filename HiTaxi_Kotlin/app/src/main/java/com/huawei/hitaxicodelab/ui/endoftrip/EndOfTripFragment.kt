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
package com.huawei.hitaxicodelab.ui.endoftrip

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.huawei.hitaxicodelab.R
import com.huawei.hitaxicodelab.databinding.FragmentEndOfTripBinding
import com.huawei.hitaxicodelab.ui.base.BaseFragment
import com.huawei.hitaxicodelab.ui.home.HomeViewModel
import com.huawei.hitaxicodelab.utils.roundTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EndOfTripFragment : BaseFragment<HomeViewModel, FragmentEndOfTripBinding>() {
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun getViewModel(): HomeViewModel = homeViewModel

    private val args: EndOfTripFragmentArgs by navArgs()

    private val TAG = "EndOfTripFragment"

    override fun getFragmentViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEndOfTripBinding {
        return FragmentEndOfTripBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        blockBackButton(viewLifecycleOwner)
    }

    override fun setupUi() {
        super.setupUi()

        fragmentViewBinding.apply {

            wontSeeRatingText.text = getString(R.string.will_not_see_rating, args.trip.driver.name)
            tripCost.text = getString(R.string.taxi_price, args.trip.cost.toString())

            rateDriver.averageRate.text =
                getString(R.string.average_rate, args.trip.driver.rate.roundTo(2).toString())
            rateVehicle.averageRate.text =
                getString(R.string.average_rate, args.trip.driver.carRate.roundTo(2).toString())

            rateDriver.ratingBar.rating = args.trip.driver.rate
            rateVehicle.ratingBar.rating = args.trip.driver.carRate
        }
    }

    override fun setupListeners() {
        super.setupListeners()

        fragmentViewBinding.skipRateButton.setOnClickListener { navigateToHome() }
        fragmentViewBinding.finishRateButton.setOnClickListener { sendUserRating() }
    }

    private fun navigateToHome() {
        homeViewModel.clearViewModel()
        val action = EndOfTripFragmentDirections.actionEndOfTripFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun sendUserRating() {
        val driverRate = fragmentViewBinding.rateDriver.ratingBar.rating
        val carRate = fragmentViewBinding.rateVehicle.ratingBar.rating

        Log.i(TAG, "Given driver rate: $driverRate")
        Log.i(TAG, "Given car rate: $carRate")

        lifecycleScope.launch {
            navigateToHome()
        }
    }
}