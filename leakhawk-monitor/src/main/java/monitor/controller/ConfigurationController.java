package monitor.controller;

import api.LeakHawk;
import api.LeakHawkTopology;
import monitor.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Sugeesh Chandraweera
 */
@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;



    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/start_leakhawk",method = RequestMethod.GET)
    @ResponseBody
    public boolean startLeakHawk(){
        return configurationService.startLeakHawk();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/stop_leakhawk",method = RequestMethod.GET)
    @ResponseBody
    public boolean stopLeakHawk(){
        return configurationService.stopLeakHawk();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/add_twitter",method = RequestMethod.GET)
    @ResponseBody
    public boolean addTwitterFeed(){
        return configurationService.addTwitterFilter();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/add_pastebin",method = RequestMethod.GET)
    @ResponseBody
    public boolean addPastebinFeed(){
        return configurationService.addPastebinFilter();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/save_config",method = RequestMethod.GET)
    @ResponseBody
    public boolean saveConfig(String contentPath){
        return configurationService.saveConfig(contentPath);
    }



}
