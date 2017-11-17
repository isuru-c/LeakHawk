package monitor.service;

import api.LeakHawkMain;
import monitor.resource.ResourcePath;
import org.springframework.stereotype.Service;
import sensor.PastebinSensor;
import sensor.TwitterSensor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@Service
public class ConfigurationService {

    private LeakHawkMain leakHawk;

    private TwitterSensor twitterSensor;
    private PastebinSensor pastebinSensor;
    private boolean leakhawkStarted = false;

    ConfigurationService(){
        this.leakHawk= new LeakHawkMain();
    }

    public boolean startLeakHawk() {
        this.leakhawkStarted = true;
        return leakHawk.startLeakHawk();
    }

    public boolean stopLeakHawk() {
        this.leakhawkStarted = false;
        return leakHawk.stopLeakHawk();
    }

    public boolean addTwitterSensor() {
        this.twitterSensor = leakHawk.addTwitterFeed();
        return true;
    }

    public boolean stopTwitterSensor() {
        return this.twitterSensor.stopSensor();
    }

    public boolean addPastebinSensor() {
        this.pastebinSensor = leakHawk.addPastebinFeed();
        return true;
    }

    public boolean stopPastebinSensor() {
        return this.pastebinSensor.stopSensor();
    }


    public boolean saveConfig(String contentPath) {
        return leakHawk.setResourceFolderPath(contentPath);
    }

    public ResourcePath getConfig() {
        ResourcePath resourcePath = new ResourcePath();
        if(twitterSensor==null){
            resourcePath.setTwitterSensor(false);
        }else{
            resourcePath.setTwitterSensor(this.twitterSensor.getSensorState());
        }
        if(pastebinSensor == null){
            resourcePath.setPastebinSensor(false);
        }else {
            resourcePath.setPastebinSensor(this.pastebinSensor.getSensorState());
        }
        resourcePath.setLeakhawk(this.leakhawkStarted);
        resourcePath.setResourcePath(leakHawk.getResourceFolderPath());
        return resourcePath;
    }

    public boolean checkFilesExist(String contentPath) {
        List<String> fileNames = new ArrayList<>();
        fileNames.add("context.properties");
        fileNames.add("context_wordList.txt");
        fileNames.add("PreFilter_twitter.txt");
        fileNames.add("twitter.properties");

        boolean check = true;

        for(String file : fileNames){
            Path path = Paths.get(contentPath+"/"+file);
            if(!Files.exists(path)){
                check = false;
                break;
            }
        }
        return check;
    }
}
