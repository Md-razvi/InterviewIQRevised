package InterviewIQ.AI_project.service;

import InterviewIQ.AI_project.repository.EvaluationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final AIService aiService;
    public EvaluationService(EvaluationRepository evaluationRepository,AIService aiService){
        this.evaluationRepository=evaluationRepository;
        this.aiService=aiService;
    }
    public static class OverallResult {
        public int score;
        public int fillerWords;
        public String confidence;
        public String relevance;
        public List<String> strengths= new ArrayList<>();
        public List<String> weakness=new ArrayList<>();
        public List<String> recommendations=new ArrayList<>();
    }
    public static class QA{
        public Long answerId;
        public String question;
        public  String answer;

    }

    public OverallResult evaluateAnswerPair(List<QA> pairs){
        if(pairs==null || pairs.isEmpty()){
            OverallResult res=new OverallResult();
            res.score=0;
        }
        List<AIService.AIEvaluation> aiResult=new ArrayList<>();
        boolean aiOk=true;
        for(QA p: pairs){
            AIService.AIEvaluation ev=aiService.evaluateWithAI(p.question==null?"(no questions are present here)":p.question,p.answer==null?"":p.answer);
            if(ev==null){
                aiOk=false;
                break;
            }
            aiResult.add(ev);

        }
        return  null;


    }

}
