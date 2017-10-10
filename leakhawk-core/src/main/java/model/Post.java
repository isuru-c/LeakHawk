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
import java.util.ArrayList;

/**
 * @author Isuru Chandima
 */
public class Post implements Serializable {

    private String postType;
    private String key;
    private String date;
    private String title;
    private String user;
    private String syntax;
    private String postText;
    private String language;

    private ContentModel contentModel = null;
    private EvidenceModel evidenceModel = null;

    // Next output stream the post needs to be emitted
    private String nextOutputStream;

    // List of urls inside the post
    private ArrayList<String> urlList = new ArrayList<>();

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

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setContentModel(ContentModel contentModel) {
        this.contentModel = contentModel;
    }

    public void setEvidenceModel(EvidenceModel evidenceModel) {
        this.evidenceModel = evidenceModel;
    }

    public void setNextOutputStream(String nextOutputStream) {
        this.nextOutputStream = nextOutputStream;
    }

    public void addUrl(String url) {
        this.urlList.add(url);
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

    public String getLanguage() {
        return language;
    }

    public ArrayList<String> getUrlList() {
        return urlList;
    }

    public ContentModel getContentModel() {
        return contentModel;
    }

    public EvidenceModel getEvidenceModel() {
        return evidenceModel;
    }

    public String getNextOutputStream() {
        return nextOutputStream;
    }
}
