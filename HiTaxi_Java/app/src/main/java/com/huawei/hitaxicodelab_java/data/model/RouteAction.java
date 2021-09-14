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

package com.huawei.hitaxicodelab_java.data.model;

import com.huawei.hitaxicodelab_java.R;

public enum RouteAction {
    TURN_SLIGHT_LEFT("turn-slight-left", R.drawable.ic_turn_slight_left),
    TURN_SHARP_LEFT("turn-sharp-left", R.drawable.ic_turn_slight_right),
    UTURN_LEFT("uturn-left", R.drawable.ic_u_turn),
    TURN_LEFT("turn-left", R.drawable.ic_turn_left),
    TURN_SLIGHT_RIGHT("turn-slight-right", R.drawable.ic_turn_slight_right),
    TURN_SHARP_RIGHT("turn-sharp-right", R.drawable.ic_turn_sharp_right),
    UTURN_RIGHT("uturn-right", R.drawable.ic_u_turn),
    TURN_RIGHT("turn-right", R.drawable.ic_turn_right),
    STRAIGHT("straight", R.drawable.ic_straight),
    RAMP_LEFT("ramp-left", R.drawable.ic_turn_ramp_left),
    RAMP_RIGHT("ramp-right", R.drawable.ic_turn_ramp_right),
    MERGE("merge", R.drawable.ic_merge),
    FORK_LEFT("fork-left", R.drawable.ic_fork_left),
    FORK_RIGHT("fork-right", R.drawable.ic_fork_right),
    ROUNDABOUT_LEFT("roundabout_left", R.drawable.ic_round_about_left),
    ROUNDABOUT_RIGHT("roundabout_right", R.drawable.ic_round_about_right),
    END("end", R.drawable.ic_flag);

    private String action;
    private int actionIconId;

    RouteAction(String action, int actionIconId) {
        this.action = action;
        this.actionIconId = actionIconId;
    }

    public final String getAction() {
        return this.action;
    }

    public final int getActionIconId() {
        return this.actionIconId;
    }

    public static int getIconByAction(String action) {
        for (RouteAction value : RouteAction.values()) {
            if (value.action.equals(action)) {
                return value.actionIconId;
            }
        }
        return -1;
    }


}
