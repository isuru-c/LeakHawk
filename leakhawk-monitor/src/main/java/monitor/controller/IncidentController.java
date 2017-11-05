package monitor.controller;

import api.LeakHawkMain;
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
    @RequestMapping(value = "/get_slevel_incidents/{level}",method = RequestMethod.GET)
    @ResponseBody
    public List getIncidentForLevel(@PathVariable("level") int level){
        return incidentService.getIncidentsForLevel(level);
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/get_incident/{id}",method = RequestMethod.GET)
    @ResponseBody
    public IncidentResource getIncident(@PathVariable("id") String id){
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
        boolean b = new LeakHawkMain().startLeakHawk();
        System.out.println("Come to here 2"+b);

        return null;
    }

}
