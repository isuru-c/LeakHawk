package exception;

/**
 * @author Sugeesh Chandraweera
 */
public class LeakHawkDatabaseException extends RuntimeException{

    public LeakHawkDatabaseException(String message){
        super(message);
    }

    public LeakHawkDatabaseException(String message, Exception exception){
        super(message,exception);
    }

}
