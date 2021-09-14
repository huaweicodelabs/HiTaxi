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
package com.huawei.hitaxicodelab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hitaxicodelab.databinding.ItemBottomSheetCarsBinding
import com.huawei.hitaxicodelab.listeners.IClickListener
import com.huawei.hitaxicodelab.data.model.CarTypes
import com.huawei.hitaxicodelab.utils.toDp

class BottomSheetCarsAdapter(
    private val cars: ArrayList<CarTypes>,
    private val selectedCarType: CarTypes,
    private val clickListener: IClickListener<CarTypes>
) :
    RecyclerView.Adapter<BottomSheetCarsAdapter.CarsHolder>() {

    var selectedCarPosition = selectedCarIndex()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsHolder {
        val itemBinding =
            ItemBottomSheetCarsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarsHolder(itemBinding)
    }

    override fun getItemCount(): Int = cars.size

    override fun onBindViewHolder(holder: CarsHolder, position: Int) {
        val carItem = cars[position]
        holder.bind(carItem, position)

    }

    private fun selectedCarIndex() = cars.indexOf(selectedCarType)

    override fun getItemViewType(position: Int): Int = position

    inner class CarsHolder(private val itemBinding: ItemBottomSheetCarsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: CarTypes, position: Int) {
            val car = item.car
            itemBinding.carTitle.text = car.title
            itemBinding.carImage.setImageResource(car.iconId)

            itemBinding.carRoot.apply {
                if (selectedCarPosition == adapterPosition) {
                    alpha = 1.0F
                    itemBinding.carSeparator.layoutParams.height = 4.toDp()
                } else {
                    alpha = 0.4F
                    itemBinding.carSeparator.layoutParams.height = 2.toDp()
                }
            }
            itemBinding.carSeparator.setBackgroundColor(car.separatorColorId)
            itemBinding.carRoot.setOnClickListener {
                clickListener.onClick(item)
                selectedCarPosition = position
                notifyDataSetChanged()
            }
        }
    }
}