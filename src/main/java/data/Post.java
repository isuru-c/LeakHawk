package data;

import classifiers.ContentModel;
import classifiers.EvidenceModel;

import java.io.Serializable;

/**
 * Created by Isuru Chandima on 8/13/17.
 */
public class Post implements Serializable{

    String postType;
    String key;
    String date;
    String title;
    String user;
    String syntax;
    String postText;

    ContentModel contentModel;
    EvidenceModel evidenceModel;

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setContentModel(ContentModel contentModel) {
        this.contentModel = contentModel;
    }

    public void setEvidenceModel(EvidenceModel evidenceModel) {
        this.evidenceModel = evidenceModel;
    }

    public String getPostType() {

        return postType;
    }

    public String getKey() {
        return key;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getPostText() {
        return postText;
    }

    public ContentModel getContentModel() {

        return contentModel;
    }

    public EvidenceModel getEvidenceModel() {
        return evidenceModel;
    }

}
