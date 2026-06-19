package InterviewIQ.AI_project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIService {

    private final ObjectMapper objectMapper;
    private Client client;

    @Value("${ai.gemini.api-key:}")
    private String apiKey;

    // Injecting standard Jackson ObjectMapper
    public AIService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private synchronized Client getClient() {
        if (client == null) {
            if (apiKey == null || apiKey.isBlank()) {
                // Fallback to default environment variable GEMINI_API_KEY
                client = new Client();
            } else {
                // Initialize with your custom spring-configured API key
                client = Client.builder().apiKey(apiKey).build();
            }
        }
        return client;
    }

    public static class AIEvaluation {
        public int score;
        public String relevance;
        public String technicalAccuracy;
        public int fillerWords;
        public List<String> strengths = new ArrayList<>();
        public List<String> weaknesses = new ArrayList<>();
        public List<String> recommendations = new ArrayList<>();
    }

    public AIEvaluation evaluateWithAI(String question, String answer) {
        if (apiKey == null || apiKey.isBlank()) return null;
        try {
            Client genaiClient = getClient();
            String prompt = buildPrompt(question, answer);

            // Generate content using the official SDK
            GenerateContentResponse response = genaiClient.models.generateContent(
                    "gemini-3.5-flash",
                    prompt,
                    null
            );

            String aiText = response.text().trim();
            return parseEvaluation(aiText);
        } catch (Exception e) {
            System.err.println("[AIService] Evaluation Failed: " + e.getMessage());
            return null;
        }
    }

    public String buildPrompt(String question, String answer) {
        return "You are an expert technical interviewer. Evaluate the candidate's answer.\n\n" +
                "A  short and precise answers is acceptable of 2 or 3 provided that it is correct"+
                "Also check if the candidate gives example if the questions have keywords like 'Explain','Can you give an example of this'"+
                "Question: " + question + "\n" +
                "Answer: " + answer + "\n\n" +
                "Evaluate based on:\n" +
                "1. Relevance to the question\n" +
                "2. Technical correctness\n" +
                "3. Clarity\n" +
                "4. Count filler words in the answer (um, uh, er, like, you know, basically)\n\n" +
                "Return ONLY valid JSON in this exact format (do not wrap in markdown blocks like ```json):\n" +
                "{\n" +
                "  \"score\": 0,\n" +
                "  \"fillerWords\": 0,\n" +
                "  \"relevance\": \"low\",\n" +
                "  \"technicalAccuracy\": \"poor\",\n" +
                "  \"strengths\": [\"...\"],\n" +
                "  \"weaknesses\": [\"...\"],\n" +
                "  \"recommendations\": [\"...\"]\n" +
                "}";
    }

    public List<String> generateQuestion(String role, String experienceLevel, String difficulty, int count) {
        if (apiKey == null || apiKey.isBlank()) {
            return List.of();
        }

        try {
            Client genaiClient = getClient();
            String prompt = buildQuestionPrompt(role, experienceLevel, difficulty, count);

            // Corrected: Now calling the SDK instead of the deprecated callGemini() method!
            GenerateContentResponse response = genaiClient.models.generateContent(
                    "gemini-3.5-flash",
                    prompt,
                    null
            );

            String aiText = response.text().trim();
            return parseGenerateQuestion(aiText);

        } catch (Exception e) {
            System.err.println("AI Error generating questions: " + e.getMessage());
            return List.of();
        }
    }

    private String buildQuestionPrompt(String role, String experienceLevel, String difficulty, int count) {
        String safeRole = (role == null || role.isBlank()) ? "Software Developer" : role;
        String safeExperienceLevel = (experienceLevel == null || experienceLevel.isBlank()) ? "Entry" : experienceLevel;
        String safeDifficulty = (difficulty == null || difficulty.isBlank()) ? "Easy" : difficulty;
        int n = (count <= 0) ? 5 : count;

        return "You are an expert technical interviewer. Generate exactly " + n + " interview questions for a "
                + safeExperienceLevel + " level candidate in the role of " + safeRole + " at " + safeDifficulty + " level.\n\n"
                + "Constraints:\n"
                + "- Each question must be exactly one sentence long.\n"
                + "- Each question must focus on a specific technical topic.\n"
                + "- Each question should be a trending question currently asked by hiring managers.\n\n"
                + "Output Format:\n"
                + "Return a valid JSON array of strings containing only the questions. "
                + "Do NOT wrap inside markdown block formatting (such as ```json). Output ONLY the raw JSON array.\n"
                + "Example: [\"Question 1\", \"Question 2\"]";
    }

    private AIEvaluation parseEvaluation(String aiText) {
        try {
            String clean = cleanJsonMarkdown(aiText);
            return objectMapper.readValue(clean, AIEvaluation.class);
        } catch (Exception e) {
            System.err.println("Jackson parsing error (Evaluation): " + e.getMessage());
            return new AIEvaluation();
        }
    }

    private List<String> parseGenerateQuestion(String aiText) {
        try {
            String clean = cleanJsonMarkdown(aiText);
            return objectMapper.readValue(clean, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            System.err.println("Jackson parsing error (Questions): " + e.getMessage());
            return List.of();
        }
    }

    private String cleanJsonMarkdown(String text) {
        if (text == null) return "{}";
        String clean = text.trim();
        if (clean.startsWith("```")) {
            clean = clean.replaceFirst("^```(?:json)?", "").replaceFirst("```$", "").trim();
        }
        return clean;
    }
}
