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

package classifiers;

import java.io.Serializable;

/**
 * Created by Isuru Chandima on 8/13/17.
 */
public class EvidenceModel implements Serializable{

    private boolean evidenceFound;
    private boolean userExists;
    private boolean classifier1Passed;
    private boolean classifier2Passed;
    private boolean classifier3Passed;
    private boolean classifier4Passed;
    private boolean classifier5Passed;
    private boolean classifier6Passed;
    private boolean classifier7Passed;
    private boolean classifier8Passed;

    public EvidenceModel(){

        evidenceFound = false;
        userExists = false;
        classifier1Passed = false;
        classifier2Passed = false;
        classifier3Passed = false;
        classifier4Passed = false;
        classifier5Passed = false;
        classifier6Passed = false;
        classifier7Passed = false;
        classifier8Passed = false;
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

    public boolean isClassifier2Passed() {
        return classifier2Passed;
    }

    public boolean isClassifier3Passed() {
        return classifier3Passed;
    }

    public boolean isClassifier4Passed() {
        return classifier4Passed;
    }

    public boolean isClassifier5Passed() {
        return classifier5Passed;
    }

    public boolean isClassifier6Passed() {
        return classifier6Passed;
    }

    public boolean isClassifier7Passed() {
        return classifier7Passed;
    }

    public boolean isClassifier8Passed() {
        return classifier8Passed;
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

    public void setClassifier2Passed(boolean classifier2Passed) {
        this.classifier2Passed = classifier2Passed;
    }

    public void setClassifier3Passed(boolean classifier3Passed) {
        this.classifier3Passed = classifier3Passed;
    }

    public void setClassifier4Passed(boolean classifier4Passed) {
        this.classifier4Passed = classifier4Passed;
    }

    public void setClassifier5Passed(boolean classifier5Passed) {
        this.classifier5Passed = classifier5Passed;
    }

    public void setClassifier6Passed(boolean classifier6Passed) {
        this.classifier6Passed = classifier6Passed;
    }

    public void setClassifier7Passed(boolean classifier7Passed) {
        this.classifier7Passed = classifier7Passed;
    }

    public void setClassifier8Passed(boolean classifier8Passed) {
        this.classifier8Passed = classifier8Passed;
    }

}
