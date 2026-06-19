package InterviewIQ.AI_project.service;

import InterviewIQ.AI_project.entity.Evaluation;
import InterviewIQ.AI_project.repository.EvaluationRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final AIService aiService;

    public EvaluationService(
            EvaluationRepository evaluationRepository,
            AIService aiService
    ) {
        this.evaluationRepository = evaluationRepository;
        this.aiService = aiService;
    }

    public static class OverallResult {
        public int score;
        public int fillerWords;
        public String confidence;
        public String relevance;
        public List<String> strengths = new ArrayList<>();
        public List<String> weakness = new ArrayList<>();
        public List<String> recommendations = new ArrayList<>();
    }
    @AllArgsConstructor
    public static class QA {
        public Long answerId;
        public String question;
        public String answer;
    }

    public OverallResult evaluateAnswerPair(List<QA> pairs) {

        OverallResult result = new OverallResult();

        if (pairs == null || pairs.isEmpty()) {
            result.weakness.add("No answers were provided");
            result.recommendations.add("Complete the interview");
            return result;
        }

        List<AIService.AIEvaluation> aiResults = new ArrayList<>();

        int totalScore = 0;
        int totalFillerWords = 0;

        for (QA pair : pairs) {

            String question =
                    pair.question == null
                            ? "(No question provided)"
                            : pair.question;

            String answer =
                    pair.answer == null
                            ? ""
                            : pair.answer;

            AIService.AIEvaluation evaluation =
                    aiService.evaluateWithAI(question, answer);

            // Fallback instead of crashing
            if (evaluation == null) {
                evaluation = new AIService.AIEvaluation();
                evaluation.score = 0;
                evaluation.fillerWords = 0;
                evaluation.relevance = "Low";
                evaluation.strengths = new ArrayList<>();
                evaluation.weaknesses = new ArrayList<>();
                evaluation.recommendations = new ArrayList<>();
                evaluation.weaknesses.add("AI evaluation unavailable");
                evaluation.recommendations.add("Try again later");
            }

            aiResults.add(evaluation);

            totalScore += evaluation.score;
            totalFillerWords += evaluation.fillerWords;

            result.strengths.addAll(evaluation.strengths);
            result.weakness.addAll(evaluation.weaknesses);
            result.recommendations.addAll(evaluation.recommendations);

            // Save individual answer evaluation
            if (pair.answerId != null) {

                Evaluation dbEvaluation = new Evaluation(
                        null, // evaluation id
                        pair.answerId,
                        evaluation.score,
                        "AI Score: " + evaluation.score
                );

                evaluationRepository.save(dbEvaluation);
            }
        }

        result.score = totalScore / aiResults.size();
        result.fillerWords = totalFillerWords;
        result.confidence = calculateConfidence(aiResults);
        result.relevance = calculateRelevance(aiResults);

        return result;
    }

    private String calculateConfidence(
            List<AIService.AIEvaluation> evaluations
    ) {

        int avgScore = evaluations.stream()
                .mapToInt(ev -> ev.score)
                .sum() / evaluations.size();

        if (avgScore >= 8) {
            return "High";
        }

        if (avgScore >= 5) {
            return "Medium";
        }

        return "Low";
    }

    private String calculateRelevance(
            List<AIService.AIEvaluation> evaluations
    ) {

        int avgScore = evaluations.stream()
                .mapToInt(ev -> ev.score)
                .sum() / evaluations.size();

        if (avgScore >= 8) {
            return "Excellent";
        }

        if (avgScore >= 5) {
            return "Good";
        }

        return "Needs Improvement";
    }
}