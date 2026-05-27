package InterviewIQ.AI_project.service;

import InterviewIQ.AI_project.entity.Interview;
import InterviewIQ.AI_project.entity.Question;
import InterviewIQ.AI_project.repository.InterviewRepository;
import InterviewIQ.AI_project.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepository questionRepository;
    private final InterviewRepository interviewRepository;
    private final AIService aiService;

    private static final List<String> FALLBACK_QUESTIONS = List.of(
            "Tell me about yourself?",
            "Explain OOP concepts in Java.",
            "What is the difference between ArrayList and LinkedList?",
            "Explain REST API.",
            "What is Dependency Injection in Spring Boot?",
            "What is the difference between == and equals() in Java?",
            "Name some features of Java 8."
    );

    public QuestionService(QuestionRepository questionRepository,
                           InterviewRepository interviewRepository,
                           AIService aiService) {
        this.questionRepository = questionRepository;
        this.interviewRepository = interviewRepository;
        this.aiService = aiService;
    }

    public List<Question> getQuestions(Long interviewId) {

        if (interviewId == null) {
            return List.of();
        }

        Optional<Interview> interviewOpt = interviewRepository.findById(interviewId);
        if (interviewOpt.isEmpty()) {
            return List.of();
        }

        Interview interview = interviewOpt.get();

        List<Question> existingQuestions = questionRepository.findByInterviewId(interviewId);
        if (existingQuestions != null && !existingQuestions.isEmpty()) {
            return existingQuestions;
        }

        List<String> generatedText = aiService.generateQuestion(
                interview.getRole(),
                interview.getExperienceLevel(),
                interview.getDifficulty(),
                5
        );

        if (generatedText == null || generatedText.isEmpty()) {
            log.warn("AI unavailable, using fallback questions.");
            generatedText = FALLBACK_QUESTIONS;
        }

        List<Question> questionsToSave = new ArrayList<>();
        for (String text : generatedText) {
            if (text != null && !text.trim().isEmpty()) {
                questionsToSave.add(new Question(null, interviewId, text.trim()));
            }
        }

        if (questionsToSave.isEmpty()) {
            return List.of();
        }

        return questionRepository.saveAll(questionsToSave);
    }
}