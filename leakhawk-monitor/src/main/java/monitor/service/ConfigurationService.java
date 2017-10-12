package monitor.service;

import api.LeakHawk;
import api.LeakHawkTopology;
import monitor.model.ChartDetail;
import monitor.model.Incident;
import monitor.repository.ChartDetailRepository;
import monitor.repository.IncidentRepository;
import monitor.resource.HeaderDataResource;
import monitor.resource.IncidentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.LeakHawkParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@Service
public class ConfigurationService {

    private LeakHawkTopology leakHawkTopology;

    ConfigurationService(){
        this.leakHawkTopology = new LeakHawkTopology();
    }

    public boolean startLeakHawk() {
        return leakHawkTopology.startLeakHawk();
    }

    public boolean stopLeakHawk() {
        return leakHawkTopology.stopLeakHawk();
    }

    public boolean addTwitterFilter() {
        return leakHawkTopology.addTwitterFeed();
    }

    public boolean addPastebinFilter() {
        return leakHawkTopology.addPastebinFeed();
    }

    public boolean saveConfig(String contentPath) {
        LeakHawkParameters.CONTEXT_FILTER_FILE_PATH = contentPath;
        return true;
    }
}
