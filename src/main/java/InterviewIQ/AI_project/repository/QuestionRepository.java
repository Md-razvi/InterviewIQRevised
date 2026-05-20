package InterviewIQ.AI_project.repository;

import InterviewIQ.AI_project.entity.Question;
import InterviewIQ.AI_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
    Optional<User> findByInterviewId(Long interviewId);
}
