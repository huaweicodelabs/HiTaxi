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

package com.huawei.hitaxicodelab_java.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hitaxicodelab_java.data.model.Car;
import com.huawei.hitaxicodelab_java.databinding.ItemBottomSheetCarsBinding;
import com.huawei.hitaxicodelab_java.listeners.IClickListener;
import com.huawei.hitaxicodelab_java.utils.CarTypes;
import com.huawei.hitaxicodelab_java.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class BottomSheetCarsAdapter extends RecyclerView.Adapter {
    private final ArrayList<CarTypes> cars;
    private final CarTypes selectedCarType;
    private final IClickListener clickListener;
    private int selectedCarPosition;

    public BottomSheetCarsAdapter(@NotNull List<CarTypes> cars, @NotNull CarTypes selectedCarType, @NotNull IClickListener clickListener) {
        super();
        this.cars = (ArrayList<CarTypes>) cars;
        this.selectedCarType = selectedCarType;
        this.clickListener = clickListener;
        this.selectedCarPosition = this.selectedCarIndex();
    }

    public final int getSelectedCarPosition() {
        return this.selectedCarPosition;
    }

    public final void setSelectedCarPosition(int var1) {
        this.selectedCarPosition = var1;
    }

    @Override
    @NotNull
    public BottomSheetCarsAdapter.CarsHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemBottomSheetCarsBinding itemBottomSheetCarsBinding = ItemBottomSheetCarsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BottomSheetCarsAdapter.CarsHolder(itemBottomSheetCarsBinding);
    }

    public int getItemCount() {
        return this.cars.size();
    }

    public void onBindViewHolder(@NotNull BottomSheetCarsAdapter.CarsHolder holder, int position) {

        CarTypes carItem = cars.get(position);
        holder.bind(carItem, position);
    }

    // $FF: synthetic method
    // $FF: bridge method
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder var1, int var2) {
        this.onBindViewHolder((BottomSheetCarsAdapter.CarsHolder) var1, var2);
    }

    private int selectedCarIndex() {
        return this.cars.indexOf(this.selectedCarType);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public final class CarsHolder extends RecyclerView.ViewHolder {
        private final ItemBottomSheetCarsBinding itemBinding;

        public CarsHolder(ItemBottomSheetCarsBinding itemBinding) {
            super((View) itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public final void bind(final CarTypes item, final int position) {
            Car car = item.getCar();
            itemBinding.carTitle.setText(car.getTitle());
            itemBinding.carImage.setImageResource(car.getIconId());


            LinearLayout linearLayout = itemBinding.carRoot;

            if (selectedCarPosition == getAdapterPosition()) {
                linearLayout.setAlpha(1.0F);
                View view = itemBinding.carSeparator;
                view.getLayoutParams().height = Utils.toDp(4);
            } else {
                linearLayout.setAlpha(0.4F);
                View view = itemBinding.carSeparator;
                view.getLayoutParams().height = Utils.toDp(2);
            }

            itemBinding.carSeparator.setBackgroundColor(car.getSeparatorColorId());
            itemBinding.carRoot.setOnClickListener((View.OnClickListener) (it -> {
                BottomSheetCarsAdapter.this.clickListener.onClick(item);
                BottomSheetCarsAdapter.this.setSelectedCarPosition(position);
                BottomSheetCarsAdapter.this.notifyDataSetChanged();
            }));

        }
    }
}

