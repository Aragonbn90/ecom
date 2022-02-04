package com.ed.ecom.service.mapper;

import com.ed.ecom.domain.OrderProduct;
import com.ed.ecom.service.dto.OrderProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderProduct} and its DTO {@link OrderProductDTO}.
 */
@Mapper(componentModel = "spring", uses = { OrderMapper.class, ProductMapper.class })
public interface OrderProductMapper extends EntityMapper<OrderProductDTO, OrderProduct> {
    @Mapping(target = "order", source = "order", qualifiedByName = "id")
    @Mapping(target = "product", source = "product", qualifiedByName = "name")
    OrderProductDTO toDto(OrderProduct s);
}
