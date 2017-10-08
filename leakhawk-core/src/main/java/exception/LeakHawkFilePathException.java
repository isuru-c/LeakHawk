package exception;

/**
 * @author Sugeesh Chandraweera
 */
public class LeakHawkFilePathException extends RuntimeException{

    public LeakHawkFilePathException(String message) {
        super(message);
    }

    public LeakHawkFilePathException(String message,Exception e){
        super(message,e);
    }
}
