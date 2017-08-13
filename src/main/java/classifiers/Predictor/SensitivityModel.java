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
