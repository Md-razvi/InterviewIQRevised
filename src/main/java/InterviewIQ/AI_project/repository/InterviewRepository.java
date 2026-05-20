package InterviewIQ.AI_project.repository;

import InterviewIQ.AI_project.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewRepository extends JpaRepository<Interview,Long> {
}
