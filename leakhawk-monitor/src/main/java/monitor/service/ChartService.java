package monitor.service;

import monitor.resource.ChartDetailResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sugeesh Chandraweera
 */
@Service
public class ChartService {


    public ChartDetailResource getChartOneDetail(){
        ChartDetailResource chartDetailResource = new ChartDetailResource();
        List<String> titleList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();
        titleList.add("Title1");
        titleList.add("Title2");
        titleList.add("Title3");
        dataList.add("Data1");
        dataList.add("Data2");
        dataList.add("Data3");
        chartDetailResource.setTitleList(titleList);
        chartDetailResource.setDataList(dataList);
        return chartDetailResource;

    }


}
