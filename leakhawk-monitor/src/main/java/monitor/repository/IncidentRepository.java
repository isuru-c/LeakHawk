package monitor.repository;

import monitor.model.Incident;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Sugeesh Chandraweera
 */
public interface IncidentRepository extends CrudRepository<Incident, Long> {

//    @Query("UPDATE Category c SET c.priority = :priority WHERE c.categoryId = :categoryId")
//    List<Incident> findAllOrderBySensitivitylevelDesc();

    List<Incident> findBySensitivityLevelOrderByIdDesc(int level);

    List<Incident> findAllByOrderByDateDesc();

    Incident findByPostKey(String key);

    List<Incident> findAllByOrderBySensitivityLevelDescIdDesc();
}
