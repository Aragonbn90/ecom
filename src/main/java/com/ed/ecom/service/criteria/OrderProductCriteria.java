package com.ed.ecom.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.ed.ecom.domain.OrderProduct} entity. This class is used
 * in {@link com.ed.ecom.web.rest.OrderProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /order-products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OrderProductCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private FloatFilter price;

    private IntegerFilter quantity;

    private FloatFilter total;

    private LongFilter orderId;

    private LongFilter productId;

    private Boolean distinct;

    public OrderProductCriteria() {}

    public OrderProductCriteria(OrderProductCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.price = other.price == null ? null : other.price.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.total = other.total == null ? null : other.total.copy();
        this.orderId = other.orderId == null ? null : other.orderId.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OrderProductCriteria copy() {
        return new OrderProductCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public FloatFilter getPrice() {
        return price;
    }

    public FloatFilter price() {
        if (price == null) {
            price = new FloatFilter();
        }
        return price;
    }

    public void setPrice(FloatFilter price) {
        this.price = price;
    }

    public IntegerFilter getQuantity() {
        return quantity;
    }

    public IntegerFilter quantity() {
        if (quantity == null) {
            quantity = new IntegerFilter();
        }
        return quantity;
    }

    public void setQuantity(IntegerFilter quantity) {
        this.quantity = quantity;
    }

    public FloatFilter getTotal() {
        return total;
    }

    public FloatFilter total() {
        if (total == null) {
            total = new FloatFilter();
        }
        return total;
    }

    public void setTotal(FloatFilter total) {
        this.total = total;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public LongFilter orderId() {
        if (orderId == null) {
            orderId = new LongFilter();
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public LongFilter productId() {
        if (productId == null) {
            productId = new LongFilter();
        }
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrderProductCriteria that = (OrderProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(price, that.price) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(total, that.total) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(productId, that.productId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, quantity, total, orderId, productId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderProductCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (price != null ? "price=" + price + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (total != null ? "total=" + total + ", " : "") +
            (orderId != null ? "orderId=" + orderId + ", " : "") +
            (productId != null ? "productId=" + productId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
