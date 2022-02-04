package com.ed.ecom.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ed.ecom.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderProduct.class);
        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setId(1L);
        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setId(orderProduct1.getId());
        assertThat(orderProduct1).isEqualTo(orderProduct2);
        orderProduct2.setId(2L);
        assertThat(orderProduct1).isNotEqualTo(orderProduct2);
        orderProduct1.setId(null);
        assertThat(orderProduct1).isNotEqualTo(orderProduct2);
    }
}
