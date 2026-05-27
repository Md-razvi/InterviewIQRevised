package InterviewIQ.AI_project.controller;

import InterviewIQ.AI_project.entity.Question;
import InterviewIQ.AI_project.service.QuestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService){
        this.questionService=questionService;
    }
    @GetMapping("/{interviewId}")
    public List<Question> getQuestions(@PathVariable Long interviewId){
        return questionService.getQuestions(interviewId);
    }
}
