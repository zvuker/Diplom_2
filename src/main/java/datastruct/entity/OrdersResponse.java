package datastruct.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.google.gson.annotations.SerializedName;
import lombok.experimental.Accessors;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OrdersResponse {
    private String success;
    private String name;
    @SerializedName("orders")
    private ArrayList<PurchaseOrders> orders;
    private PurchaseOrder purchaseOrder;
    private Integer total;
    private Integer totalToday;
}
