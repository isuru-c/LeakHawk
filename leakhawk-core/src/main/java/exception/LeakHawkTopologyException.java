package exception;

/**
 * @author Sugeesh Chandraweera
 */
public class LeakHawkTopologyException extends RuntimeException{

    public LeakHawkTopologyException(String message){
        super(message);
    }

    public LeakHawkTopologyException(String message, Exception e){
        super(message, e);
    }

}
