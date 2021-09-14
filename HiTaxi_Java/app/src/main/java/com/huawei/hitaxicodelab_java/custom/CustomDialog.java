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
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import com.huawei.hitaxicodelab_java.databinding.CustomDialogBinding;

public class CustomDialog {
    private final AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private final CustomDialogBinding dialogBinding;

    public CustomDialog(Context context) {
        dialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(context));
        builder = new AlertDialog.Builder(context).setCancelable(false);
    }

    public CustomDialog setTitle(String title)  {
        dialogBinding.dialogTitle.setVisibility(View.VISIBLE);
        dialogBinding.dialogTitle.setText(title);

        return this;
    }

    public CustomDialog setMessage(String title){
        dialogBinding.dialogMessage.setVisibility(View.VISIBLE);
        dialogBinding.dialogMessage.setText(title);

        return this;
    }

    public CustomDialog setIcon(Drawable icon){
        dialogBinding.dialogIcon.setVisibility(View.VISIBLE);
        dialogBinding.dialogIcon.setImageDrawable(icon);

        return this;
    }

    public CustomDialog setCancelButton(String negativeText){
        dialogBinding.negativeButton.setVisibility(View.VISIBLE);
        dialogBinding.negativeButton.setText(negativeText);
        dialogBinding.negativeButton.setOnClickListener(v -> dismissDialog());
        return this;
    }

    public CustomDialog setPositiveButton(
            String positiveText,
            ICustomDialogClickListener onClickListener
    ){
        dialogBinding.positiveButton.setVisibility(View.VISIBLE);
        dialogBinding.positiveButton.setText(positiveText);
        dialogBinding.positiveButton.setOnClickListener(v ->{
            onClickListener.onClick();
            dismissDialog();
        });

        return this;
    }

    public CustomDialog createDialog(){
        builder.setView(dialogBinding.getRoot());
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return this;
    }

    public void showDialog() {
        if (this.alertDialog==null) {
            createDialog();
        }
        alertDialog.show();
    }

    public void dismissDialog(){ alertDialog.dismiss();}

    public interface ICustomDialogClickListener {
        void onClick();
    }
}
