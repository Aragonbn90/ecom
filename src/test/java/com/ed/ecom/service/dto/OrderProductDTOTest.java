package com.ed.ecom.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ed.ecom.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderProductDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderProductDTO.class);
        OrderProductDTO orderProductDTO1 = new OrderProductDTO();
        orderProductDTO1.setId(1L);
        OrderProductDTO orderProductDTO2 = new OrderProductDTO();
        assertThat(orderProductDTO1).isNotEqualTo(orderProductDTO2);
        orderProductDTO2.setId(orderProductDTO1.getId());
        assertThat(orderProductDTO1).isEqualTo(orderProductDTO2);
        orderProductDTO2.setId(2L);
        assertThat(orderProductDTO1).isNotEqualTo(orderProductDTO2);
        orderProductDTO1.setId(null);
        assertThat(orderProductDTO1).isNotEqualTo(orderProductDTO2);
    }
}
