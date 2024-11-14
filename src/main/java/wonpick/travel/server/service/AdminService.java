package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wonpick.travel.server.controller.AdminController;
import wonpick.travel.server.dto.UserDTO;
import wonpick.travel.server.entity.User;
import wonpick.travel.server.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {


    private final UserRepository userRepository;

    // 사용자 전체조회 API
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();

            return users.stream()
                    .map(user -> new UserDTO(
                            user.getEmail(),
                            user.getName(),
                            user.getPhoneNumber()
                    ))
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            // 데이터베이스 액세스 관련 예외 처리
            throw new RuntimeException("사용자 조회 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("알 수 없는 오류가 발생했습니다.");
        }
    }

    // 사용자 삭제 API
    @Transactional
    public void deleteUser(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

            userRepository.delete(user);
        } catch (EmptyResultDataAccessException e) {
            // 삭제하려는 사용자가 없을 때 발생하는 예외
            throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
        } catch (DataAccessException e) {
            // 데이터베이스 액세스 관련 예외 처리
            throw new RuntimeException("사용자 삭제 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("알 수 없는 오류가 발생했습니다.");
        }
    }

}
