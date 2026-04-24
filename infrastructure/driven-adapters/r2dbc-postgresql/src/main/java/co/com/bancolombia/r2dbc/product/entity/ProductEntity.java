package co.com.bancolombia.r2dbc.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product", schema = "inventory")
public class ProductEntity {

    @Id
    @Column("id")
    private Long id;

    @Column("id_branch")
    private Long idBranch;

    @Column("name")
    private String name;

    @Column("stock_quantity")
    private Integer stockQuantity;

}
