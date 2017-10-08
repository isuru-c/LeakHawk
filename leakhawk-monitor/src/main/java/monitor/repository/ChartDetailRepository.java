package monitor.repository;

import monitor.model.ChartDetail;
import monitor.model.Incident;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Sugeesh Chandraweera
 */
public interface ChartDetailRepository extends CrudRepository<ChartDetail, Long> {

}
