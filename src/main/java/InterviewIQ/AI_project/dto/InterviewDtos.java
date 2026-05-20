package InterviewIQ.AI_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
public class InterviewDtos {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InterviewRequest {
        private String role;
        private String experienceLevel;
        private String difficulty;
        private Integer duration;
        private String finalScore;
        private LocalDateTime completedAt;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InterviewResponse{
        private boolean success;
        private String message;
        private Long interviewId;
    }

}
