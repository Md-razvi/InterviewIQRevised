
package InterviewIQ.AI_project.controller;

import InterviewIQ.AI_project.entity.Answer;

import InterviewIQ.AI_project.service.AnswerService;

import org.springframework.web.bind.annotation.*;

import InterviewIQ.AI_project.dto.AnswerDtos.*;


@RestController
@RequestMapping("/answer")
public class AnswerController {
    private final  AnswerService answerService;
    public AnswerController(AnswerService answerService)
    {
        this.answerService=answerService;
    }
    @PostMapping
    public AnswerResponse getAnswers(@RequestBody AnswerRequest request){
        Answer ans=answerService.Save(request.getQuestionId(),request.getAnswerText());
        return new AnswerResponse(true,"Answer Saved",ans.getId());
    }



}
