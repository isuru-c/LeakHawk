package exception;

/**
 * @author Sugeesh Chandraweera
 */
public class LeakHawkDataStreamException extends RuntimeException{

    public LeakHawkDataStreamException(String message){
        super(message);
    }

    public LeakHawkDataStreamException(String message, Exception e){
        super(message, e);
    }
}
