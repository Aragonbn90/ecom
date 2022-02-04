package com.ed.ecom.service.criteria;

import com.ed.ecom.domain.enumeration.OrderStatus;
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
 * Criteria class for the {@link com.ed.ecom.domain.Order} entity. This class is used
 * in {@link com.ed.ecom.web.rest.OrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OrderCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrderStatus
     */
    public static class OrderStatusFilter extends Filter<OrderStatus> {

        public OrderStatusFilter() {}

        public OrderStatusFilter(OrderStatusFilter filter) {
            super(filter);
        }

        @Override
        public OrderStatusFilter copy() {
            return new OrderStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private OrderStatusFilter status;

    private FloatFilter total;

    private FloatFilter discount;

    private FloatFilter fee;

    private FloatFilter actualTotal;

    private Boolean distinct;

    public OrderCriteria() {}

    public OrderCriteria(OrderCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.total = other.total == null ? null : other.total.copy();
        this.discount = other.discount == null ? null : other.discount.copy();
        this.fee = other.fee == null ? null : other.fee.copy();
        this.actualTotal = other.actualTotal == null ? null : other.actualTotal.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OrderCriteria copy() {
        return new OrderCriteria(this);
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

    public OrderStatusFilter getStatus() {
        return status;
    }

    public OrderStatusFilter status() {
        if (status == null) {
            status = new OrderStatusFilter();
        }
        return status;
    }

    public void setStatus(OrderStatusFilter status) {
        this.status = status;
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

    public FloatFilter getDiscount() {
        return discount;
    }

    public FloatFilter discount() {
        if (discount == null) {
            discount = new FloatFilter();
        }
        return discount;
    }

    public void setDiscount(FloatFilter discount) {
        this.discount = discount;
    }

    public FloatFilter getFee() {
        return fee;
    }

    public FloatFilter fee() {
        if (fee == null) {
            fee = new FloatFilter();
        }
        return fee;
    }

    public void setFee(FloatFilter fee) {
        this.fee = fee;
    }

    public FloatFilter getActualTotal() {
        return actualTotal;
    }

    public FloatFilter actualTotal() {
        if (actualTotal == null) {
            actualTotal = new FloatFilter();
        }
        return actualTotal;
    }

    public void setActualTotal(FloatFilter actualTotal) {
        this.actualTotal = actualTotal;
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
        final OrderCriteria that = (OrderCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(total, that.total) &&
            Objects.equals(discount, that.discount) &&
            Objects.equals(fee, that.fee) &&
            Objects.equals(actualTotal, that.actualTotal) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, total, discount, fee, actualTotal, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (total != null ? "total=" + total + ", " : "") +
            (discount != null ? "discount=" + discount + ", " : "") +
            (fee != null ? "fee=" + fee + ", " : "") +
            (actualTotal != null ? "actualTotal=" + actualTotal + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
