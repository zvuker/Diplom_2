package datastruct.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Components {
    private String _id;
    private String name;
    private String type;
    private Integer calories;
    private Integer fat;
    private Integer proteins;
    private Integer carbohydrates;
    private Integer price;
    private String image;
    private String image_mobile;
    private String image_large;
    private Integer __v;
}
