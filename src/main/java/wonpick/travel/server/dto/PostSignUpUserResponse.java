package wonpick.travel.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wonpick.travel.server.entity.UserPassenger;
import wonpick.travel.server.entity.Reservation;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostSignUpUserResponse {

    private Long id;
    private String email;
    private String phoneNumber;
    private Boolean notification;
    private List<UserPassenger> userPassengers;
    private List<Reservation> reservations;

    public PostSignUpUserResponse(Long id, String email, String phoneNumber, Boolean notification,
                                  List<UserPassenger> userPassengers, List<Reservation> reservations) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.notification = notification;
        this.userPassengers = userPassengers;
        this.reservations = reservations;
    }
}
