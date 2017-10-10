/*
 *    Copyright 2017 SWIS
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package model;

import java.io.Serializable;

/**
 * This object is created to store the statics of a single bolt within a
 * defined time period and send it to the StaticsCounter bolt
 *
 * @author Isuru Chandima
 */
public class Statics implements Serializable {

    private String boltType;
    private int inCount;
    private int outCount;

    public Statics(String boltType, int inCount, int outCount) {
        this.boltType = boltType;
        this.inCount = inCount;
        this.outCount = outCount;
    }

    public String getBoltType() {
        return boltType;
    }

    public int getInCount() {
        return inCount;
    }

    public int getOutCount() {
        return outCount;
    }
}
