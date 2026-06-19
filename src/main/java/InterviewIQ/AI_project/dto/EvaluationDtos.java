package InterviewIQ.AI_project.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


public class EvaluationDtos {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EvaluationRequest{
        private List<AnswerDtos.SubmitAnswerItem> answers;
    }
    @Data
    @AllArgsConstructor
    public static class EvaluationResponse{
        private boolean success;
        private int score;
        private int fillerWords;
        private String confidence;
        private String relevance;
        private List<String> strengths;
        private List<String> weakness;
        private List<String> recommendations;
    }



}
