package edu.coursly.app.repository;

import edu.coursly.app.model.entity.Section;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findAllByCourse_Id(Long courseId);
}
