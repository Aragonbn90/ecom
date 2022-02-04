package com.ed.ecom.service.dto;

import com.ed.ecom.domain.enumeration.OrderStatus;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.ed.ecom.domain.Order} entity.
 */
public class OrderDTO implements Serializable {

    private Long id;

    private OrderStatus status;

    @NotNull
    @DecimalMin(value = "0")
    private Float total;

    private Float discount;

    private Float fee;

    private Float actualTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getFee() {
        return fee;
    }

    public void setFee(Float fee) {
        this.fee = fee;
    }

    public Float getActualTotal() {
        return actualTotal;
    }

    public void setActualTotal(Float actualTotal) {
        this.actualTotal = actualTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderDTO)) {
            return false;
        }

        OrderDTO orderDTO = (OrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", total=" + getTotal() +
            ", discount=" + getDiscount() +
            ", fee=" + getFee() +
            ", actualTotal=" + getActualTotal() +
            "}";
    }
}
