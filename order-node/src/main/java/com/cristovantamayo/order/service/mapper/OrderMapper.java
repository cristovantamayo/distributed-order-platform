package com.cristovantamayo.order.service.mapper;

import com.cristovantamayo.order.dao.OrderDTO;
import com.cristovantamayo.order.dao.OrderStatusEnum;
import com.cristovantamayo.order.repository.entities.OrderEntity;
import com.cristovantamayo.order.repository.entities.OrderItemEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.jaxb.SpringDataJaxb;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "eventType", target = "orderStatus")
    OrderEntity toEntity(OrderDTO orderDTO);

    OrderItemEntity toItemEntity(OrderDTO.Item item);

    @Mapping(source = "orderStatus", target = "eventType")
    List<OrderDTO> toDTOs(List<OrderEntity> orderEntities);

    OrderDTO toDTO(OrderEntity orderEntity);

    @AfterMapping
    default void linkItems(@MappingTarget OrderEntity order) {
        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }
    }
}
