package com.ed.ecom.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ed.ecom.IntegrationTest;
import com.ed.ecom.domain.Order;
import com.ed.ecom.domain.OrderProduct;
import com.ed.ecom.domain.Product;
import com.ed.ecom.repository.OrderProductRepository;
import com.ed.ecom.service.criteria.OrderProductCriteria;
import com.ed.ecom.service.dto.OrderProductDTO;
import com.ed.ecom.service.mapper.OrderProductMapper;
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
 * Integration tests for the {@link OrderProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderProductResourceIT {

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;
    private static final Float SMALLER_PRICE = 1F - 1F;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;
    private static final Integer SMALLER_QUANTITY = 1 - 1;

    private static final Float DEFAULT_TOTAL = 1F;
    private static final Float UPDATED_TOTAL = 2F;
    private static final Float SMALLER_TOTAL = 1F - 1F;

    private static final String ENTITY_API_URL = "/api/order-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderProductMockMvc;

    private OrderProduct orderProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderProduct createEntity(EntityManager em) {
        OrderProduct orderProduct = new OrderProduct().price(DEFAULT_PRICE).quantity(DEFAULT_QUANTITY).total(DEFAULT_TOTAL);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        orderProduct.setOrder(order);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        orderProduct.setProduct(product);
        return orderProduct;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderProduct createUpdatedEntity(EntityManager em) {
        OrderProduct orderProduct = new OrderProduct().price(UPDATED_PRICE).quantity(UPDATED_QUANTITY).total(UPDATED_TOTAL);
        // Add required entity
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createUpdatedEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        orderProduct.setOrder(order);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createUpdatedEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        orderProduct.setProduct(product);
        return orderProduct;
    }

    @BeforeEach
    public void initTest() {
        orderProduct = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderProduct() throws Exception {
        int databaseSizeBeforeCreate = orderProductRepository.findAll().size();
        // Create the OrderProduct
        OrderProductDTO orderProductDTO = orderProductMapper.toDto(orderProduct);
        restOrderProductMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderProductDTO))
            )
            .andExpect(status().isCreated());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeCreate + 1);
        OrderProduct testOrderProduct = orderProductList.get(orderProductList.size() - 1);
        assertThat(testOrderProduct.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testOrderProduct.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testOrderProduct.getTotal()).isEqualTo(DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void createOrderProductWithExistingId() throws Exception {
        // Create the OrderProduct with an existing ID
        orderProduct.setId(1L);
        OrderProductDTO orderProductDTO = orderProductMapper.toDto(orderProduct);

        int databaseSizeBeforeCreate = orderProductRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderProductMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderProductRepository.findAll().size();
        // set the field null
        orderProduct.setQuantity(null);

        // Create the OrderProduct, which fails.
        OrderProductDTO orderProductDTO = orderProductMapper.toDto(orderProduct);

        restOrderProductMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderProductDTO))
            )
            .andExpect(status().isBadRequest());

        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrderProducts() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList
        restOrderProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())));
    }

    @Test
    @Transactional
    void getOrderProduct() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get the orderProduct
        restOrderProductMockMvc
            .perform(get(ENTITY_API_URL_ID, orderProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderProduct.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.doubleValue()));
    }

    @Test
    @Transactional
    void getOrderProductsByIdFiltering() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        Long id = orderProduct.getId();

        defaultOrderProductShouldBeFound("id.equals=" + id);
        defaultOrderProductShouldNotBeFound("id.notEquals=" + id);

        defaultOrderProductShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderProductShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderProductShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderProductShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrderProductsByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where price equals to DEFAULT_PRICE
        defaultOrderProductShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the orderProductList where price equals to UPDATED_PRICE
        defaultOrderProductShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderProductsByPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where price not equals to DEFAULT_PRICE
        defaultOrderProductShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);

        // Get all the orderProductList where price not equals to UPDATED_PRICE
        defaultOrderProductShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderProductsByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultOrderProductShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the orderProductList where price equals to UPDATED_PRICE
        defaultOrderProductShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderProductsByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where price is not null
        defaultOrderProductShouldBeFound("price.specified=true");

        // Get all the orderProductList where price is null
        defaultOrderProductShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderProductsByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where price is greater than or equal to DEFAULT_PRICE
        defaultOrderProductShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the orderProductList where price is greater than or equal to UPDATED_PRICE
        defaultOrderProductShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderProductsByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where price is less than or equal to DEFAULT_PRICE
        defaultOrderProductShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the orderProductList where price is less than or equal to SMALLER_PRICE
        defaultOrderProductShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderProductsByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where price is less than DEFAULT_PRICE
        defaultOrderProductShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the orderProductList where price is less than UPDATED_PRICE
        defaultOrderProductShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderProductsByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where price is greater than DEFAULT_PRICE
        defaultOrderProductShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the orderProductList where price is greater than SMALLER_PRICE
        defaultOrderProductShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    void getAllOrderProductsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where quantity equals to DEFAULT_QUANTITY
        defaultOrderProductShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the orderProductList where quantity equals to UPDATED_QUANTITY
        defaultOrderProductShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderProductsByQuantityIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where quantity not equals to DEFAULT_QUANTITY
        defaultOrderProductShouldNotBeFound("quantity.notEquals=" + DEFAULT_QUANTITY);

        // Get all the orderProductList where quantity not equals to UPDATED_QUANTITY
        defaultOrderProductShouldBeFound("quantity.notEquals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderProductsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultOrderProductShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the orderProductList where quantity equals to UPDATED_QUANTITY
        defaultOrderProductShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderProductsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where quantity is not null
        defaultOrderProductShouldBeFound("quantity.specified=true");

        // Get all the orderProductList where quantity is null
        defaultOrderProductShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderProductsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultOrderProductShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the orderProductList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultOrderProductShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderProductsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultOrderProductShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the orderProductList where quantity is less than or equal to SMALLER_QUANTITY
        defaultOrderProductShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderProductsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where quantity is less than DEFAULT_QUANTITY
        defaultOrderProductShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the orderProductList where quantity is less than UPDATED_QUANTITY
        defaultOrderProductShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderProductsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where quantity is greater than DEFAULT_QUANTITY
        defaultOrderProductShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the orderProductList where quantity is greater than SMALLER_QUANTITY
        defaultOrderProductShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllOrderProductsByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where total equals to DEFAULT_TOTAL
        defaultOrderProductShouldBeFound("total.equals=" + DEFAULT_TOTAL);

        // Get all the orderProductList where total equals to UPDATED_TOTAL
        defaultOrderProductShouldNotBeFound("total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrderProductsByTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where total not equals to DEFAULT_TOTAL
        defaultOrderProductShouldNotBeFound("total.notEquals=" + DEFAULT_TOTAL);

        // Get all the orderProductList where total not equals to UPDATED_TOTAL
        defaultOrderProductShouldBeFound("total.notEquals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrderProductsByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where total in DEFAULT_TOTAL or UPDATED_TOTAL
        defaultOrderProductShouldBeFound("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL);

        // Get all the orderProductList where total equals to UPDATED_TOTAL
        defaultOrderProductShouldNotBeFound("total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrderProductsByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where total is not null
        defaultOrderProductShouldBeFound("total.specified=true");

        // Get all the orderProductList where total is null
        defaultOrderProductShouldNotBeFound("total.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderProductsByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where total is greater than or equal to DEFAULT_TOTAL
        defaultOrderProductShouldBeFound("total.greaterThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the orderProductList where total is greater than or equal to UPDATED_TOTAL
        defaultOrderProductShouldNotBeFound("total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrderProductsByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where total is less than or equal to DEFAULT_TOTAL
        defaultOrderProductShouldBeFound("total.lessThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the orderProductList where total is less than or equal to SMALLER_TOTAL
        defaultOrderProductShouldNotBeFound("total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrderProductsByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where total is less than DEFAULT_TOTAL
        defaultOrderProductShouldNotBeFound("total.lessThan=" + DEFAULT_TOTAL);

        // Get all the orderProductList where total is less than UPDATED_TOTAL
        defaultOrderProductShouldBeFound("total.lessThan=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrderProductsByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        // Get all the orderProductList where total is greater than DEFAULT_TOTAL
        defaultOrderProductShouldNotBeFound("total.greaterThan=" + DEFAULT_TOTAL);

        // Get all the orderProductList where total is greater than SMALLER_TOTAL
        defaultOrderProductShouldBeFound("total.greaterThan=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrderProductsByOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);
        Order order;
        if (TestUtil.findAll(em, Order.class).isEmpty()) {
            order = OrderResourceIT.createEntity(em);
            em.persist(order);
            em.flush();
        } else {
            order = TestUtil.findAll(em, Order.class).get(0);
        }
        em.persist(order);
        em.flush();
        orderProduct.setOrder(order);
        orderProductRepository.saveAndFlush(orderProduct);
        Long orderId = order.getId();

        // Get all the orderProductList where order equals to orderId
        defaultOrderProductShouldBeFound("orderId.equals=" + orderId);

        // Get all the orderProductList where order equals to (orderId + 1)
        defaultOrderProductShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }

    @Test
    @Transactional
    void getAllOrderProductsByProductIsEqualToSomething() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        em.persist(product);
        em.flush();
        orderProduct.setProduct(product);
        orderProductRepository.saveAndFlush(orderProduct);
        Long productId = product.getId();

        // Get all the orderProductList where product equals to productId
        defaultOrderProductShouldBeFound("productId.equals=" + productId);

        // Get all the orderProductList where product equals to (productId + 1)
        defaultOrderProductShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderProductShouldBeFound(String filter) throws Exception {
        restOrderProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderProduct.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())));

        // Check, that the count call also returns 1
        restOrderProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderProductShouldNotBeFound(String filter) throws Exception {
        restOrderProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderProductMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrderProduct() throws Exception {
        // Get the orderProduct
        restOrderProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrderProduct() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();

        // Update the orderProduct
        OrderProduct updatedOrderProduct = orderProductRepository.findById(orderProduct.getId()).get();
        // Disconnect from session so that the updates on updatedOrderProduct are not directly saved in db
        em.detach(updatedOrderProduct);
        updatedOrderProduct.price(UPDATED_PRICE).quantity(UPDATED_QUANTITY).total(UPDATED_TOTAL);
        OrderProductDTO orderProductDTO = orderProductMapper.toDto(updatedOrderProduct);

        restOrderProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderProductDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderProductDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);
        OrderProduct testOrderProduct = orderProductList.get(orderProductList.size() - 1);
        assertThat(testOrderProduct.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testOrderProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderProduct.getTotal()).isEqualTo(UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void putNonExistingOrderProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();
        orderProduct.setId(count.incrementAndGet());

        // Create the OrderProduct
        OrderProductDTO orderProductDTO = orderProductMapper.toDto(orderProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderProductDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();
        orderProduct.setId(count.incrementAndGet());

        // Create the OrderProduct
        OrderProductDTO orderProductDTO = orderProductMapper.toDto(orderProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();
        orderProduct.setId(count.incrementAndGet());

        // Create the OrderProduct
        OrderProductDTO orderProductDTO = orderProductMapper.toDto(orderProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderProductDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderProductWithPatch() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();

        // Update the orderProduct using partial update
        OrderProduct partialUpdatedOrderProduct = new OrderProduct();
        partialUpdatedOrderProduct.setId(orderProduct.getId());

        partialUpdatedOrderProduct.quantity(UPDATED_QUANTITY).total(UPDATED_TOTAL);

        restOrderProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderProduct))
            )
            .andExpect(status().isOk());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);
        OrderProduct testOrderProduct = orderProductList.get(orderProductList.size() - 1);
        assertThat(testOrderProduct.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testOrderProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderProduct.getTotal()).isEqualTo(UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void fullUpdateOrderProductWithPatch() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();

        // Update the orderProduct using partial update
        OrderProduct partialUpdatedOrderProduct = new OrderProduct();
        partialUpdatedOrderProduct.setId(orderProduct.getId());

        partialUpdatedOrderProduct.price(UPDATED_PRICE).quantity(UPDATED_QUANTITY).total(UPDATED_TOTAL);

        restOrderProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderProduct.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderProduct))
            )
            .andExpect(status().isOk());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);
        OrderProduct testOrderProduct = orderProductList.get(orderProductList.size() - 1);
        assertThat(testOrderProduct.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testOrderProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderProduct.getTotal()).isEqualTo(UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void patchNonExistingOrderProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();
        orderProduct.setId(count.incrementAndGet());

        // Create the OrderProduct
        OrderProductDTO orderProductDTO = orderProductMapper.toDto(orderProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderProductDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();
        orderProduct.setId(count.incrementAndGet());

        // Create the OrderProduct
        OrderProductDTO orderProductDTO = orderProductMapper.toDto(orderProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderProductRepository.findAll().size();
        orderProduct.setId(count.incrementAndGet());

        // Create the OrderProduct
        OrderProductDTO orderProductDTO = orderProductMapper.toDto(orderProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderProductMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderProductDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderProduct in the database
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderProduct() throws Exception {
        // Initialize the database
        orderProductRepository.saveAndFlush(orderProduct);

        int databaseSizeBeforeDelete = orderProductRepository.findAll().size();

        // Delete the orderProduct
        restOrderProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderProduct.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderProduct> orderProductList = orderProductRepository.findAll();
        assertThat(orderProductList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
