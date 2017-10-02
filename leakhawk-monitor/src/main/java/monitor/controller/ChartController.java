package monitor.controller;

import monitor.resource.ChartDetailResource;
import monitor.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sugeesh Chandraweera
 */
@RestController
@RequestMapping("/charts")
public class ChartController {

    @Autowired
    private ChartService chartService;

    @RequestMapping(value = "/one",method = RequestMethod.GET)
    @ResponseBody
    public ChartDetailResource getChartOne(){
        return chartService.getChartOneDetail();
    }


}
