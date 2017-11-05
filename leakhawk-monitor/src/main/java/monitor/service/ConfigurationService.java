package monitor.service;

import api.LeakHawkMain;
import org.springframework.stereotype.Service;
import util.LeakHawkConstant;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@Service
public class ConfigurationService {

    private LeakHawkMain leakHawk;

    ConfigurationService(){
        this.leakHawk= new LeakHawkMain();
    }

    public boolean startLeakHawk() {
        return leakHawk.startLeakHawk();
    }

    public boolean stopLeakHawk() {
        return leakHawk.stopLeakHawk();
    }

    public boolean addTwitterFilter() {
        return leakHawk.addTwitterFeed();
    }

    public boolean addPastebinFilter() {
        return leakHawk.addPastebinFeed();
    }

    public boolean saveConfig(String contentPath) {
        leakHawk.setResourceFolderPath(contentPath);
        return true;
    }
}
