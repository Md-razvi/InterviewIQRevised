package InterviewIQ.AI_project.controller;

import InterviewIQ.AI_project.dto.AuthDtos.*;
import InterviewIQ.AI_project.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public  AuthController(AuthService authService){
        this.authService=authService;
    }
    @PostMapping("/register")
    public ApiResponse register(@RequestBody RegisterRequest request){
        String message =authService.register(request.getName(),request.getEmail(),request.getPassword());
        boolean success=message.equals("User registered successfully");
        return new ApiResponse(success,message);
    }
    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest request){
        boolean isLoggedIn =authService.login(request.getEmail(),request.getPassword());
        if(isLoggedIn){
            return new ApiResponse(true,"Login successfully");
        }
        return new ApiResponse(false,"Login is not successful");

    }
}
