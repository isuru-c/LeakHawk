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

import classifiers.ContentModel;
import classifiers.EvidenceModel;

import java.io.Serializable;

/**
 * Created by Isuru Chandima on 8/13/17.
 */
public class Post implements Serializable{

    private String postType;
    private String key;
    private String date;
    private String title;
    private String user;
    private String syntax;
    private String postText;

    private ContentModel contentModel = null;
    private EvidenceModel evidenceModel = null;

    private boolean evidenceClassifierPassed = false;
    private boolean contentClassifierPassed = false;
    private boolean evidenceFinderPassed = false;

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

    public void setEvidenceClassifierPassed() {
        this.evidenceClassifierPassed = true;
    }

    public void setContentClassifierPassed() {
        this.contentClassifierPassed = true;
    }

    public boolean isEvidenceClassifierPassed() {
        return evidenceClassifierPassed;
    }

    public boolean isContentClassifierPassed() {
        return contentClassifierPassed;
    }

    public boolean isEvidenceFinderPassed() {
        return evidenceFinderPassed;
    }

    public void setEvidenceFinderPassed(boolean evidenceFinderPassed) {
        this.evidenceFinderPassed = evidenceFinderPassed;
    }
}
