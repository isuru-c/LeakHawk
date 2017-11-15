package monitor.service;

import api.LeakHawkMain;
import monitor.resource.ResourcePath;
import org.springframework.stereotype.Service;
import sensor.PastebinSensor;
import sensor.TwitterSensor;

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
}
