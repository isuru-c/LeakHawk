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

package classifiers.Predictor;

/**
 * @author Sugeesh Chandraweera
 */
public class SensitivityModel {
    /*
    * Low       -   1
    * High      -   2
    * Critical  -   3
    * */
    private int level;
    private boolean contentClassifier;
    private boolean evidenceClassifier;
    private String predictClass;

    public SensitivityModel() {
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isContentClassifier() {
        return contentClassifier;
    }

    public void setContentClassifier(boolean contentClassifier) {
        this.contentClassifier = contentClassifier;
    }

    public boolean isEvidenceClassifier() {
        return evidenceClassifier;
    }

    public void setEvidenceClassifier(boolean evidenceClassifier) {
        this.evidenceClassifier = evidenceClassifier;
    }

    public String getPredictClass() {
        return predictClass;
    }

    public void setPredictClass(String predictClass) {
        this.predictClass = predictClass;
    }
}
