package com.ed.ecom.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderProductMapperTest {

    private OrderProductMapper orderProductMapper;

    @BeforeEach
    public void setUp() {
        orderProductMapper = new OrderProductMapperImpl();
    }
}
