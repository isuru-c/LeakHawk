package monitor.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sugeesh Chandraweera
 */
@Entity
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String postKey;
    private String user;
    private String title;
    private String type;
    private String date;

    @Column(columnDefinition = "boolean default false")
    private boolean link;
    private int sensitivityLevel;
    private boolean content;
    private boolean evidence;
    private String predictClass;

    public Incident() {
    }

    public Incident(String postKey, String user, String title, String type, String date, int sensitivityLevel, boolean content, boolean evidence, String predictClass) {
        this.postKey = postKey;
        this.user = user;
        this.title = title;
        this.type = type;
        this.date = date;
        this.sensitivityLevel = sensitivityLevel;
        this.content = content;
        this.evidence = evidence;
        this.predictClass = predictClass;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLink() {
        return link;
    }

    public void setLink(boolean link) {
        this.link = link;
    }
}
