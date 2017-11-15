package monitor.resource;

/**
 * REST API model class for custom response
 *
 * @author Sugeesh Chandraweera
 */
public class ResponseResource {

    private String response;

    public ResponseResource(String message, String... args) {
        try {
            this.response = processMsg(message, args);
        } catch (Exception ex) {
            this.response = message;
        }
    }

    private String processMsg(String message, String... args) throws Exception {
        //todo need to optimize this
        int index = 0;
        while (message.contains("{}")) {
            message = message.replaceFirst("\\{\\}", args[index++]);
        }
        return message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
