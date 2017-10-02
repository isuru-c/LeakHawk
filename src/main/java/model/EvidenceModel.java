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
 * @author sewwandi
 */
public class EvidenceModel implements Serializable{

    private boolean evidenceFound;
    private boolean userExists;
    private boolean classifier1Passed;

    public EvidenceModel(){

        evidenceFound = false;
        userExists = false;
        classifier1Passed = false;
    }

    public boolean isEvidenceFound() {
        return evidenceFound;
    }

    public boolean isUserExists() {
        return userExists;
    }

    public boolean isClassifier1Passed() {
        return classifier1Passed;
    }

    public void setEvidenceFound(boolean evidenceFound) {
        this.evidenceFound = evidenceFound;
    }

    public void setUserExists(boolean userExists) {
        this.userExists = userExists;
    }

    public void setClassifier1Passed(boolean classifier1Passed) {
        this.classifier1Passed = classifier1Passed;
    }

}
