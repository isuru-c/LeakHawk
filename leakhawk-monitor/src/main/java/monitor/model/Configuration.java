package monitor.model;

import javax.persistence.*;

/**
 * @author Sugeesh Chandraweera
 */
@Entity
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String file;

    @Lob
    @Column(length=1024)
    private String content;


    public Configuration() {
    }

    public Configuration(String file, String content) {
        this.file = file;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
