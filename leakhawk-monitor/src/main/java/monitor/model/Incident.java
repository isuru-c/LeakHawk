package monitor.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Sugeesh Chandraweera
 */
@Entity
public class Incident {
    @Id
    private String post_key;
    private String user;
    private String title;
    private String type;
    private String date;
    private int sensitivityLevel;
    private boolean content;
    private boolean evidence;
    private String predictClass;

    public Incident() {
    }

    public Incident(String post_key, String user, String title, String type, String date, int sensitivityLevel, boolean content, boolean evidence, String predictClass) {
        this.post_key = post_key;
        this.user = user;
        this.title = title;
        this.type = type;
        this.date = date;
        this.sensitivityLevel = sensitivityLevel;
        this.content = content;
        this.evidence = evidence;
        this.predictClass = predictClass;
    }

    public String getPost_key() {
        return post_key;
    }

    public void setPost_key(String post_key) {
        this.post_key = post_key;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSensitivityLevel() {
        return sensitivityLevel;
    }

    public void setSensitivityLevel(int sensitivityLevel) {
        this.sensitivityLevel = sensitivityLevel;
    }

    public boolean isContent() {
        return content;
    }

    public void setContent(boolean content) {
        this.content = content;
    }

    public boolean isEvidence() {
        return evidence;
    }

    public void setEvidence(boolean evidence) {
        this.evidence = evidence;
    }

    public String getPredictClass() {
        return predictClass;
    }

    public void setPredictClass(String predictClass) {
        this.predictClass = predictClass;
    }
}
