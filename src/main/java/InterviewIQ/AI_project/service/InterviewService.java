package InterviewIQ.AI_project.service;

import InterviewIQ.AI_project.entity.Interview;
import InterviewIQ.AI_project.repository.InterviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
        InterviewService(InterviewRepository interviewRepository){
            this.interviewRepository=interviewRepository;

        }
    public Interview create(String role,
                            String experienceLevel,
                            String difficulty,
                            Integer duration){
            Interview interview=new Interview();
            interview.setRole(role);
            interview.setExperienceLevel(experienceLevel);
            interview.setDuration(duration);
            interview.setDifficulty(difficulty);
            return interviewRepository.save(interview);
    }

}
