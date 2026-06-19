package InterviewIQ.AI_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class AnswerDtos {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubmitAnswerItem {
        private Long questionId;
        private String questionText;
        private String answerText;

    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubmitAnswerResponse{
        private boolean success;
        private int saved;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubmitAnswerRequest{
        private List<SubmitAnswerItem> answers;
    }
}
