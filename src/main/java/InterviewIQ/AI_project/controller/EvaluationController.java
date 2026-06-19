package InterviewIQ.AI_project.controller;

import InterviewIQ.AI_project.dto.AnswerDtos;
import InterviewIQ.AI_project.entity.Answer;
import InterviewIQ.AI_project.dto.AnswerDtos.*;
import InterviewIQ.AI_project.service.AnswerService;
import InterviewIQ.AI_project.service.EvaluationService;
import InterviewIQ.AI_project.dto.EvaluationDtos.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController

@RequestMapping("/evaluate")
public class EvaluationController {
    private final AnswerService answerService;
    private final EvaluationService evaluationService;
    public EvaluationController(AnswerService answerService,
                                EvaluationService evaluationService){
        this.answerService=answerService;
        this.evaluationService=evaluationService;

    }
    @PostMapping("/evaluate-answer")
    public EvaluationResponse evaluate(@RequestBody EvaluationRequest evaluationRequest){
        List<EvaluationService.QA> pair=new ArrayList<>();
        if(!evaluationRequest.getAnswers().isEmpty()){
            List<Answer> toSave=new ArrayList<>();
            for(SubmitAnswerItem item: evaluationRequest.getAnswers()){
                Answer a=new Answer();
                a.setAnswerText(item.getAnswerText());
                a.setQuestionId(item.getQuestionId());
                toSave.add(a);
            }
            List <SubmitAnswerItem> answers=evaluationRequest.getAnswers();
            List <Answer> saved=answerService.SaveAll(toSave);
            for(int i=0;i<answers.size();i++){
                SubmitAnswerItem itm=answers.get(i);
                Long answerId=saved.get(i).getId();
                pair.add(new EvaluationService.QA(answerId,itm.getQuestionText(),itm.getAnswerText()));
            }

        }
        EvaluationService.OverallResult r=evaluationService.evaluateAnswerPair(pair);
        return new EvaluationResponse(true,r.score,r.fillerWords,r.confidence,r.relevance,r.strengths,r.weakness,r.recommendations);
    }
}
