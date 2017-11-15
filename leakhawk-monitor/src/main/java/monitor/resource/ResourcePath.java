package monitor.resource;

/**
 * @author Sugeesh Chandraweera
 */
public class ResourcePath {
    private String resourcePath;

    private boolean twitterSensor;

    private boolean pastebinSensor;

    private boolean leakhawk;

    public ResourcePath() {
    }

    public ResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public boolean isTwitterSensor() {
        return twitterSensor;
    }

    public void setTwitterSensor(boolean twitterSensor) {
        this.twitterSensor = twitterSensor;
    }

    public boolean isPastebinSensor() {
        return pastebinSensor;
    }

    public void setPastebinSensor(boolean pastebinSensor) {
        this.pastebinSensor = pastebinSensor;
    }

    public boolean isLeakhawk() {
        return leakhawk;
    }

    public void setLeakhawk(boolean leakhawk) {
        this.leakhawk = leakhawk;
    }
}
