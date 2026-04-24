package co.com.bancolombia.api.product.mappers;

import co.com.bancolombia.api.product.dto.CreateProductRequestDTO;
import co.com.bancolombia.api.product.dto.ProductResponseDTO;
import co.com.bancolombia.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDTO toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idBranch", source = "idBranch")
    Product toEntity(CreateProductRequestDTO request, Long idBranch);

}
