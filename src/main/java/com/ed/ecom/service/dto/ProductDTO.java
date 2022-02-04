package com.ed.ecom.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.ed.ecom.domain.Product} entity.
 */
public class ProductDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private Float price;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private Float discountPercent;

    private Float newPrice;

    private String instructions;

    private Float netQuantity;

    private String netQuantityUnit;

    private String imageUrls;

    private String ingredients;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Float discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Float getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Float newPrice) {
        this.newPrice = newPrice;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Float getNetQuantity() {
        return netQuantity;
    }

    public void setNetQuantity(Float netQuantity) {
        this.netQuantity = netQuantity;
    }

    public String getNetQuantityUnit() {
        return netQuantityUnit;
    }

    public void setNetQuantityUnit(String netQuantityUnit) {
        this.netQuantityUnit = netQuantityUnit;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDTO)) {
            return false;
        }

        ProductDTO productDTO = (ProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", discountPercent=" + getDiscountPercent() +
            ", newPrice=" + getNewPrice() +
            ", instructions='" + getInstructions() + "'" +
            ", netQuantity=" + getNetQuantity() +
            ", netQuantityUnit='" + getNetQuantityUnit() + "'" +
            ", imageUrls='" + getImageUrls() + "'" +
            ", ingredients='" + getIngredients() + "'" +
            "}";
    }
}
