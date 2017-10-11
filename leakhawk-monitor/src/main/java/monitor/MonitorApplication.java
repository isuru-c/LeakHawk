package monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * @author Sugeesh Chandraweera
 */
//@SpringBootApplication
//public class MonitorApplication extends SpringBootServletInitializer {
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(MonitorApplication.class);
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(MonitorApplication.class, args);
//    }
//}


@SpringBootApplication
public class MonitorApplication{

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }
}
