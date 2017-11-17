package monitor.service;

import monitor.model.ChartDetail;
import monitor.model.Incident;
import monitor.repository.ChartDetailRepository;
import monitor.repository.IncidentRepository;
import monitor.resource.ChartDetailResource;
import monitor.resource.HeaderDataResource;
import monitor.resource.IncidentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private ChartDetailRepository chartDetailRepository;


    public List getAllIncidents() {
        List<Incident> allOrderBySensitivityLevelDesc = incidentRepository.findAllByOrderBySensitivityLevelDescIdDesc();
        List<IncidentResource> incidentResourceList = new ArrayList<>();

        for (Incident incident: allOrderBySensitivityLevelDesc){
            incidentResourceList.add(IncidentResource.getResource(incident));
        }

        return incidentResourceList;
    }

    public List getIncidentsForLevel(int level) {
        List<Incident> allOrderBySensitivityLevelDesc = incidentRepository.findBySensitivityLevelOrderByIdDesc(level);
        List<IncidentResource> incidentResourceList = new ArrayList<>();
        for (Incident incident: allOrderBySensitivityLevelDesc){
            incidentResourceList.add(IncidentResource.getResource(incident));
        }
        return incidentResourceList;
    }

    public List getIncidentsOrderByDate() {
        List<Incident> allOrderByDateDesc = incidentRepository.findAllByOrderByDateDesc();
        List<IncidentResource> incidentResourceList = new ArrayList<>();
        for (Incident incident: allOrderByDateDesc){
            incidentResourceList.add(IncidentResource.getResource(incident));
        }
        return incidentResourceList;
    }

    public IncidentResource getIncident(String id) {
        Incident incident = incidentRepository.findByPostKey(id);
        IncidentResource resource = IncidentResource.getResource(incident);
        return resource;
    }

    public HeaderDataResource getHeaderData() {
        HeaderDataResource headerDataResource = new HeaderDataResource();

        Iterable<ChartDetail> all = chartDetailRepository.findAll();
        int totalPosts = 0;
        for(ChartDetail chartDetail: all){
            if(chartDetail.getFieldName().equals("totalPostCount")){
                totalPosts = chartDetail.getValue();
            }
        }
        headerDataResource.setTotalPosts(totalPosts);


        Iterable<Incident> allIncident = incidentRepository.findAll();
        int totalSensitiveIncidents = 0;
        int criticalIncidents = 0;
        for(Incident incident: allIncident){
            totalSensitiveIncidents++;
            if(incident.getSensitivityLevel()==3){
                criticalIncidents++;
            }
        }
        headerDataResource.setSensitivePosts(totalSensitiveIncidents);
        headerDataResource.setCriticalPosts(criticalIncidents);

        return headerDataResource;

    }
}
