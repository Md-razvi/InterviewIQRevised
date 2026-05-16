package InterviewIQ.AI_project.service;

import InterviewIQ.AI_project.entity.User;
import InterviewIQ.AI_project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    public AuthService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    public String register(String name ,String email,String password){
        boolean isOk=userRepository.findByEmail(email).isPresent();
        if(isOk){
            return "Email is already registered";
        }
        User user=new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);
        return "User registered successfully";
    }
    public boolean login(String email,String password){
        Optional<User> LoginUser=userRepository.findByEmail(email);

        return LoginUser.isPresent() && LoginUser.get().getPassword().equals(password);
    }





}
