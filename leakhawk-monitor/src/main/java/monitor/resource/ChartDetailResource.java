package monitor.resource;

import java.util.List;

/**
 * @author Sugeesh Chandraweera
 */
public class ChartDetailResource {
    private List<String> titleList;
    private List<String> dataList;

    public ChartDetailResource() {
    }

    public ChartDetailResource(List<String> titleList, List<String> dataList) {
        this.titleList = titleList;
        this.dataList = dataList;
    }

    public List<String> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<String> titleList) {
        this.titleList = titleList;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }
}
