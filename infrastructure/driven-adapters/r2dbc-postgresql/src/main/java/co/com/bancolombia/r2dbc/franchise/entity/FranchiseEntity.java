package co.com.bancolombia.r2dbc.franchise.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "franchise", schema = "inventory")
public class FranchiseEntity {

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

}
