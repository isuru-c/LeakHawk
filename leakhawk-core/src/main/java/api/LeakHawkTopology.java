package api;


/**
 * @author Sugeesh Chandraweera
 */
public class LeakHawkTopology {


    public static boolean startLeakHawk(){
        LeakHawk.startLeakhawk();
        return true;
    }


    public static boolean stopLeakHawk(){
        LeakHawk.stopTopology();
        return true;
    }

    public boolean setModelFolerPath(String path){

        return true;
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
