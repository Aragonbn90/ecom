package com.ed.ecom.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Float price;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "discount_percent")
    private Float discountPercent;

    @Column(name = "new_price")
    private Float newPrice;

    @Column(name = "instructions")
    private String instructions;

    @Column(name = "net_quantity")
    private Float netQuantity;

    @Column(name = "net_quantity_unit")
    private String netQuantityUnit;

    @Column(name = "image_urls")
    private String imageUrls;

    @Column(name = "ingredients")
    private String ingredients;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return this.price;
    }

    public Product price(Float price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getDiscountPercent() {
        return this.discountPercent;
    }

    public Product discountPercent(Float discountPercent) {
        this.setDiscountPercent(discountPercent);
        return this;
    }

    public void setDiscountPercent(Float discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Float getNewPrice() {
        return this.newPrice;
    }

    public Product newPrice(Float newPrice) {
        this.setNewPrice(newPrice);
        return this;
    }

    public void setNewPrice(Float newPrice) {
        this.newPrice = newPrice;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public Product instructions(String instructions) {
        this.setInstructions(instructions);
        return this;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Float getNetQuantity() {
        return this.netQuantity;
    }

    public Product netQuantity(Float netQuantity) {
        this.setNetQuantity(netQuantity);
        return this;
    }

    public void setNetQuantity(Float netQuantity) {
        this.netQuantity = netQuantity;
    }

    public String getNetQuantityUnit() {
        return this.netQuantityUnit;
    }

    public Product netQuantityUnit(String netQuantityUnit) {
        this.setNetQuantityUnit(netQuantityUnit);
        return this;
    }

    public void setNetQuantityUnit(String netQuantityUnit) {
        this.netQuantityUnit = netQuantityUnit;
    }

    public String getImageUrls() {
        return this.imageUrls;
    }

    public Product imageUrls(String imageUrls) {
        this.setImageUrls(imageUrls);
        return this;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getIngredients() {
        return this.ingredients;
    }

    public Product ingredients(String ingredients) {
        this.setIngredients(ingredients);
        return this;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
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
