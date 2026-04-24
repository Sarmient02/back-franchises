package co.com.bancolombia.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TopStockProductByBranch {

    private Long id;
    private Long idBranch;
    private String branchName;
    private String name;
    private Integer stockQuantity;

}
