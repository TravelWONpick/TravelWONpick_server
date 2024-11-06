package wonpick.travel.server.controller;

import wonpick.travel.server.dto.PostSignUpUserRequest;
import wonpick.travel.server.dto.PostSignUpUserResponse;
import wonpick.travel.server.dto.PostLoginUserRequest;
import wonpick.travel.server.dto.PostUserLoginResponse;
import wonpick.travel.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원 가입 API
    @PostMapping("/signup")
    public ResponseEntity<PostSignUpUserResponse> registerUser(@RequestBody PostSignUpUserRequest postSignUpUserRequest) {
        PostSignUpUserResponse postSignUpUserResponse = userService.registerUser(postSignUpUserRequest);
        return ResponseEntity.ok(postSignUpUserResponse);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<PostUserLoginResponse> loginUser(@RequestBody PostLoginUserRequest postLoginUserRequest) {
        PostUserLoginResponse postUserLoginResponse = userService.loginUser(postLoginUserRequest);
        return ResponseEntity.ok(postUserLoginResponse);
    }

    // 특정 회원 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<PostSignUpUserResponse> getUserById(@PathVariable Long id) {
        PostSignUpUserResponse postSignUpUserResponse = userService.getUserById(id);
        return ResponseEntity.ok(postSignUpUserResponse);
    }

    // 모든 회원 조회 API
    @GetMapping
    public ResponseEntity<List<PostSignUpUserResponse>> getAllUsers() {
        List<PostSignUpUserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
