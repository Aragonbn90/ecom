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
 * Criteria class for the {@link com.ed.ecom.domain.Product} entity. This class is used
 * in {@link com.ed.ecom.web.rest.ProductResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private FloatFilter price;

    private FloatFilter discountPercent;

    private FloatFilter newPrice;

    private StringFilter instructions;

    private FloatFilter netQuantity;

    private StringFilter netQuantityUnit;

    private StringFilter imageUrls;

    private StringFilter ingredients;

    private Boolean distinct;

    public ProductCriteria() {}

    public ProductCriteria(ProductCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.price = other.price == null ? null : other.price.copy();
        this.discountPercent = other.discountPercent == null ? null : other.discountPercent.copy();
        this.newPrice = other.newPrice == null ? null : other.newPrice.copy();
        this.instructions = other.instructions == null ? null : other.instructions.copy();
        this.netQuantity = other.netQuantity == null ? null : other.netQuantity.copy();
        this.netQuantityUnit = other.netQuantityUnit == null ? null : other.netQuantityUnit.copy();
        this.imageUrls = other.imageUrls == null ? null : other.imageUrls.copy();
        this.ingredients = other.ingredients == null ? null : other.ingredients.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProductCriteria copy() {
        return new ProductCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
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

    public FloatFilter getDiscountPercent() {
        return discountPercent;
    }

    public FloatFilter discountPercent() {
        if (discountPercent == null) {
            discountPercent = new FloatFilter();
        }
        return discountPercent;
    }

    public void setDiscountPercent(FloatFilter discountPercent) {
        this.discountPercent = discountPercent;
    }

    public FloatFilter getNewPrice() {
        return newPrice;
    }

    public FloatFilter newPrice() {
        if (newPrice == null) {
            newPrice = new FloatFilter();
        }
        return newPrice;
    }

    public void setNewPrice(FloatFilter newPrice) {
        this.newPrice = newPrice;
    }

    public StringFilter getInstructions() {
        return instructions;
    }

    public StringFilter instructions() {
        if (instructions == null) {
            instructions = new StringFilter();
        }
        return instructions;
    }

    public void setInstructions(StringFilter instructions) {
        this.instructions = instructions;
    }

    public FloatFilter getNetQuantity() {
        return netQuantity;
    }

    public FloatFilter netQuantity() {
        if (netQuantity == null) {
            netQuantity = new FloatFilter();
        }
        return netQuantity;
    }

    public void setNetQuantity(FloatFilter netQuantity) {
        this.netQuantity = netQuantity;
    }

    public StringFilter getNetQuantityUnit() {
        return netQuantityUnit;
    }

    public StringFilter netQuantityUnit() {
        if (netQuantityUnit == null) {
            netQuantityUnit = new StringFilter();
        }
        return netQuantityUnit;
    }

    public void setNetQuantityUnit(StringFilter netQuantityUnit) {
        this.netQuantityUnit = netQuantityUnit;
    }

    public StringFilter getImageUrls() {
        return imageUrls;
    }

    public StringFilter imageUrls() {
        if (imageUrls == null) {
            imageUrls = new StringFilter();
        }
        return imageUrls;
    }

    public void setImageUrls(StringFilter imageUrls) {
        this.imageUrls = imageUrls;
    }

    public StringFilter getIngredients() {
        return ingredients;
    }

    public StringFilter ingredients() {
        if (ingredients == null) {
            ingredients = new StringFilter();
        }
        return ingredients;
    }

    public void setIngredients(StringFilter ingredients) {
        this.ingredients = ingredients;
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
        final ProductCriteria that = (ProductCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(price, that.price) &&
            Objects.equals(discountPercent, that.discountPercent) &&
            Objects.equals(newPrice, that.newPrice) &&
            Objects.equals(instructions, that.instructions) &&
            Objects.equals(netQuantity, that.netQuantity) &&
            Objects.equals(netQuantityUnit, that.netQuantityUnit) &&
            Objects.equals(imageUrls, that.imageUrls) &&
            Objects.equals(ingredients, that.ingredients) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            description,
            price,
            discountPercent,
            newPrice,
            instructions,
            netQuantity,
            netQuantityUnit,
            imageUrls,
            ingredients,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (price != null ? "price=" + price + ", " : "") +
            (discountPercent != null ? "discountPercent=" + discountPercent + ", " : "") +
            (newPrice != null ? "newPrice=" + newPrice + ", " : "") +
            (instructions != null ? "instructions=" + instructions + ", " : "") +
            (netQuantity != null ? "netQuantity=" + netQuantity + ", " : "") +
            (netQuantityUnit != null ? "netQuantityUnit=" + netQuantityUnit + ", " : "") +
            (imageUrls != null ? "imageUrls=" + imageUrls + ", " : "") +
            (ingredients != null ? "ingredients=" + ingredients + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
