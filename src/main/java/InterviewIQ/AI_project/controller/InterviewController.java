package InterviewIQ.AI_project.controller;

import InterviewIQ.AI_project.dto.InterviewDtos.*;
import InterviewIQ.AI_project.entity.Interview;
import InterviewIQ.AI_project.service.InterviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interview")
public class InterviewController {
    private final InterviewService interviewService;
    public InterviewController(InterviewService interviewService){
        this.interviewService=interviewService;
    }

    @PostMapping("/create")
    public InterviewResponse create(@RequestBody InterviewRequest request){
        Interview saveInterview=interviewService.create(request.getRole(),request.getExperienceLevel(),request.getDifficulty(), request.getDuration());
        return new InterviewResponse(true,"Interview has been created", saveInterview.getId());
    }

}
