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

package com.huawei.hitaxicodelab_java.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.huawei.hitaxicodelab_java.R;
import com.huawei.hitaxicodelab_java.databinding.CustomLottieDialogBinding;

public class LottieDialog {

    private final CustomLottieDialogBinding binding;
    private final Dialog dialog;
    private final Context context;
    private int lottieRawId;

    public LottieDialog(int lottieRawId, Context context) {

        this.lottieRawId = lottieRawId;
        this.context = context;
        binding = CustomLottieDialogBinding.inflate(LayoutInflater.from(context));
        dialog = createDialog();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(binding.getRoot());
        builder.setCancelable(false);
        binding.animationView.loop(true);

        binding.animationView.setAnimation(lottieRawId);

        return builder.create();
    }

    public void showDialog(){ dialog.show();}

    public void dismissDialog(){dialog.dismiss();}

    public static class Builder {
        int lottieRawId = R.raw.route_animation;
        Context context = null;

        public void setContext(Context context) {
            this.context = context;
        }

        public void build() {
            new LottieDialog(lottieRawId, context);
        }
    }
}
