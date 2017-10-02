package core.api;

import core.leakhawk.LeakHawk;

/**
 * @author Sugeesh Chandraweera
 */
public class LeakHawkTopology {


    public boolean startLeakHawk(){
        LeakHawk leakHawk = new LeakHawk();
        return leakHawk.startLeakHawk();
    }

    public static void main(String[] args) {
        new LeakHawkTopology().startLeakHawk();
    }
}
