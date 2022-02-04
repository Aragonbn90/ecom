package com.ed.ecom.domain;

import com.ed.ecom.domain.enumeration.OrderStatus;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Order.
 */
@Entity
@Table(name = "jhi_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total", nullable = false)
    private Float total;

    @Column(name = "discount")
    private Float discount;

    @Column(name = "fee")
    private Float fee;

    @Column(name = "actual_total")
    private Float actualTotal;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Order id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public Order status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Float getTotal() {
        return this.total;
    }

    public Order total(Float total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getDiscount() {
        return this.discount;
    }

    public Order discount(Float discount) {
        this.setDiscount(discount);
        return this;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getFee() {
        return this.fee;
    }

    public Order fee(Float fee) {
        this.setFee(fee);
        return this;
    }

    public void setFee(Float fee) {
        this.fee = fee;
    }

    public Float getActualTotal() {
        return this.actualTotal;
    }

    public Order actualTotal(Float actualTotal) {
        this.setActualTotal(actualTotal);
        return this;
    }

    public void setActualTotal(Float actualTotal) {
        this.actualTotal = actualTotal;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", total=" + getTotal() +
            ", discount=" + getDiscount() +
            ", fee=" + getFee() +
            ", actualTotal=" + getActualTotal() +
            "}";
    }
}
