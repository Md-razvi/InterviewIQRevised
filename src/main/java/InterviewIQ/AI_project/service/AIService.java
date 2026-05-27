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
    private String geminiURL="https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";
    private final RestTemplate http;
    @Value("${ai.gemini.api-key:}")
    private String apiKey;

    public AIService(RestTemplate http) {
        this.http = http;
    }

    public List<String> generateQuestion(String role,String experienceLevel,String difficulty,int count){
            if(apiKey==null || apiKey.isBlank()){
                return List.of();
            }
            try{
                String prompt=buildQuestionPrompt(role,experienceLevel,difficulty,count);
                String aiText=callGemini(prompt);
                return parseGenerateQuestion(aiText);

            }catch (Exception e){
                System.out.print("AI Error:"+e.getMessage());
                return List.of();
            }
    }

    private List<String> parseGenerateQuestion(String aiText) {
        String clean = aiText == null?"":aiText.trim();

        // Remove ```json ... ``` fences if present.
        // replaceFirst with regex: ^``` matches at start; (?:json)? optionally matches "json".
        if (clean.startsWith("```")) {
            clean = clean.replaceFirst("^```(?:json)?", "").replaceFirst("```$", "").trim();
        }

        // Step A: find the [...] block.
        //   "\\[" matches a literal '['  (in Java strings "\\[" is the 2-char regex \[ )
        //   "(.*?)" captures everything inside, lazily (the smallest match)
        //   "\\]" matches a literal ']'
        //   Pattern.DOTALL makes "." match newlines too.
        Matcher block = Pattern.compile("\\[(.*?)\\]", Pattern.DOTALL).matcher(clean);
        if (!block.find()) return null;

        // Step B: inside the brackets, find every "double-quoted string".
        //   "\""             -> a literal double quote
        //   ((?:\\.|[^"\\])*) -> capture: either an escaped char (\X) or any non-quote/non-backslash char
        //   "\""             -> closing double quote
        // This handles strings that contain escaped quotes like "He said \"hi\"".
        List<String> out = new ArrayList<>();
        Matcher items = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"").matcher(block.group(1));
        while (items.find()) {
            String q = unescapeJson(items.group(1)).trim();
            if (!q.isEmpty()) out.add(q);
        }
        return out.isEmpty() ? null : out;

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
                "{ \"contents\": [" + "{ \"parts\": [" +
                        "{ \"text\": \"" + escapeJson(prompt) + "\" }" +
                        "] }" +
                        "] }";
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> req=new HttpEntity<>(body,headers);
        ResponseEntity<String> res=http.exchange(geminiURL+apiKey, HttpMethod.POST,req,String.class);
        String full=res.getBody();
        Matcher m= Pattern.compile("\"text\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").matcher(full);
        if(!m.find()){
            throw new RuntimeException("No text in Gemini Response");
        }
        return unescapeJson(m.group(1));

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
}
