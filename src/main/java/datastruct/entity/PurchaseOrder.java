package datastruct.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrder {
    private Components[] ingredients;
    private String _id;
    private AccountHolder accountHolder;
    private String status;
    private String name;
    private String createdAt;
    private String updatedAt;
    private Integer number;
    private Integer price;
}
