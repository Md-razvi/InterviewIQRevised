package InterviewIQ.AI_project.service;

import InterviewIQ.AI_project.entity.Answer;
import InterviewIQ.AI_project.repository.AnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    public AnswerService(AnswerRepository answerRepository){
        this.answerRepository=answerRepository;
    }

    public Answer Save(Long questionId,String answerText){
        Answer ans=new Answer();
        ans.setQuestionId(questionId);
        ans.setAnswerText(answerText);
        return answerRepository.save(ans);
    }
    public List<Answer> SaveAll(List<Answer> ans_batch){
        return  answerRepository.saveAll(ans_batch);
    }


}
