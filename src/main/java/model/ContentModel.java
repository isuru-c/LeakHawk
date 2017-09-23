/*
 * Copyright 2017 SWIS
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
 * @author Sugeesh Chandraweera
 */
public class ContentModel implements Serializable{

    private boolean contentFound;
    private boolean passedCC;
    private boolean passedCF;
    private boolean passedDA;
    private boolean passedDB;
    private boolean passedEC;
    private boolean passedEO;
    private boolean passedPK;
    private boolean passedUC;
    private boolean passedWD;

    public boolean isContentFound(){
        if(passedCC || passedCF || passedDA || passedDB || passedEC || passedEO || passedPK){
            contentFound = true;
        }
        else {
            contentFound = false;
        }
        return contentFound;
    }

    public boolean isPassedCC() {
        return passedCC;
    }

    public void setPassedCC(boolean passedCC) {
        this.passedCC = passedCC;
    }

    public boolean isPassedCF() {
        return passedCF;
    }

    public void setPassedCF(boolean passedCF) {
        this.passedCF = passedCF;
    }

    public boolean isPassedDA() {
        return passedDA;
    }

    public void setPassedDA(boolean passedDA) {
        this.passedDA = passedDA;
    }

    public boolean isPassedDB() {
        return passedDB;
    }

    public void setPassedDB(boolean passedDB) {
        this.passedDB = passedDB;
    }

    public boolean isPassedEC() {
        return passedEC;
    }

    public void setPassedEC(boolean passedEC) {
        this.passedEC = passedEC;
    }

    public boolean isPassedEO() {
        return passedEO;
    }

    public void setPassedEO(boolean passedEO) {
        this.passedEO = passedEO;
    }

    public boolean isPassedPK() {
        return passedPK;
    }

    public void setPassedPK(boolean passedPK) {
        this.passedPK = passedPK;
    }

    public boolean isPassedUC() {
        return passedUC;
    }

    public void setPassedUC(boolean passedUC) {
        this.passedUC = passedUC;
    }

    public boolean isPassedWD() {
        return passedWD;
    }

    public void setPassedWD(boolean passedWD) {
        this.passedWD = passedWD;
    }
}