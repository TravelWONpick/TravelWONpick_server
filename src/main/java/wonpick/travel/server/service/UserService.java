package wonpick.travel.server.service;

import wonpick.travel.server.dto.PostLoginUserRequest;
import wonpick.travel.server.dto.PostUserLoginResponse;
import wonpick.travel.server.dto.PostSignUpUserRequest;
import wonpick.travel.server.dto.PostSignUpUserResponse;
import wonpick.travel.server.entity.User;
import wonpick.travel.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원 가입
    public PostSignUpUserResponse registerUser(PostSignUpUserRequest postSignUpUserRequest) {
        User user = User.builder()
                .email(postSignUpUserRequest.getEmail())
                .password(postSignUpUserRequest.getPassword())
                .phoneNumber(postSignUpUserRequest.getPhoneNumber())
                .notification(postSignUpUserRequest.getNotification())
                .build();

        User savedUser = userRepository.save(user);
        return new PostSignUpUserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getNotification(),
                savedUser.getUserPassengers(),
                savedUser.getReservations()
        );
    }

    // 로그인 기능
    public PostUserLoginResponse loginUser(PostLoginUserRequest postLoginUserRequest) {
        Optional<User> userOpt = userRepository.findByEmail(postLoginUserRequest.getEmail());

        // 이메일과 비밀번호 검증
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(postLoginUserRequest.getPassword())) {
            User user = userOpt.get();
            return new PostUserLoginResponse(
                    user.getId(),
                    user.getEmail(),
                    "Login successful"
            );
        } else {
            throw new RuntimeException("Invalid email or password");
        }
    }

    // 회원 조회 by ID
    public PostSignUpUserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return new PostSignUpUserResponse(
                user.getId(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getNotification(),
                user.getUserPassengers(),
                user.getReservations()
        );
    }

    // 모든 회원 조회
    public List<PostSignUpUserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new PostSignUpUserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getNotification(),
                        user.getUserPassengers(),
                        user.getReservations()
                ))
                .collect(Collectors.toList());
    }
}
