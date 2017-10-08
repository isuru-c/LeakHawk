package api;

/**
 * @author Sugeesh Chandraweera
 */
public class LeakHawkTopology {


    public boolean startLeakHawk(){
        LeakHawk leakHawk = new LeakHawk();
//        return leakHawk.startLeakHawk();
        return true;
    }

    public static void main(String[] args) {
        new LeakHawkTopology().startLeakHawk();
    }
}
