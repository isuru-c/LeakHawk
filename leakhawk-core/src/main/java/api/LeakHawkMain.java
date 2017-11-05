package api;


import util.LeakHawkConstant;

/**
 * @author Sugeesh Chandraweera
 */
public class LeakHawkMain {


    public static boolean startLeakHawk(){
        LeakHawk.startLeakhawk();
        return true;
    }


    public static boolean stopLeakHawk(){
        LeakHawk.stopTopology();
        return true;
    }

    public boolean setResourceFolderPath(String path){
        LeakHawkConstant.RESOURCE_FOLDER_FILE_PATH = path;
        return true;
    }

    public String getResourceFolderPath(){
        return LeakHawkConstant.RESOURCE_FOLDER_FILE_PATH;
    }

    public boolean setContentFilterWordFile(String path){
        return true;
    }

    public static boolean addTwitterFeed(){
        return LeakHawk.startTwitterSensor();
    }

    public static boolean addPastebinFeed(){
        return LeakHawk.startPastebinSensor();
    }


}
