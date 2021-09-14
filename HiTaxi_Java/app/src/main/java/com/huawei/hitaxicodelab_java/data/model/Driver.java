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

public class Driver {
    int id;
    String name;
    String surname;
    String picture;
    Integer age;
    int gender;
    float rate;
    int voteCount;
    int carType;
    String carPlate;
    float carRate;
    int carVoteCount;
    String carModel;

    public Driver(int id, String name, String surname, String picture, Integer age, int gender, float rate, int voteCount, int carType, String carPlate, float carRate, int carVoteCount, String carModel) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.picture = picture;
        this.age = age;
        this.gender = gender;
        this.rate = rate;
        this.voteCount = voteCount;
        this.carType = carType;
        this.carPlate = carPlate;
        this.carRate = carRate;
        this.carVoteCount = carVoteCount;
        this.carModel = carModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public int getCarType() {
        return carType;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public String getCarModel() {
        return carModel;
    }

}
