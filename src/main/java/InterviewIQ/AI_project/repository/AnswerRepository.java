package InterviewIQ.AI_project.repository;

import InterviewIQ.AI_project.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AnswerRepository extends JpaRepository<Answer,Long> {
}
