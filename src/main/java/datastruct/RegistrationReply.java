package datastruct;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationReply {
    private UserProfile userProfile;
    private Boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;

    public boolean isSuccess() {
        return success != null && success;
    }

}
