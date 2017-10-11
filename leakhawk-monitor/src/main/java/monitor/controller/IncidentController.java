package monitor.controller;

import api.LeakHawk;
import api.LeakHawkTopology;
import monitor.resource.ChartDetailResource;
import monitor.resource.HeaderDataResource;
import monitor.resource.IncidentResource;
import monitor.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Sugeesh Chandraweera
 */
@RestController
@RequestMapping("/incident")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;


    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/get_all_incidents",method = RequestMethod.GET)
    @ResponseBody
    public List getAllIncidents(){
        return incidentService.getAllIncidents();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/get_incident",method = RequestMethod.GET)
    @ResponseBody
    public IncidentResource getIncident(long id){
        return incidentService.getIncident(id);
    }


    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/get_header_data",method = RequestMethod.GET)
    @ResponseBody
    public HeaderDataResource getHeaderData(){
        return incidentService.getHeaderData();
    }


    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/try_start_leakhawk",method = RequestMethod.GET)
    @ResponseBody
    public List startLeakHawk(){
        System.out.println("Come to here");
        boolean b = new LeakHawkTopology().startLeakHawk();
        System.out.println("Come to here 2"+b);

        return null;
    }

}
