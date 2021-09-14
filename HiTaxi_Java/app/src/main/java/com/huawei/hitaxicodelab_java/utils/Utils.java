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

package com.huawei.hitaxicodelab_java.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.huawei.hitaxicodelab_java.data.model.Coordinate;
import com.huawei.hms.maps.model.LatLng;

public class Utils {

    public static Integer toDp(Integer value) {
        Float calculatedValue = value * Resources.getSystem().getDisplayMetrics().density + 0.5f;
        return calculatedValue.intValue();
    }

    public static Coordinate toCoordinate(LatLng latLng) {
        if (latLng != null) {
            return new Coordinate(latLng.latitude, latLng.longitude);
        } else {
            return null;
        }
    }

    public static Long secondToMillisecond(Long value) {
        return value * 1000;
    }

    public static LatLng toLatLng(Coordinate coordinate) {
        return new LatLng(coordinate.lat, coordinate.lng);
    }

    public static LatLng toLatLng(com.huawei.hms.site.api.model.Coordinate coordinate) {
        return new LatLng(coordinate.lat, coordinate.lng);
    }

    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
