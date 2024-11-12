package wonpick.travel.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.UserDTO;
import wonpick.travel.server.entity.User;
import wonpick.travel.server.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;

    public UserDTO getUserInfo(String accessToken) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        return new UserDTO(user.getEmail(), user.getName(), user.getPhoneNumber());
    }


    public UserDTO updateUserInfo(String accessToken, UserDTO request) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);

        return new UserDTO(user.getEmail(), user.getName(), user.getPhoneNumber());
    }
}
