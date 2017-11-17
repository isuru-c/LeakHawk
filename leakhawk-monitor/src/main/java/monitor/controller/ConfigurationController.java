package monitor.controller;

import monitor.resource.ResourcePath;
import monitor.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import util.LeakHawkConstant;

import javax.ws.rs.core.Response;

/**
 * @author Sugeesh Chandraweera
 */
@RestController
@RequestMapping("/configuration")
public class ConfigurationController extends AbstractController{

    @Autowired
    private ConfigurationService configurationService;

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/start_leakhawk",method = RequestMethod.GET)
    @ResponseBody
    public Response startLeakHawk(){
        if(configurationService.checkFilesExist(LeakHawkConstant.RESOURCE_FOLDER_FILE_PATH)){
            return sendSuccessResponse(configurationService.startLeakHawk());
        }else {
            return handleServiceException(null,"Please insert all needed files into resource folder.");
        }

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
        return configurationService.addTwitterSensor();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/stop_twitter",method = RequestMethod.GET)
    @ResponseBody
    public boolean stopTwitterFeed(){
        return configurationService.stopTwitterSensor();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/add_pastebin",method = RequestMethod.GET)
    @ResponseBody
    public boolean addPastebinFeed(){
        return configurationService.addPastebinSensor();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/stop_pastebin",method = RequestMethod.GET)
    @ResponseBody
    public boolean stopPastebinFeed(){
        return configurationService.stopPastebinSensor();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/save_config",method = RequestMethod.POST)
    @ResponseBody
    public Response saveConfig(@RequestBody ResourcePath contentPath){
        if(configurationService.checkFilesExist(contentPath.getResourcePath())) {
            return sendSuccessResponse(configurationService.saveConfig(contentPath.getResourcePath()));
        }else {
            return handleServiceException(null,"Please insert all needed files");
        }
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/get_config",method = RequestMethod.GET)
    @ResponseBody
    public ResourcePath getConfig(){
        return configurationService.getConfig();
    }

    /*try {
        return sendSuccessResponse(advertisementService.saveItem(itemResource));
    } catch (Exception e) {
        e.printStackTrace();
        return handleServiceException(e);
    }*/

}
