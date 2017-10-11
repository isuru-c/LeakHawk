package api;


/**
 * @author Sugeesh Chandraweera
 */
public class LeakHawkTopology {


    public boolean startLeakHawk(){
        LeakHawk.startLeakhawk();
        return true;
    }


    public boolean stopLeakHawk(){
        return true;
    }

    public boolean setModelFolerPath(String path){

        return true;
    }

    public boolean setContentFilterWordFile(String path){

        return true;
    }


    public static void main(String[] args) {
        new LeakHawkTopology().startLeakHawk();
    }
}
