package monitor.controller;

import monitor.model.ResourcePath;
import monitor.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

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
    @RequestMapping(value = "/save_config",method = RequestMethod.POST)
    @ResponseBody
    public boolean saveConfig(@RequestBody ResourcePath contentPath){
        return configurationService.saveConfig(contentPath.getResourcePath());
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/get_config",method = RequestMethod.GET)
    @ResponseBody
    public ResourcePath getConfig(){
        return configurationService.getConfig();
    }



}
