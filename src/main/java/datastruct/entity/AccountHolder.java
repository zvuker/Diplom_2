package datastruct.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountHolder {
    private String name;
    private String email;
    private String createdAt;
    private String updatedAt;
}
