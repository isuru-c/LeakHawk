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

    ConfigurationService(){
        this.leakHawk= new LeakHawkMain();
    }

    public boolean startLeakHawk() {
        return leakHawk.startLeakHawk();
    }

    public boolean stopLeakHawk() {
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
        resourcePath.setResourcePath(leakHawk.getResourceFolderPath());
        return resourcePath;
    }
}
