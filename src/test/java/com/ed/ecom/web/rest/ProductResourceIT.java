package com.ed.ecom.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ed.ecom.IntegrationTest;
import com.ed.ecom.domain.Product;
import com.ed.ecom.repository.ProductRepository;
import com.ed.ecom.service.criteria.ProductCriteria;
import com.ed.ecom.service.dto.ProductDTO;
import com.ed.ecom.service.mapper.ProductMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;
    private static final Float SMALLER_PRICE = 1F - 1F;

    private static final Float DEFAULT_DISCOUNT_PERCENT = 0F;
    private static final Float UPDATED_DISCOUNT_PERCENT = 1F;
    private static final Float SMALLER_DISCOUNT_PERCENT = 0F - 1F;

    private static final Float DEFAULT_NEW_PRICE = 1F;
    private static final Float UPDATED_NEW_PRICE = 2F;
    private static final Float SMALLER_NEW_PRICE = 1F - 1F;

    private static final String DEFAULT_INSTRUCTIONS = "AAAAAAAAAA";
    private static final String UPDATED_INSTRUCTIONS = "BBBBBBBBBB";

    private static final Float DEFAULT_NET_QUANTITY = 1F;
    private static final Float UPDATED_NET_QUANTITY = 2F;
    private static final Float SMALLER_NET_QUANTITY = 1F - 1F;

    private static final String DEFAULT_NET_QUANTITY_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_NET_QUANTITY_UNIT = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URLS = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URLS = "BBBBBBBBBB";

    private static final String DEFAULT_INGREDIENTS = "AAAAAAAAAA";
    private static final String UPDATED_INGREDIENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductMockMvc;

    private Product product;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createEntity(EntityManager em) {
        Product product = new Product()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .price(DEFAULT_PRICE)
            .discountPercent(DEFAULT_DISCOUNT_PERCENT)
            .newPrice(DEFAULT_NEW_PRICE)
            .instructions(DEFAULT_INSTRUCTIONS)
            .netQuantity(DEFAULT_NET_QUANTITY)
            .netQuantityUnit(DEFAULT_NET_QUANTITY_UNIT)
            .imageUrls(DEFAULT_IMAGE_URLS)
            .ingredients(DEFAULT_INGREDIENTS);
        return product;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Product createUpdatedEntity(EntityManager em) {
        Product product = new Product()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .newPrice(UPDATED_NEW_PRICE)
            .instructions(UPDATED_INSTRUCTIONS)
            .netQuantity(UPDATED_NET_QUANTITY)
            .netQuantityUnit(UPDATED_NET_QUANTITY_UNIT)
            .imageUrls(UPDATED_IMAGE_URLS)
            .ingredients(UPDATED_INGREDIENTS);
        return product;
    }

    @BeforeEach
    public void initTest() {
        product = createEntity(em);
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        int databaseSizeBeforeCreate = productRepository.findAll().size();
        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isCreated());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testProduct.getDiscountPercent()).isEqualTo(DEFAULT_DISCOUNT_PERCENT);
        assertThat(testProduct.getNewPrice()).isEqualTo(DEFAULT_NEW_PRICE);
        assertThat(testProduct.getInstructions()).isEqualTo(DEFAULT_INSTRUCTIONS);
        assertThat(testProduct.getNetQuantity()).isEqualTo(DEFAULT_NET_QUANTITY);
        assertThat(testProduct.getNetQuantityUnit()).isEqualTo(DEFAULT_NET_QUANTITY_UNIT);
        assertThat(testProduct.getImageUrls()).isEqualTo(DEFAULT_IMAGE_URLS);
        assertThat(testProduct.getIngredients()).isEqualTo(DEFAULT_INGREDIENTS);
    }

    @Test
    @Transactional
    void createProductWithExistingId() throws Exception {
        // Create the Product with an existing ID
        product.setId(1L);
        ProductDTO productDTO = productMapper.toDto(product);

        int databaseSizeBeforeCreate = productRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRepository.findAll().size();
        // set the field null
        product.setName(null);

        // Create the Product, which fails.
        ProductDTO productDTO = productMapper.toDto(product);

        restProductMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isBadRequest());

        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].discountPercent").value(hasItem(DEFAULT_DISCOUNT_PERCENT.doubleValue())))
            .andExpect(jsonPath("$.[*].newPrice").value(hasItem(DEFAULT_NEW_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].instructions").value(hasItem(DEFAULT_INSTRUCTIONS)))
            .andExpect(jsonPath("$.[*].netQuantity").value(hasItem(DEFAULT_NET_QUANTITY.doubleValue())))
            .andExpect(jsonPath("$.[*].netQuantityUnit").value(hasItem(DEFAULT_NET_QUANTITY_UNIT)))
            .andExpect(jsonPath("$.[*].imageUrls").value(hasItem(DEFAULT_IMAGE_URLS)))
            .andExpect(jsonPath("$.[*].ingredients").value(hasItem(DEFAULT_INGREDIENTS)));
    }

    @Test
    @Transactional
    void getProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get the product
        restProductMockMvc
            .perform(get(ENTITY_API_URL_ID, product.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.discountPercent").value(DEFAULT_DISCOUNT_PERCENT.doubleValue()))
            .andExpect(jsonPath("$.newPrice").value(DEFAULT_NEW_PRICE.doubleValue()))
            .andExpect(jsonPath("$.instructions").value(DEFAULT_INSTRUCTIONS))
            .andExpect(jsonPath("$.netQuantity").value(DEFAULT_NET_QUANTITY.doubleValue()))
            .andExpect(jsonPath("$.netQuantityUnit").value(DEFAULT_NET_QUANTITY_UNIT))
            .andExpect(jsonPath("$.imageUrls").value(DEFAULT_IMAGE_URLS))
            .andExpect(jsonPath("$.ingredients").value(DEFAULT_INGREDIENTS));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        Long id = product.getId();

        defaultProductShouldBeFound("id.equals=" + id);
        defaultProductShouldNotBeFound("id.notEquals=" + id);

        defaultProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.greaterThan=" + id);

        defaultProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name equals to DEFAULT_NAME
        defaultProductShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name not equals to DEFAULT_NAME
        defaultProductShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the productList where name not equals to UPDATED_NAME
        defaultProductShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProductShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the productList where name equals to UPDATED_NAME
        defaultProductShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name is not null
        defaultProductShouldBeFound("name.specified=true");

        // Get all the productList where name is null
        defaultProductShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNameContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name contains DEFAULT_NAME
        defaultProductShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the productList where name contains UPDATED_NAME
        defaultProductShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where name does not contain DEFAULT_NAME
        defaultProductShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the productList where name does not contain UPDATED_NAME
        defaultProductShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description equals to DEFAULT_DESCRIPTION
        defaultProductShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description equals to UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description not equals to DEFAULT_DESCRIPTION
        defaultProductShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description not equals to UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the productList where description equals to UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description is not null
        defaultProductShouldBeFound("description.specified=true");

        // Get all the productList where description is null
        defaultProductShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description contains DEFAULT_DESCRIPTION
        defaultProductShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description contains UPDATED_DESCRIPTION
        defaultProductShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where description does not contain DEFAULT_DESCRIPTION
        defaultProductShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the productList where description does not contain UPDATED_DESCRIPTION
        defaultProductShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price equals to DEFAULT_PRICE
        defaultProductShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the productList where price equals to UPDATED_PRICE
        defaultProductShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price not equals to DEFAULT_PRICE
        defaultProductShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);

        // Get all the productList where price not equals to UPDATED_PRICE
        defaultProductShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultProductShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the productList where price equals to UPDATED_PRICE
        defaultProductShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is not null
        defaultProductShouldBeFound("price.specified=true");

        // Get all the productList where price is null
        defaultProductShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is greater than or equal to DEFAULT_PRICE
        defaultProductShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the productList where price is greater than or equal to UPDATED_PRICE
        defaultProductShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is less than or equal to DEFAULT_PRICE
        defaultProductShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the productList where price is less than or equal to SMALLER_PRICE
        defaultProductShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is less than DEFAULT_PRICE
        defaultProductShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the productList where price is less than UPDATED_PRICE
        defaultProductShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where price is greater than DEFAULT_PRICE
        defaultProductShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the productList where price is greater than SMALLER_PRICE
        defaultProductShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByDiscountPercentIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discountPercent equals to DEFAULT_DISCOUNT_PERCENT
        defaultProductShouldBeFound("discountPercent.equals=" + DEFAULT_DISCOUNT_PERCENT);

        // Get all the productList where discountPercent equals to UPDATED_DISCOUNT_PERCENT
        defaultProductShouldNotBeFound("discountPercent.equals=" + UPDATED_DISCOUNT_PERCENT);
    }

    @Test
    @Transactional
    void getAllProductsByDiscountPercentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discountPercent not equals to DEFAULT_DISCOUNT_PERCENT
        defaultProductShouldNotBeFound("discountPercent.notEquals=" + DEFAULT_DISCOUNT_PERCENT);

        // Get all the productList where discountPercent not equals to UPDATED_DISCOUNT_PERCENT
        defaultProductShouldBeFound("discountPercent.notEquals=" + UPDATED_DISCOUNT_PERCENT);
    }

    @Test
    @Transactional
    void getAllProductsByDiscountPercentIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discountPercent in DEFAULT_DISCOUNT_PERCENT or UPDATED_DISCOUNT_PERCENT
        defaultProductShouldBeFound("discountPercent.in=" + DEFAULT_DISCOUNT_PERCENT + "," + UPDATED_DISCOUNT_PERCENT);

        // Get all the productList where discountPercent equals to UPDATED_DISCOUNT_PERCENT
        defaultProductShouldNotBeFound("discountPercent.in=" + UPDATED_DISCOUNT_PERCENT);
    }

    @Test
    @Transactional
    void getAllProductsByDiscountPercentIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discountPercent is not null
        defaultProductShouldBeFound("discountPercent.specified=true");

        // Get all the productList where discountPercent is null
        defaultProductShouldNotBeFound("discountPercent.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByDiscountPercentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discountPercent is greater than or equal to DEFAULT_DISCOUNT_PERCENT
        defaultProductShouldBeFound("discountPercent.greaterThanOrEqual=" + DEFAULT_DISCOUNT_PERCENT);

        // Get all the productList where discountPercent is greater than or equal to (DEFAULT_DISCOUNT_PERCENT + 1)
        defaultProductShouldNotBeFound("discountPercent.greaterThanOrEqual=" + (DEFAULT_DISCOUNT_PERCENT + 1));
    }

    @Test
    @Transactional
    void getAllProductsByDiscountPercentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discountPercent is less than or equal to DEFAULT_DISCOUNT_PERCENT
        defaultProductShouldBeFound("discountPercent.lessThanOrEqual=" + DEFAULT_DISCOUNT_PERCENT);

        // Get all the productList where discountPercent is less than or equal to SMALLER_DISCOUNT_PERCENT
        defaultProductShouldNotBeFound("discountPercent.lessThanOrEqual=" + SMALLER_DISCOUNT_PERCENT);
    }

    @Test
    @Transactional
    void getAllProductsByDiscountPercentIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discountPercent is less than DEFAULT_DISCOUNT_PERCENT
        defaultProductShouldNotBeFound("discountPercent.lessThan=" + DEFAULT_DISCOUNT_PERCENT);

        // Get all the productList where discountPercent is less than (DEFAULT_DISCOUNT_PERCENT + 1)
        defaultProductShouldBeFound("discountPercent.lessThan=" + (DEFAULT_DISCOUNT_PERCENT + 1));
    }

    @Test
    @Transactional
    void getAllProductsByDiscountPercentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where discountPercent is greater than DEFAULT_DISCOUNT_PERCENT
        defaultProductShouldNotBeFound("discountPercent.greaterThan=" + DEFAULT_DISCOUNT_PERCENT);

        // Get all the productList where discountPercent is greater than SMALLER_DISCOUNT_PERCENT
        defaultProductShouldBeFound("discountPercent.greaterThan=" + SMALLER_DISCOUNT_PERCENT);
    }

    @Test
    @Transactional
    void getAllProductsByNewPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where newPrice equals to DEFAULT_NEW_PRICE
        defaultProductShouldBeFound("newPrice.equals=" + DEFAULT_NEW_PRICE);

        // Get all the productList where newPrice equals to UPDATED_NEW_PRICE
        defaultProductShouldNotBeFound("newPrice.equals=" + UPDATED_NEW_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByNewPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where newPrice not equals to DEFAULT_NEW_PRICE
        defaultProductShouldNotBeFound("newPrice.notEquals=" + DEFAULT_NEW_PRICE);

        // Get all the productList where newPrice not equals to UPDATED_NEW_PRICE
        defaultProductShouldBeFound("newPrice.notEquals=" + UPDATED_NEW_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByNewPriceIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where newPrice in DEFAULT_NEW_PRICE or UPDATED_NEW_PRICE
        defaultProductShouldBeFound("newPrice.in=" + DEFAULT_NEW_PRICE + "," + UPDATED_NEW_PRICE);

        // Get all the productList where newPrice equals to UPDATED_NEW_PRICE
        defaultProductShouldNotBeFound("newPrice.in=" + UPDATED_NEW_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByNewPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where newPrice is not null
        defaultProductShouldBeFound("newPrice.specified=true");

        // Get all the productList where newPrice is null
        defaultProductShouldNotBeFound("newPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNewPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where newPrice is greater than or equal to DEFAULT_NEW_PRICE
        defaultProductShouldBeFound("newPrice.greaterThanOrEqual=" + DEFAULT_NEW_PRICE);

        // Get all the productList where newPrice is greater than or equal to UPDATED_NEW_PRICE
        defaultProductShouldNotBeFound("newPrice.greaterThanOrEqual=" + UPDATED_NEW_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByNewPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where newPrice is less than or equal to DEFAULT_NEW_PRICE
        defaultProductShouldBeFound("newPrice.lessThanOrEqual=" + DEFAULT_NEW_PRICE);

        // Get all the productList where newPrice is less than or equal to SMALLER_NEW_PRICE
        defaultProductShouldNotBeFound("newPrice.lessThanOrEqual=" + SMALLER_NEW_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByNewPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where newPrice is less than DEFAULT_NEW_PRICE
        defaultProductShouldNotBeFound("newPrice.lessThan=" + DEFAULT_NEW_PRICE);

        // Get all the productList where newPrice is less than UPDATED_NEW_PRICE
        defaultProductShouldBeFound("newPrice.lessThan=" + UPDATED_NEW_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByNewPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where newPrice is greater than DEFAULT_NEW_PRICE
        defaultProductShouldNotBeFound("newPrice.greaterThan=" + DEFAULT_NEW_PRICE);

        // Get all the productList where newPrice is greater than SMALLER_NEW_PRICE
        defaultProductShouldBeFound("newPrice.greaterThan=" + SMALLER_NEW_PRICE);
    }

    @Test
    @Transactional
    void getAllProductsByInstructionsIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where instructions equals to DEFAULT_INSTRUCTIONS
        defaultProductShouldBeFound("instructions.equals=" + DEFAULT_INSTRUCTIONS);

        // Get all the productList where instructions equals to UPDATED_INSTRUCTIONS
        defaultProductShouldNotBeFound("instructions.equals=" + UPDATED_INSTRUCTIONS);
    }

    @Test
    @Transactional
    void getAllProductsByInstructionsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where instructions not equals to DEFAULT_INSTRUCTIONS
        defaultProductShouldNotBeFound("instructions.notEquals=" + DEFAULT_INSTRUCTIONS);

        // Get all the productList where instructions not equals to UPDATED_INSTRUCTIONS
        defaultProductShouldBeFound("instructions.notEquals=" + UPDATED_INSTRUCTIONS);
    }

    @Test
    @Transactional
    void getAllProductsByInstructionsIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where instructions in DEFAULT_INSTRUCTIONS or UPDATED_INSTRUCTIONS
        defaultProductShouldBeFound("instructions.in=" + DEFAULT_INSTRUCTIONS + "," + UPDATED_INSTRUCTIONS);

        // Get all the productList where instructions equals to UPDATED_INSTRUCTIONS
        defaultProductShouldNotBeFound("instructions.in=" + UPDATED_INSTRUCTIONS);
    }

    @Test
    @Transactional
    void getAllProductsByInstructionsIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where instructions is not null
        defaultProductShouldBeFound("instructions.specified=true");

        // Get all the productList where instructions is null
        defaultProductShouldNotBeFound("instructions.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByInstructionsContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where instructions contains DEFAULT_INSTRUCTIONS
        defaultProductShouldBeFound("instructions.contains=" + DEFAULT_INSTRUCTIONS);

        // Get all the productList where instructions contains UPDATED_INSTRUCTIONS
        defaultProductShouldNotBeFound("instructions.contains=" + UPDATED_INSTRUCTIONS);
    }

    @Test
    @Transactional
    void getAllProductsByInstructionsNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where instructions does not contain DEFAULT_INSTRUCTIONS
        defaultProductShouldNotBeFound("instructions.doesNotContain=" + DEFAULT_INSTRUCTIONS);

        // Get all the productList where instructions does not contain UPDATED_INSTRUCTIONS
        defaultProductShouldBeFound("instructions.doesNotContain=" + UPDATED_INSTRUCTIONS);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantity equals to DEFAULT_NET_QUANTITY
        defaultProductShouldBeFound("netQuantity.equals=" + DEFAULT_NET_QUANTITY);

        // Get all the productList where netQuantity equals to UPDATED_NET_QUANTITY
        defaultProductShouldNotBeFound("netQuantity.equals=" + UPDATED_NET_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantity not equals to DEFAULT_NET_QUANTITY
        defaultProductShouldNotBeFound("netQuantity.notEquals=" + DEFAULT_NET_QUANTITY);

        // Get all the productList where netQuantity not equals to UPDATED_NET_QUANTITY
        defaultProductShouldBeFound("netQuantity.notEquals=" + UPDATED_NET_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantity in DEFAULT_NET_QUANTITY or UPDATED_NET_QUANTITY
        defaultProductShouldBeFound("netQuantity.in=" + DEFAULT_NET_QUANTITY + "," + UPDATED_NET_QUANTITY);

        // Get all the productList where netQuantity equals to UPDATED_NET_QUANTITY
        defaultProductShouldNotBeFound("netQuantity.in=" + UPDATED_NET_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantity is not null
        defaultProductShouldBeFound("netQuantity.specified=true");

        // Get all the productList where netQuantity is null
        defaultProductShouldNotBeFound("netQuantity.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantity is greater than or equal to DEFAULT_NET_QUANTITY
        defaultProductShouldBeFound("netQuantity.greaterThanOrEqual=" + DEFAULT_NET_QUANTITY);

        // Get all the productList where netQuantity is greater than or equal to UPDATED_NET_QUANTITY
        defaultProductShouldNotBeFound("netQuantity.greaterThanOrEqual=" + UPDATED_NET_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantity is less than or equal to DEFAULT_NET_QUANTITY
        defaultProductShouldBeFound("netQuantity.lessThanOrEqual=" + DEFAULT_NET_QUANTITY);

        // Get all the productList where netQuantity is less than or equal to SMALLER_NET_QUANTITY
        defaultProductShouldNotBeFound("netQuantity.lessThanOrEqual=" + SMALLER_NET_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantity is less than DEFAULT_NET_QUANTITY
        defaultProductShouldNotBeFound("netQuantity.lessThan=" + DEFAULT_NET_QUANTITY);

        // Get all the productList where netQuantity is less than UPDATED_NET_QUANTITY
        defaultProductShouldBeFound("netQuantity.lessThan=" + UPDATED_NET_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantity is greater than DEFAULT_NET_QUANTITY
        defaultProductShouldNotBeFound("netQuantity.greaterThan=" + DEFAULT_NET_QUANTITY);

        // Get all the productList where netQuantity is greater than SMALLER_NET_QUANTITY
        defaultProductShouldBeFound("netQuantity.greaterThan=" + SMALLER_NET_QUANTITY);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantityUnit equals to DEFAULT_NET_QUANTITY_UNIT
        defaultProductShouldBeFound("netQuantityUnit.equals=" + DEFAULT_NET_QUANTITY_UNIT);

        // Get all the productList where netQuantityUnit equals to UPDATED_NET_QUANTITY_UNIT
        defaultProductShouldNotBeFound("netQuantityUnit.equals=" + UPDATED_NET_QUANTITY_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityUnitIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantityUnit not equals to DEFAULT_NET_QUANTITY_UNIT
        defaultProductShouldNotBeFound("netQuantityUnit.notEquals=" + DEFAULT_NET_QUANTITY_UNIT);

        // Get all the productList where netQuantityUnit not equals to UPDATED_NET_QUANTITY_UNIT
        defaultProductShouldBeFound("netQuantityUnit.notEquals=" + UPDATED_NET_QUANTITY_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityUnitIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantityUnit in DEFAULT_NET_QUANTITY_UNIT or UPDATED_NET_QUANTITY_UNIT
        defaultProductShouldBeFound("netQuantityUnit.in=" + DEFAULT_NET_QUANTITY_UNIT + "," + UPDATED_NET_QUANTITY_UNIT);

        // Get all the productList where netQuantityUnit equals to UPDATED_NET_QUANTITY_UNIT
        defaultProductShouldNotBeFound("netQuantityUnit.in=" + UPDATED_NET_QUANTITY_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantityUnit is not null
        defaultProductShouldBeFound("netQuantityUnit.specified=true");

        // Get all the productList where netQuantityUnit is null
        defaultProductShouldNotBeFound("netQuantityUnit.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityUnitContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantityUnit contains DEFAULT_NET_QUANTITY_UNIT
        defaultProductShouldBeFound("netQuantityUnit.contains=" + DEFAULT_NET_QUANTITY_UNIT);

        // Get all the productList where netQuantityUnit contains UPDATED_NET_QUANTITY_UNIT
        defaultProductShouldNotBeFound("netQuantityUnit.contains=" + UPDATED_NET_QUANTITY_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByNetQuantityUnitNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where netQuantityUnit does not contain DEFAULT_NET_QUANTITY_UNIT
        defaultProductShouldNotBeFound("netQuantityUnit.doesNotContain=" + DEFAULT_NET_QUANTITY_UNIT);

        // Get all the productList where netQuantityUnit does not contain UPDATED_NET_QUANTITY_UNIT
        defaultProductShouldBeFound("netQuantityUnit.doesNotContain=" + UPDATED_NET_QUANTITY_UNIT);
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlsIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrls equals to DEFAULT_IMAGE_URLS
        defaultProductShouldBeFound("imageUrls.equals=" + DEFAULT_IMAGE_URLS);

        // Get all the productList where imageUrls equals to UPDATED_IMAGE_URLS
        defaultProductShouldNotBeFound("imageUrls.equals=" + UPDATED_IMAGE_URLS);
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrls not equals to DEFAULT_IMAGE_URLS
        defaultProductShouldNotBeFound("imageUrls.notEquals=" + DEFAULT_IMAGE_URLS);

        // Get all the productList where imageUrls not equals to UPDATED_IMAGE_URLS
        defaultProductShouldBeFound("imageUrls.notEquals=" + UPDATED_IMAGE_URLS);
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlsIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrls in DEFAULT_IMAGE_URLS or UPDATED_IMAGE_URLS
        defaultProductShouldBeFound("imageUrls.in=" + DEFAULT_IMAGE_URLS + "," + UPDATED_IMAGE_URLS);

        // Get all the productList where imageUrls equals to UPDATED_IMAGE_URLS
        defaultProductShouldNotBeFound("imageUrls.in=" + UPDATED_IMAGE_URLS);
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlsIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrls is not null
        defaultProductShouldBeFound("imageUrls.specified=true");

        // Get all the productList where imageUrls is null
        defaultProductShouldNotBeFound("imageUrls.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlsContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrls contains DEFAULT_IMAGE_URLS
        defaultProductShouldBeFound("imageUrls.contains=" + DEFAULT_IMAGE_URLS);

        // Get all the productList where imageUrls contains UPDATED_IMAGE_URLS
        defaultProductShouldNotBeFound("imageUrls.contains=" + UPDATED_IMAGE_URLS);
    }

    @Test
    @Transactional
    void getAllProductsByImageUrlsNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where imageUrls does not contain DEFAULT_IMAGE_URLS
        defaultProductShouldNotBeFound("imageUrls.doesNotContain=" + DEFAULT_IMAGE_URLS);

        // Get all the productList where imageUrls does not contain UPDATED_IMAGE_URLS
        defaultProductShouldBeFound("imageUrls.doesNotContain=" + UPDATED_IMAGE_URLS);
    }

    @Test
    @Transactional
    void getAllProductsByIngredientsIsEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where ingredients equals to DEFAULT_INGREDIENTS
        defaultProductShouldBeFound("ingredients.equals=" + DEFAULT_INGREDIENTS);

        // Get all the productList where ingredients equals to UPDATED_INGREDIENTS
        defaultProductShouldNotBeFound("ingredients.equals=" + UPDATED_INGREDIENTS);
    }

    @Test
    @Transactional
    void getAllProductsByIngredientsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where ingredients not equals to DEFAULT_INGREDIENTS
        defaultProductShouldNotBeFound("ingredients.notEquals=" + DEFAULT_INGREDIENTS);

        // Get all the productList where ingredients not equals to UPDATED_INGREDIENTS
        defaultProductShouldBeFound("ingredients.notEquals=" + UPDATED_INGREDIENTS);
    }

    @Test
    @Transactional
    void getAllProductsByIngredientsIsInShouldWork() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where ingredients in DEFAULT_INGREDIENTS or UPDATED_INGREDIENTS
        defaultProductShouldBeFound("ingredients.in=" + DEFAULT_INGREDIENTS + "," + UPDATED_INGREDIENTS);

        // Get all the productList where ingredients equals to UPDATED_INGREDIENTS
        defaultProductShouldNotBeFound("ingredients.in=" + UPDATED_INGREDIENTS);
    }

    @Test
    @Transactional
    void getAllProductsByIngredientsIsNullOrNotNull() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where ingredients is not null
        defaultProductShouldBeFound("ingredients.specified=true");

        // Get all the productList where ingredients is null
        defaultProductShouldNotBeFound("ingredients.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByIngredientsContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where ingredients contains DEFAULT_INGREDIENTS
        defaultProductShouldBeFound("ingredients.contains=" + DEFAULT_INGREDIENTS);

        // Get all the productList where ingredients contains UPDATED_INGREDIENTS
        defaultProductShouldNotBeFound("ingredients.contains=" + UPDATED_INGREDIENTS);
    }

    @Test
    @Transactional
    void getAllProductsByIngredientsNotContainsSomething() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        // Get all the productList where ingredients does not contain DEFAULT_INGREDIENTS
        defaultProductShouldNotBeFound("ingredients.doesNotContain=" + DEFAULT_INGREDIENTS);

        // Get all the productList where ingredients does not contain UPDATED_INGREDIENTS
        defaultProductShouldBeFound("ingredients.doesNotContain=" + UPDATED_INGREDIENTS);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductShouldBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].discountPercent").value(hasItem(DEFAULT_DISCOUNT_PERCENT.doubleValue())))
            .andExpect(jsonPath("$.[*].newPrice").value(hasItem(DEFAULT_NEW_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].instructions").value(hasItem(DEFAULT_INSTRUCTIONS)))
            .andExpect(jsonPath("$.[*].netQuantity").value(hasItem(DEFAULT_NET_QUANTITY.doubleValue())))
            .andExpect(jsonPath("$.[*].netQuantityUnit").value(hasItem(DEFAULT_NET_QUANTITY_UNIT)))
            .andExpect(jsonPath("$.[*].imageUrls").value(hasItem(DEFAULT_IMAGE_URLS)))
            .andExpect(jsonPath("$.[*].ingredients").value(hasItem(DEFAULT_INGREDIENTS)));

        // Check, that the count call also returns 1
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductShouldNotBeFound(String filter) throws Exception {
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduct() throws Exception {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product
        Product updatedProduct = productRepository.findById(product.getId()).get();
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct);
        updatedProduct
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .newPrice(UPDATED_NEW_PRICE)
            .instructions(UPDATED_INSTRUCTIONS)
            .netQuantity(UPDATED_NET_QUANTITY)
            .netQuantityUnit(UPDATED_NET_QUANTITY_UNIT)
            .imageUrls(UPDATED_IMAGE_URLS)
            .ingredients(UPDATED_INGREDIENTS);
        ProductDTO productDTO = productMapper.toDto(updatedProduct);

        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProduct.getDiscountPercent()).isEqualTo(UPDATED_DISCOUNT_PERCENT);
        assertThat(testProduct.getNewPrice()).isEqualTo(UPDATED_NEW_PRICE);
        assertThat(testProduct.getInstructions()).isEqualTo(UPDATED_INSTRUCTIONS);
        assertThat(testProduct.getNetQuantity()).isEqualTo(UPDATED_NET_QUANTITY);
        assertThat(testProduct.getNetQuantityUnit()).isEqualTo(UPDATED_NET_QUANTITY_UNIT);
        assertThat(testProduct.getImageUrls()).isEqualTo(UPDATED_IMAGE_URLS);
        assertThat(testProduct.getIngredients()).isEqualTo(UPDATED_INGREDIENTS);
    }

    @Test
    @Transactional
    void putNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct.newPrice(UPDATED_NEW_PRICE).instructions(UPDATED_INSTRUCTIONS);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduct.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testProduct.getDiscountPercent()).isEqualTo(DEFAULT_DISCOUNT_PERCENT);
        assertThat(testProduct.getNewPrice()).isEqualTo(UPDATED_NEW_PRICE);
        assertThat(testProduct.getInstructions()).isEqualTo(UPDATED_INSTRUCTIONS);
        assertThat(testProduct.getNetQuantity()).isEqualTo(DEFAULT_NET_QUANTITY);
        assertThat(testProduct.getNetQuantityUnit()).isEqualTo(DEFAULT_NET_QUANTITY_UNIT);
        assertThat(testProduct.getImageUrls()).isEqualTo(DEFAULT_IMAGE_URLS);
        assertThat(testProduct.getIngredients()).isEqualTo(DEFAULT_INGREDIENTS);
    }

    @Test
    @Transactional
    void fullUpdateProductWithPatch() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeUpdate = productRepository.findAll().size();

        // Update the product using partial update
        Product partialUpdatedProduct = new Product();
        partialUpdatedProduct.setId(product.getId());

        partialUpdatedProduct
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .price(UPDATED_PRICE)
            .discountPercent(UPDATED_DISCOUNT_PERCENT)
            .newPrice(UPDATED_NEW_PRICE)
            .instructions(UPDATED_INSTRUCTIONS)
            .netQuantity(UPDATED_NET_QUANTITY)
            .netQuantityUnit(UPDATED_NET_QUANTITY_UNIT)
            .imageUrls(UPDATED_IMAGE_URLS)
            .ingredients(UPDATED_INGREDIENTS);

        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduct))
            )
            .andExpect(status().isOk());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
        Product testProduct = productList.get(productList.size() - 1);
        assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduct.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProduct.getDiscountPercent()).isEqualTo(UPDATED_DISCOUNT_PERCENT);
        assertThat(testProduct.getNewPrice()).isEqualTo(UPDATED_NEW_PRICE);
        assertThat(testProduct.getInstructions()).isEqualTo(UPDATED_INSTRUCTIONS);
        assertThat(testProduct.getNetQuantity()).isEqualTo(UPDATED_NET_QUANTITY);
        assertThat(testProduct.getNetQuantityUnit()).isEqualTo(UPDATED_NET_QUANTITY_UNIT);
        assertThat(testProduct.getImageUrls()).isEqualTo(UPDATED_IMAGE_URLS);
        assertThat(testProduct.getIngredients()).isEqualTo(UPDATED_INGREDIENTS);
    }

    @Test
    @Transactional
    void patchNonExistingProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduct() throws Exception {
        int databaseSizeBeforeUpdate = productRepository.findAll().size();
        product.setId(count.incrementAndGet());

        // Create the Product
        ProductDTO productDTO = productMapper.toDto(product);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(productDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Product in the database
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduct() throws Exception {
        // Initialize the database
        productRepository.saveAndFlush(product);

        int databaseSizeBeforeDelete = productRepository.findAll().size();

        // Delete the product
        restProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, product.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Product> productList = productRepository.findAll();
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
