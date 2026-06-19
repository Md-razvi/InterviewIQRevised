
package InterviewIQ.AI_project.controller;

import InterviewIQ.AI_project.entity.Answer;

import InterviewIQ.AI_project.service.AnswerService;

import org.springframework.web.bind.annotation.*;

import InterviewIQ.AI_project.dto.AnswerDtos.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/answer")
public class AnswerController {
    private final  AnswerService answerService;
    public AnswerController(AnswerService answerService)
    {
        this.answerService=answerService;
    }
    @PostMapping
    public SubmitAnswerResponse getAnswers(@RequestBody SubmitAnswerRequest request){
        List <Answer> givenAnswer=new ArrayList<>();
        if(request.getAnswers()!=null){
            for(SubmitAnswerItem item: request.getAnswers()) {
                Answer a = new Answer();
                a.setQuestionId(item.getQuestionId());
                a.setAnswerText(item.getAnswerText());
                givenAnswer.add(a);
            }
        }
        List<Answer> saved=answerService.SaveAll(givenAnswer);
        return new SubmitAnswerResponse(true,saved.size());
    }



}
