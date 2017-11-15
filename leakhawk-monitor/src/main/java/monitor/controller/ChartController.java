package monitor.controller;

import monitor.resource.ChartDetailResource;
import monitor.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sugeesh Chandraweera
 */
@RestController
@RequestMapping("/chart")
public class ChartController {

    @Autowired
    private ChartService chartService;

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/count_prefilter_post",method = RequestMethod.GET)
    @ResponseBody
    public ChartDetailResource getChartOne(){
        return chartService.getPreFilterCount();
    }


    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/count_contextfilter_post",method = RequestMethod.GET)
    @ResponseBody
    public ChartDetailResource getChartTwo(){
        return chartService.getContextFilterCount();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/count_filter_post",method = RequestMethod.GET)
    @ResponseBody
    public ChartDetailResource getChartThree(){
        return chartService.getFilterCount();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/count_content_passed",method = RequestMethod.GET)
    @ResponseBody
    public ChartDetailResource getSecondChartOne(){
        return chartService.getContentPassedCount();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/count_evidence_passed",method = RequestMethod.GET)
    @ResponseBody
    public ChartDetailResource getSecondChartTwo(){
        return chartService.getEvidencePassedCount();
    }


    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/count_sensitivity_level",method = RequestMethod.GET)
    @ResponseBody
    public ChartDetailResource getThirdChartOne(){
        return chartService.getSensitivityLevelCount();
    }

    @CrossOrigin(origins = "http://localhost:8000")
    @RequestMapping(value = "/count_content_type",method = RequestMethod.GET)
    @ResponseBody
    public ChartDetailResource getThirdChartTwo(){
        return chartService.getContentTypeCount();
    }

}
