package monitor.resource;

/**
 * @author Sugeesh Chandraweera
 */
public class HeaderDataResource {
    private int totalPosts = 0;
    private int sensitivePosts  = 0;
    private int criticalPosts = 0;

    public HeaderDataResource() {
    }

    public int getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(int totalPosts) {
        this.totalPosts = totalPosts;
    }

    public int getSensitivePosts() {
        return sensitivePosts;
    }

    public void setSensitivePosts(int sensitivePosts) {
        this.sensitivePosts = sensitivePosts;
    }

    public int getCriticalPosts() {
        return criticalPosts;
    }

    public void setCriticalPosts(int criticalPosts) {
        this.criticalPosts = criticalPosts;
    }
}
