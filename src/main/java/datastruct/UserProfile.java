package datastruct;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserProfile {
    private String name;
    private String email;
}
