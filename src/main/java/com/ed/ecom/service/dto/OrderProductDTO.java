package com.ed.ecom.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.ed.ecom.domain.OrderProduct} entity.
 */
public class OrderProductDTO implements Serializable {

    private Long id;

    private Float price;

    @NotNull
    private Integer quantity;

    private Float total;

    private OrderDTO order;

    private ProductDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderProductDTO)) {
            return false;
        }

        OrderProductDTO orderProductDTO = (OrderProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderProductDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderProductDTO{" +
            "id=" + getId() +
            ", price=" + getPrice() +
            ", quantity=" + getQuantity() +
            ", total=" + getTotal() +
            ", order=" + getOrder() +
            ", product=" + getProduct() +
            "}";
    }
}
