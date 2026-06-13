package InterviewIQ.AI_project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIService {
    private final String geminiURL="https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent??key=";
    private final RestTemplate http;
    @Value("${ai.gemini.api-key:}")
    private String apiKey;

    public AIService(RestTemplate http) {
        this.http = http;
    }
    public static class AIEvaluation{
        public int score;
        public String relevance;
        public String technicalAccuracy;
        public int fillerWords;
        public List<String> strengths=new ArrayList<>();
        public List<String> weaknesses=new ArrayList<>();
        public List<String> recommendations=new ArrayList<>();

    }
    public  AIEvaluation evaluateWithAI(String question,String answer){
        if(apiKey==null || apiKey.isBlank()) return null;
        try{
            String prompt=buildPrompt(question,answer); //  1)English Instruction
            String aiText=callGemini(prompt);
            return parseEvaluation(aiText);
        }
        catch(Exception e){
            System.err.println("[AIService] Failed: " + e.getMessage());
            return null;
        }

    }
    public String buildPrompt(String question,String answer){
        return "You are an expert technical interviewer. " +
                "Evaluate the candidate's answer.\n\n" +
                "Question: " + question + "\n" +
                "Answer: "   + answer   + "\n" +
                "Evaluate based on:\n" +
                "1. Relevance to the question\n" +
                "2. Technical correctness\n" +
                "3. Clarity\n" +
                "4. Count filler words in the answer (um, uh, er, like, you know, basically)\n\n" +
                "Return ONLY valid JSON in this exact format (no markdown, no prose):\n" +
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

    public List<String> generateQuestion(String role,
                                         String experienceLevel,
                                         String difficulty,
                                         int count) {

        System.out.println("===== generateQuestion START =====");

        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("ERROR: Gemini API Key is missing!");
            return List.of();
        }

        try {

            String prompt = buildQuestionPrompt(
                    role,
                    experienceLevel,
                    difficulty,
                    count
            );

            System.out.println("\n===== GENERATED PROMPT =====");
            System.out.println(prompt);

            String aiText = callGemini(prompt);

            System.out.println("\n===== RAW AI RESPONSE =====");
            System.out.println(aiText);

            List<String> questions = parseGenerateQuestion(aiText);

            System.out.println("\n===== PARSED QUESTIONS =====");

            if (questions.isEmpty()) {
                System.out.println("No questions parsed!");
            } else {
                for (String q : questions) {
                    System.out.println(q);
                }
            }

            System.out.println("===== generateQuestion END =====");

            return questions;

        } catch (Exception e) {

            System.out.println("\n===== AI ERROR =====");
            e.printStackTrace();

            return List.of();
        }
    }

    private List<String> parseGenerateQuestion(String aiText) {


        String clean = aiText == null ? "" : aiText.trim();

        if (clean.startsWith("```")) {
            clean = clean.replaceFirst("^```(?:json)?", "")
                    .replaceFirst("```$", "")
                    .trim();
        }

        Matcher block = Pattern.compile("\\[(.*?)\\]", Pattern.DOTALL).matcher(clean);

        if (!block.find()) {
            return List.of();
        }

        List<String> out = new ArrayList<>();

        Matcher items = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"")
                .matcher(block.group(1));

        while (items.find()) {
            String q = unescapeJson(items.group(1)).trim();
            if (!q.isEmpty()) {
                out.add(q);
            }
        }

        return out.isEmpty() ? List.of() : out;
    }


    private String buildQuestionPrompt(String role, String experienceLevel, String difficulty, int count){
        String safeRole= ((role==null)||role.isBlank())? "Software Developer":role;
        String safeExperienceLevel=((experienceLevel==null)||experienceLevel.isBlank())?"Entry":experienceLevel;
        String safeDifficulty=((difficulty==null)|| difficulty.isBlank())?"Easy":difficulty;
        int n=(count<=0)?5:count;
        return "You are an expert technical interviewer. Generate exactly " + n + " interview questions for a "
                + safeExperienceLevel + " level candidate in the role of " + safeRole + " at " + safeDifficulty + " level.\n\n"
                + "Constraints:\n"
                + "- Each question must be exactly one sentence long.\n"
                + "- Each question must focus on a specific technical topic.\n"
                + "- Each question should be a trending question currently asked by hiring managers.\n\n"
                + "Output Format:\n"
                + "Return a valid JSON array of strings containing only the questions. "
                + "Strictly do NOT include any numbering, intro/outro preamble, or markdown code block formatting (such as ```json). "
                + "Output ONLY the raw JSON array. Example: [\"Question 1\", \"Question 2\"]";

    }
    //Build  a request Json:Http request to Gemini
    private String callGemini(String prompt) {

        String body =
                "{ \"contents\": [" +
                        "{ \"parts\": [" +
                        "{ \"text\": \"" + escapeJson(prompt) + "\" }" +
                        "] }" +
                        "] }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> req = new HttpEntity<>(body, headers);

        ResponseEntity<String> res =
                http.exchange(geminiURL + apiKey,
                        HttpMethod.POST,
                        req,
                        String.class);

        String full = res.getBody();
        System.out.println("\n===== FULL GEMINI RESPONSE =====");
        System.out.println(full);
        if (full == null || full.isBlank()) {
            throw new RuntimeException("Empty Gemini Response");
        }

        Matcher m = Pattern.compile("\"text\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"")
                .matcher(full);

        if (!m.find()) {
            throw new RuntimeException("No text in Gemini Response");
        }

        String result = unescapeJson(m.group(1));

        System.out.println("\n===== EXTRACTED TEXT =====");
        System.out.println(result);

        return result;
    }

    private String escapeJson(String prompt) {
        return prompt
                .replace("\\", "\\\\")
                .replace("\"","\\\"")
                .replace("\n","\\n")
                .replace("\r","\\r")
                .replace("\t","\\t");
    }
    private String unescapeJson(String s){
        return s
                .replace("\\\\","\\")
                .replace("\\\"","\"")
                .replace("\\n","\n")
                .replace("\\r","\r")
                .replace("\\t","\t");
    }
    private AIEvaluation parseEvaluation(String aiText){
        if (aiText == null || aiText.isBlank()) {
            return new AIEvaluation();
        }
        String clean = aiText.trim();
        if (clean.startsWith("```")) {
            clean = clean.replaceFirst("^```(?:json)?", "").replaceFirst("```$", "").trim();
        }
        AIEvaluation e=new AIEvaluation();
        e.score=parseInt(clean,"score",50);
        e.fillerWords       = parseInt(clean,    "fillerWords",       0);
        e.relevance         = parseString(clean, "relevance",         "medium");
        e.technicalAccuracy = parseString(clean, "technicalAccuracy", "average");
        e.strengths         = parseArray(clean,  "strengths");
        e.weaknesses       = parseArray(clean,  "weaknesses");
        e.recommendations   = parseArray(clean,  "recommendations");
        return e;

    }
    private int parseInt(String json, String key, int fallback) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)").matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : fallback;
    }
    private List<String> parseArray(String json, String key) {
        List<String> out = new ArrayList<>();
        Matcher block = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL)
                .matcher(json);
        if (!block.find()) return out;   // key missing -> empty list

        Matcher items = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"").matcher(block.group(1));
        while (items.find()) out.add(unescapeJson(items.group(1)));
        return out;
    }
    private String parseString(String json, String key, String fallback) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : fallback;
    }


}

