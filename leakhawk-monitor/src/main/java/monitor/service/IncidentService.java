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
        List<Incident> allOrderBySensitivityLevelDesc = (List<Incident>) incidentRepository.findAll();
        List<IncidentResource> incidentResourceList = new ArrayList<>();

        for (Incident incident: allOrderBySensitivityLevelDesc){
            incidentResourceList.add(IncidentResource.getResource(incident));
        }

        return incidentResourceList;
    }

    public IncidentResource getIncident(long id) {
        Incident incident = incidentRepository.findOne(id);
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
            if(incident.getSensitivityLevel().equals("3")){
                criticalIncidents++;
            }
        }
        headerDataResource.setSensitivePosts(totalSensitiveIncidents);
        headerDataResource.setCriticalPosts(criticalIncidents);

        return headerDataResource;

    }
}
