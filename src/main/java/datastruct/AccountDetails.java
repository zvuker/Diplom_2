package datastruct;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AccountDetails {
    private String email;
    private String password;
    private String name;

    @Override
    public String toString() {
        return String.format("аккаунт. имя: %s; email: %s; пароль: %s.", name, email, password);
    }
}
