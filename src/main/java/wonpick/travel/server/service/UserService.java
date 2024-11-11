package wonpick.travel.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import wonpick.travel.server.dto.PostLoginUserRequest;
import wonpick.travel.server.dto.PostLoginUserResponse;
import wonpick.travel.server.dto.PostSignupUserRequest;
import wonpick.travel.server.dto.PostVerifySuccessUserResponse;
import wonpick.travel.server.dto.PostVerifyUserRequest;
import wonpick.travel.server.dto.PostVertifyUserResponse;
import wonpick.travel.server.dto.PostVerifyUserUserRequest;
import wonpick.travel.server.entity.User;
import wonpick.travel.server.repository.UserRepository;

import java.util.Base64;
import java.util.Map;

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
    public PostVertifyUserResponse verifyUser(PostVerifyUserRequest request) {
        try {
            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(clientId)
                    .username(request.getEmail())
                    .password(request.getPassword())
                    .userAttributes(
                            AttributeType.builder().name("email").value(request.getEmail()).build(),
                            AttributeType.builder().name("name").value(request.getName()).build(),
                            AttributeType.builder().name("phonenumber").value(request.getPhonenumber()).build()
                    )
                    .build();

            cognitoClient.signUp(signUpRequest);
            return new PostVertifyUserResponse("인증번호가 발송되었습니다. 이메일 인증을 확인해 주세요.");
        } catch (UsernameExistsException e) {
            throw new RuntimeException("이미 해당 이메일 주소로 가입된 사용자가 있습니다.", e);
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("인증번호 발송 중 오류가 발생했습니다: " + e.awsErrorDetails().errorMessage(), e);
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
        String eamil = request.getEmail();
        String password = request.getPassword();
        String name = request.getName();
        String phonenumber = request.getPhonenumber();
        Boolean notification = request.getNotification();

        User user = User.builder()
                .email(eamil)
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
            AdminGetUserRequest getUserRequest = AdminGetUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(request.getEmail())
                    .build();

            AdminGetUserResponse getUserResponse = cognitoClient.adminGetUser(getUserRequest);
            boolean isEmailVerified = getUserResponse.userAttributes().stream()
                    .anyMatch(attribute -> attribute.name().equals("email_verified") && attribute.value().equals("true"));

            if (!isEmailVerified) {
                throw new RuntimeException("사용자가 이메일 인증을 완료하지 않았습니다.");
            }

            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .clientId(clientId)
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .authParameters(Map.of(
                            "USERNAME", request.getEmail(),
                            "PASSWORD", request.getPassword()
                    ))
                    .build();

            InitiateAuthResponse response = cognitoClient.initiateAuth(authRequest);
            String accessToken = response.authenticationResult().accessToken();
//          String sub = response.authenticationResult().parse(accessToken());

            DecodedJWT jwt = JWT.decode(accessToken);
            String sub = jwt.getSubject();

            // sub 값
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(RuntimeException::new);
            user.setSub(sub);
            userRepository.save(user);

            return new PostLoginUserResponse("로그인 성공", accessToken, sub);
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("로그인 중 오류가 발생했습니다: " + e.awsErrorDetails().errorMessage(), e);
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
