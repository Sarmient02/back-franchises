package co.com.bancolombia.api.franchise.mappers;

import co.com.bancolombia.api.franchise.dto.CreateFranchiseRequestDTO;
import co.com.bancolombia.api.franchise.dto.FranchiseResponseDTO;
import co.com.bancolombia.model.franchise.Franchise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {

    FranchiseResponseDTO toResponse(Franchise franchise);

    Franchise toEntity(CreateFranchiseRequestDTO createFranchiseRequestDTO);
}
