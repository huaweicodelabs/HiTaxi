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
package com.huawei.hitaxicodelab.custom

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.airbnb.lottie.LottieDrawable
import com.huawei.hitaxicodelab.R
import com.huawei.hitaxicodelab.databinding.CustomLottieDialogBinding

class LottieDialog private constructor(lottieRawId: Int, context: Context?) {
    private lateinit var binding: CustomLottieDialogBinding
    private lateinit var dialog: Dialog
    private var lottieRawId: Int = R.raw.route_animation
    private lateinit var context: Context

    init {
        context?.let {
            this.lottieRawId = lottieRawId
            this.context = it
            binding = CustomLottieDialogBinding.inflate(LayoutInflater.from(it))
            dialog = createDialog()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    private fun createDialog(): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setView(binding.root)
        builder.setCancelable(false)
        binding.animationView.repeatCount = LottieDrawable.INFINITE

        binding.animationView.setAnimation(lottieRawId)

        return builder.create()
    }

    fun showDialog() = dialog.show()

    fun dismissDialog() = dialog.dismiss()

    class Builder {
        var lottieRawId: Int = R.raw.route_animation
        var context: Context? = null
        fun setLottieRawId(lottieRawId: Int) = apply { this.lottieRawId = lottieRawId }
        fun setContext(context: Context) = apply { this.context = context }
        fun build() = LottieDialog(lottieRawId, context)
    }
}