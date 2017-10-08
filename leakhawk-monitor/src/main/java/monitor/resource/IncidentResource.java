package monitor.resource;

import monitor.model.Incident;

/**
 * @author Sugeesh Chandraweera
 */
public class IncidentResource {
    private String postType;
    private String key;
    private String title;
    private String date;
    private String user;
    private String predictClass;
    private int level;
    private boolean content;
    private boolean evidence;

    public IncidentResource() {
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPredictClass() {
        return predictClass;
    }

    public void setPredictClass(String predictClass) {
        this.predictClass = predictClass;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public static IncidentResource getResource(Incident incident){
        IncidentResource incidentResource = new IncidentResource();
        incidentResource.setKey(incident.getPost_key());
        incidentResource.setContent(incident.isContent());
        incidentResource.setEvidence(incident.isEvidence());
        incidentResource.setDate(incident.getDate());
        incidentResource.setLevel(Integer.parseInt(incident.getSensitivityLevel()));
        incidentResource.setTitle(incident.getTitle());
        incidentResource.setPostType(incident.getType());
        incidentResource.setPredictClass(incident.getPredictClass());
        return incidentResource;
    }
}


