package co.com.bancolombia.r2dbc.product.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopStockProductProjection {

    private Long id;
    private Long idBranch;
    private String branchName;
    private String name;
    private Integer stockQuantity;

}
