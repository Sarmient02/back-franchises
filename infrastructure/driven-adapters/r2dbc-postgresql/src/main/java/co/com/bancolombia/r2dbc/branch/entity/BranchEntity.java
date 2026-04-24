package co.com.bancolombia.r2dbc.branch.entity;

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
@Table(name = "branch", schema = "inventory")
public class BranchEntity {

    @Id
    @Column("id")
    private Long id;

    @Column("id_franchise")
    private Long idFranchise;

    @Column("name")
    private String name;

}

