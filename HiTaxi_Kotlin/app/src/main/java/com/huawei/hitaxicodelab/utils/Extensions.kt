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
package com.huawei.hitaxicodelab.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.huawei.hitaxicodelab.data.model.Coordinate
import com.huawei.hms.maps.model.LatLng
import java.util.*

fun Int.toDp() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

fun Coordinate.toLatLng() = LatLng(this.lat, this.lng)

fun com.huawei.hms.site.api.model.Coordinate.toLatLng() = LatLng(this.lat, this.lng)

fun LatLng?.toCoordinate(): Coordinate {
    return this?.let {
        Coordinate(this.latitude, this.longitude)
    } ?: kotlin.run {
        throw NullPointerException()
    }
}

fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable)
        return this.bitmap
    val bitmap =
        Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)

    return bitmap
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Long.secondToMillisecond() = this * 1000

fun Float.roundTo(n: Int): Float {
    return "%.${n}f".format(Locale.ENGLISH, this).toFloat()
}