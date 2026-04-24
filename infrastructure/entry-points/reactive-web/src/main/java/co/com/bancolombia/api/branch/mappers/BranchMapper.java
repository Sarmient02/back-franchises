package co.com.bancolombia.api.branch.mappers;

import co.com.bancolombia.api.branch.dto.BranchResponseDTO;
import co.com.bancolombia.api.branch.dto.CreateBranchRequestDTO;
import co.com.bancolombia.model.branch.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    @Mapping(target = "idFranchise", source = "idFranchise")
    BranchResponseDTO toResponse(Branch branch);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idFranchise", source = "idFranchise")
    Branch toEntity(CreateBranchRequestDTO request, Long idFranchise);

}



