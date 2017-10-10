package exception;

/**
 * @author Sugeesh Chandraweera
 */
public class LeakHawkClassifierLoadingException extends RuntimeException{

    public LeakHawkClassifierLoadingException(String message){
        super(message);
    }

    public LeakHawkClassifierLoadingException(String message, Exception exception){
        super(message,exception);
    }

}
