package wonpick.travel.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GlobalSignOutRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import wonpick.travel.server.dto.*;
import wonpick.travel.server.entity.User;
import wonpick.travel.server.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final UserRepository userRepository;

    @Value("${cloud.aws.cognito.user-pool-id}")
    private String userPoolId;

    @Value("${cloud.aws.cognito.client-id}")
    private String clientId;

    // 인증번호 전송 API
    public PostVerifyUserResponse verifyUser(PostVerifyUserRequest request) {
        try {
            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(clientId)
                    .username(request.getEmail())
                    .password(request.getPassword())
                    .userAttributes(
                            AttributeType.builder().name("email").value(request.getEmail()).build(),
                            AttributeType.builder().name("name").value((request.getName())).build(),
                            AttributeType.builder().name("custom:phone").value(request.getPhonenumber()).build()
                    )
                    .build();

            cognitoClient.signUp(signUpRequest);
            return new PostVerifyUserResponse("인증번호가 발송되었습니다. 이메일 인증을 확인해 주세요.");
        } catch (UsernameExistsException e) {
            // 이미 가입된 사용자에 대한 예외 메시지
            throw new RuntimeException("이미 해당 이메일 주소로 가입된 사용자가 있습니다.");
        } catch (CognitoIdentityProviderException e) {
            String errorMessage = e.awsErrorDetails().errorMessage();
            if (errorMessage.contains("daily email limit")) {
                throw new RuntimeException("인증번호 발송 한도를 초과했습니다. 잠시 후 다시 시도해 주세요.");
            } else if (errorMessage.contains("InvalidParameterException")) {
                throw new RuntimeException("입력된 정보에 오류가 있습니다. 확인 후 다시 시도해 주세요.");
            } else {
                throw new RuntimeException("인증번호 발송 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            }
        }
    }


    // 인증번호 확인 API
    public PostVerifySuccessUserResponse verifySuccess(PostVerifyUserUserRequest request) {
        try {
            ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                    .clientId(clientId)
                    .username(request.getEmail())
                    .confirmationCode(request.getConfirmationCode())
                    .build();

            cognitoClient.confirmSignUp(confirmSignUpRequest);
            return new PostVerifySuccessUserResponse("이메일 인증이 완료되었습니다.");
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("이메일 인증 중 오류가 발생했습니다: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    // 최종 회원가입 - DB 저장
    @Transactional
    public void signUp(PostSignupUserRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String name = request.getName();
        String phonenumber = request.getPhonenumber();
        Boolean notification = request.getNotification();

        User user = User.builder()
                .email(email)
                .password(password)
                .name(name)
                .phoneNumber(phonenumber)
                .notification(notification)
                .build();

        userRepository.save(user);
    }

    // 로그인 API
    public PostLoginUserResponse login(PostLoginUserRequest request) {
        try {
            // 사용자 정보 요청
            AdminGetUserRequest getUserRequest = AdminGetUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(request.getEmail())
                    .build();

            AdminGetUserResponse getUserResponse = cognitoClient.adminGetUser(getUserRequest);

            // 이메일 인증 여부 확인
            boolean isEmailVerified = getUserResponse.userAttributes().stream()
                    .anyMatch(attribute -> attribute.name().equals("email_verified") && attribute.value().equals("true"));

            if (!isEmailVerified) {
                throw new RuntimeException("사용자가 이메일 인증을 완료하지 않았습니다.");
            }

            // 인증 요청
            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .clientId(clientId)
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .authParameters(Map.of(
                            "USERNAME", request.getEmail(),
                            "PASSWORD", request.getPassword()
                    ))
                    .build();

            // 인증 응답 처리
            InitiateAuthResponse response = cognitoClient.initiateAuth(authRequest);
            String accessToken = response.authenticationResult().accessToken();
            String idToken = response.authenticationResult().idToken();

            if (accessToken == null || idToken == null) {
                throw new RuntimeException("Access token 또는 ID token 생성에 실패했습니다.");
            }

            // 사용자 이름 가져오기
            String name = getUserResponse.userAttributes().stream()
                    .filter(attribute -> "name".equals(attribute.name()))
                    .map(AttributeType::value)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("사용자의 이름 속성을 찾을 수 없습니다."));

            // sub 값 저장
            DecodedJWT jwt = JWT.decode(accessToken);
            String sub = jwt.getSubject();

            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(RuntimeException::new);
            if (user.getSub() == null) {
                user.setSub(sub);
                userRepository.save(user);
            }

            // 관리자인지 여부 확인 (ID 토큰에서 확인)
            DecodedJWT idTokenDecoded = JWT.decode(idToken);
            List<String> groups = idTokenDecoded.getClaim("cognito:groups").asList(String.class);
            boolean isAdmin = groups != null && groups.contains("admin");

            return new PostLoginUserResponse("로그인 성공", accessToken, name, isAdmin);

        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("로그인 중 오류가 발생했습니다: " + e.awsErrorDetails().errorMessage(), e);
        }
    }


    public boolean isAdmin(String IdToken) {
        try {
            DecodedJWT jwt = JWT.decode(IdToken);
            String role = jwt.getClaim("cognito:groups").asString();

            return "admin".equals(role);
        } catch (JWTDecodeException e) {
            throw new RuntimeException("JWT 토큰 디코딩 중 오류 발생 " + e.getMessage(), e);
        }
    }

    // 로그아웃 API
    public String logout(String accessToken) {
        try {
            GlobalSignOutRequest signOutRequest = GlobalSignOutRequest.builder()
                    .accessToken(accessToken)
                    .build();
            cognitoClient.globalSignOut(signOutRequest);
            return "로그아웃이 완료되었습니다.";
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("로그아웃 중 오류가 발생했습니다: " + e.awsErrorDetails().errorMessage(), e);
        }
    }
}
