package InterviewIQ.AI_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AnswerDtos {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerRequest {
        private Long questionId;
        private String answerText;

    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerResponse{
        private boolean success;
        private String message;
        private Long answerId;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubmitAnswerItem{

    }
}
