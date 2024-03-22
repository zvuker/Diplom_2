package datastruct;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationData {
    private String email;
    private String password;

    public AuthenticationData(AccountDetails account) {
        this.email = account.getEmail();
        this.password = account.getPassword();
    }

    @Override
    public String toString() {
        return String.format("логин. email: %s; пароль: %s.", email, password);
    }
}
