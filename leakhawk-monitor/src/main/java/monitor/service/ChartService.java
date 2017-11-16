package monitor.service;

import api.LeakHawkMain;
import monitor.model.ChartDetail;
import monitor.model.Incident;
import monitor.repository.ChartDetailRepository;
import monitor.repository.IncidentRepository;
import monitor.resource.ChartDetailResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
@Service
public class ChartService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private ChartDetailRepository chartDetailRepository;

    public ChartDetailResource getChartOneDetail(){
        ChartDetailResource chartDetailResource = new ChartDetailResource();
        List<String> titleList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();
        int class1 = 0;
        int class2 = 0;
        int class3 = 0;
        Iterable<Incident> allIncident = incidentRepository.findAll();

        for(Incident incident : allIncident){
            if(incident.getSensitivityLevel()==3){
                class3++;
            }else if(incident.getSensitivityLevel()==2){
                class2++;
            }else if(incident.getSensitivityLevel()==1) {
                class1++;
            }
        }
        titleList.add("Critical");
        titleList.add("High");
        titleList.add("Low");

        dataList.add(String.valueOf(class3));
        dataList.add(String.valueOf(class2));
        dataList.add(String.valueOf(class1));

        chartDetailResource.setDataList(dataList);
        chartDetailResource.setTitleList(titleList);

        LeakHawkMain leakHawkTopology = new LeakHawkMain();
        leakHawkTopology.startLeakHawk();
        return chartDetailResource;

    }


    public ChartDetailResource getPreFilterCount(){
        ChartDetailResource chartDetailResource = new ChartDetailResource();
        List<String> titleList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();


        Iterable<ChartDetail> all = chartDetailRepository.findAll();
        int totalPosts = 0;
        int preFilterPosts = 0;
        for(ChartDetail chartDetail: all){
            if(chartDetail.getFieldName().equals("totalPostCount")){
                titleList.add("Pre-Filter Removed Posts");
                dataList.add(String.valueOf(chartDetail.getValue()));
                totalPosts = chartDetail.getValue();
            }else if(chartDetail.getFieldName().equals("preFilterPassedCount")){
                titleList.add("PreFilter Passed Posts");
                dataList.add(String.valueOf(chartDetail.getValue()));
                preFilterPosts = chartDetail.getValue();
            }
        }
        dataList.set(0,String.valueOf(totalPosts-preFilterPosts));
        chartDetailResource.setTitleList(titleList);
        chartDetailResource.setDataList(dataList);

        return chartDetailResource;
    }

    public ChartDetailResource getContextFilterCount(){
        ChartDetailResource chartDetailResource = new ChartDetailResource();
        List<String> titleList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();

        Iterable<ChartDetail> all = chartDetailRepository.findAll();
        int preFilterPosts = 0;
        int contextFilterPosts = 0;
        for(ChartDetail chartDetail: all){
            if(chartDetail.getFieldName().equals("preFilterPassedCount")){
                titleList.add("Context-Filtered Removed Posts");
                dataList.add(String.valueOf(chartDetail.getValue()));
                preFilterPosts = chartDetail.getValue();
            }else if(chartDetail.getFieldName().equals("contextFilterPassedCount")){
                titleList.add("Context-Filter Passed Posts");
                dataList.add(String.valueOf(chartDetail.getValue()));
                contextFilterPosts = chartDetail.getValue();
            }
        }
        dataList.set(0,String.valueOf(preFilterPosts-contextFilterPosts));
        chartDetailResource.setTitleList(titleList);
        chartDetailResource.setDataList(dataList);

        return chartDetailResource;
    }

    public ChartDetailResource getFilterCount(){
        ChartDetailResource chartDetailResource = new ChartDetailResource();
        List<String> titleList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();


        Iterable<ChartDetail> all = chartDetailRepository.findAll();
        int totalPosts = 0;
        int filterPosts = 0;
        for(ChartDetail chartDetail: all){
            if(chartDetail.getFieldName().equals("totalPostCount")){
                titleList.add("Removed Posts");
                dataList.add(String.valueOf(chartDetail.getValue()));
                totalPosts = chartDetail.getValue();
            }else if(chartDetail.getFieldName().equals("contextFilterPassedCount")){
                titleList.add("Passed Posts");
                dataList.add(String.valueOf(chartDetail.getValue()));
                filterPosts = chartDetail.getValue();
            }
        }
        dataList.set(0,String.valueOf(totalPosts-filterPosts));
        chartDetailResource.setTitleList(titleList);
        chartDetailResource.setDataList(dataList);

        return chartDetailResource;
    }

    public ChartDetailResource getContentPassedCount(){
        ChartDetailResource chartDetailResource = new ChartDetailResource();
        List<String> titleList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();


        Iterable<Incident> all = incidentRepository.findAll();
        int contentPassed = 0;
        int totalPosts = 0;
        for(Incident incident: all){
            if(incident.isContent()){
                contentPassed++;
            }
            totalPosts++;
        }

        titleList.add("Conntent Classfied");
        titleList.add("Conntent Not Classfied");

        dataList.add(String.valueOf(contentPassed));
        dataList.add(String.valueOf(totalPosts-contentPassed));
        chartDetailResource.setTitleList(titleList);
        chartDetailResource.setDataList(dataList);

        return chartDetailResource;
    }

    public ChartDetailResource getEvidencePassedCount(){
        ChartDetailResource chartDetailResource = new ChartDetailResource();
        List<String> titleList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();


        Iterable<Incident> all = incidentRepository.findAll();
        int evidencePassed = 0;
        int totalPosts = 0;
        for(Incident incident: all){
            if(incident.isEvidence()){
                evidencePassed++;
            }
            totalPosts++;
        }

        titleList.add("Evidence Classfied");
        titleList.add("Evidence Not Classfied");

        dataList.add(String.valueOf(evidencePassed));
        dataList.add(String.valueOf(totalPosts-evidencePassed));
        chartDetailResource.setTitleList(titleList);
        chartDetailResource.setDataList(dataList);

        return chartDetailResource;
    }

    public ChartDetailResource getSensitivityLevelCount(){
        ChartDetailResource chartDetailResource = new ChartDetailResource();
        List<String> titleList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();

        int class1 = 0;
        int class2 = 0;
        int class3 = 0;
        Iterable<Incident> allIncident = incidentRepository.findAll();

        for(Incident incident : allIncident){
            if(incident.getSensitivityLevel()==3){
                class3++;
            }else if(incident.getSensitivityLevel()==2){
                class2++;
            }else if(incident.getSensitivityLevel()==1) {
                class1++;
            }
        }
        titleList.add("Critical");
        titleList.add("High");
        titleList.add("Low");

        dataList.add(String.valueOf(class3));
        dataList.add(String.valueOf(class2));
        dataList.add(String.valueOf(class1));

        chartDetailResource.setDataList(dataList);
        chartDetailResource.setTitleList(titleList);
        return chartDetailResource;
    }


    public ChartDetailResource getContentTypeCount(){
        ChartDetailResource chartDetailResource = new ChartDetailResource();
        List<String> titleList = new ArrayList<>();
        List<String> dataList = new ArrayList<>();

        int cc = 0;
        int cf = 0;
        int da = 0;
        int db = 0;
        int ec = 0;
        int eo = 0;
        int pk = 0;
        int uc = 0;
        int wd = 0;

        Iterable<Incident> allIncident = incidentRepository.findAll();

        for(Incident incident : allIncident){
            if(!incident.getPredictClass().isEmpty()) {
                if (incident.getPredictClass().contains("Credit Card")) {
                    cc++;
                }
                if (incident.getPredictClass().contains("Configuration files")) {
                    cf++;
                }
                if (incident.getPredictClass().contains("DNS Attack")) {
                    da++;
                }
                if (incident.getPredictClass().contains("Database")) {
                    db++;
                }
                if (incident.getPredictClass().contains("Email conversation")) {
                    ec++;
                }
                if (incident.getPredictClass().contains("Email only")) {
                    eo++;
                }
                if (incident.getPredictClass().contains("Private keys")) {
                    pk++;
                }
                if (incident.getPredictClass().contains("User Credentials")) {
                    uc++;
                }
                if (incident.getPredictClass().contains("Website Defacement")) {
                    wd++;
                }
            }
        }
        titleList.add("Credit Card");
        titleList.add("Configuration files");
        titleList.add("DNS Attack");
        titleList.add("Database");
        titleList.add("Email conversation");
        titleList.add("Email only");
        titleList.add("Private keys");
        titleList.add("User Credentials");
        titleList.add("Website Defacement");

        dataList.add(String.valueOf(cc));
        dataList.add(String.valueOf(cf));
        dataList.add(String.valueOf(da));
        dataList.add(String.valueOf(db));
        dataList.add(String.valueOf(ec));
        dataList.add(String.valueOf(eo));
        dataList.add(String.valueOf(pk));
        dataList.add(String.valueOf(uc));
        dataList.add(String.valueOf(wd));

        chartDetailResource.setDataList(dataList);
        chartDetailResource.setTitleList(titleList);
        return chartDetailResource;
    }





}
