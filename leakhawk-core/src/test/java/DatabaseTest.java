import db.DBConnection;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sugeesh Chandraweera
 */
public class DatabaseTest {
    @Rule
    public ContiPerfRule i = new ContiPerfRule();


    @Before
    public void setEval() {

    }

    @Test
    @PerfTest(invocations = 1, threads = 1)
    public void testForDatabaseConnection() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDBConnection().getConnection();
        assertNotNull(connection);
    }
}
