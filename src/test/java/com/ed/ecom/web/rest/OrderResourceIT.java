package com.ed.ecom.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ed.ecom.IntegrationTest;
import com.ed.ecom.domain.Order;
import com.ed.ecom.domain.enumeration.OrderStatus;
import com.ed.ecom.repository.OrderRepository;
import com.ed.ecom.service.criteria.OrderCriteria;
import com.ed.ecom.service.dto.OrderDTO;
import com.ed.ecom.service.mapper.OrderMapper;
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
 * Integration tests for the {@link OrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderResourceIT {

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.TEMP;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.WAIT_TO_DELIVER;

    private static final Float DEFAULT_TOTAL = 0F;
    private static final Float UPDATED_TOTAL = 1F;
    private static final Float SMALLER_TOTAL = 0F - 1F;

    private static final Float DEFAULT_DISCOUNT = 1F;
    private static final Float UPDATED_DISCOUNT = 2F;
    private static final Float SMALLER_DISCOUNT = 1F - 1F;

    private static final Float DEFAULT_FEE = 1F;
    private static final Float UPDATED_FEE = 2F;
    private static final Float SMALLER_FEE = 1F - 1F;

    private static final Float DEFAULT_ACTUAL_TOTAL = 1F;
    private static final Float UPDATED_ACTUAL_TOTAL = 2F;
    private static final Float SMALLER_ACTUAL_TOTAL = 1F - 1F;

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderMockMvc;

    private Order order;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity(EntityManager em) {
        Order order = new Order()
            .status(DEFAULT_STATUS)
            .total(DEFAULT_TOTAL)
            .discount(DEFAULT_DISCOUNT)
            .fee(DEFAULT_FEE)
            .actualTotal(DEFAULT_ACTUAL_TOTAL);
        return order;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity(EntityManager em) {
        Order order = new Order()
            .status(UPDATED_STATUS)
            .total(UPDATED_TOTAL)
            .discount(UPDATED_DISCOUNT)
            .fee(UPDATED_FEE)
            .actualTotal(UPDATED_ACTUAL_TOTAL);
        return order;
    }

    @BeforeEach
    public void initTest() {
        order = createEntity(em);
    }

    @Test
    @Transactional
    void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();
        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOrder.getTotal()).isEqualTo(DEFAULT_TOTAL);
        assertThat(testOrder.getDiscount()).isEqualTo(DEFAULT_DISCOUNT);
        assertThat(testOrder.getFee()).isEqualTo(DEFAULT_FEE);
        assertThat(testOrder.getActualTotal()).isEqualTo(DEFAULT_ACTUAL_TOTAL);
    }

    @Test
    @Transactional
    void createOrderWithExistingId() throws Exception {
        // Create the Order with an existing ID
        order.setId(1L);
        OrderDTO orderDTO = orderMapper.toDto(order);

        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTotalIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setTotal(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(DEFAULT_DISCOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].fee").value(hasItem(DEFAULT_FEE.doubleValue())))
            .andExpect(jsonPath("$.[*].actualTotal").value(hasItem(DEFAULT_ACTUAL_TOTAL.doubleValue())));
    }

    @Test
    @Transactional
    void getOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.total").value(DEFAULT_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.discount").value(DEFAULT_DISCOUNT.doubleValue()))
            .andExpect(jsonPath("$.fee").value(DEFAULT_FEE.doubleValue()))
            .andExpect(jsonPath("$.actualTotal").value(DEFAULT_ACTUAL_TOTAL.doubleValue()));
    }

    @Test
    @Transactional
    void getOrdersByIdFiltering() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        Long id = order.getId();

        defaultOrderShouldBeFound("id.equals=" + id);
        defaultOrderShouldNotBeFound("id.notEquals=" + id);

        defaultOrderShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status equals to DEFAULT_STATUS
        defaultOrderShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the orderList where status equals to UPDATED_STATUS
        defaultOrderShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status not equals to DEFAULT_STATUS
        defaultOrderShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the orderList where status not equals to UPDATED_STATUS
        defaultOrderShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultOrderShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the orderList where status equals to UPDATED_STATUS
        defaultOrderShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOrdersByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where status is not null
        defaultOrderShouldBeFound("status.specified=true");

        // Get all the orderList where status is null
        defaultOrderShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where total equals to DEFAULT_TOTAL
        defaultOrderShouldBeFound("total.equals=" + DEFAULT_TOTAL);

        // Get all the orderList where total equals to UPDATED_TOTAL
        defaultOrderShouldNotBeFound("total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where total not equals to DEFAULT_TOTAL
        defaultOrderShouldNotBeFound("total.notEquals=" + DEFAULT_TOTAL);

        // Get all the orderList where total not equals to UPDATED_TOTAL
        defaultOrderShouldBeFound("total.notEquals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where total in DEFAULT_TOTAL or UPDATED_TOTAL
        defaultOrderShouldBeFound("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL);

        // Get all the orderList where total equals to UPDATED_TOTAL
        defaultOrderShouldNotBeFound("total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where total is not null
        defaultOrderShouldBeFound("total.specified=true");

        // Get all the orderList where total is null
        defaultOrderShouldNotBeFound("total.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where total is greater than or equal to DEFAULT_TOTAL
        defaultOrderShouldBeFound("total.greaterThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the orderList where total is greater than or equal to UPDATED_TOTAL
        defaultOrderShouldNotBeFound("total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where total is less than or equal to DEFAULT_TOTAL
        defaultOrderShouldBeFound("total.lessThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the orderList where total is less than or equal to SMALLER_TOTAL
        defaultOrderShouldNotBeFound("total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where total is less than DEFAULT_TOTAL
        defaultOrderShouldNotBeFound("total.lessThan=" + DEFAULT_TOTAL);

        // Get all the orderList where total is less than UPDATED_TOTAL
        defaultOrderShouldBeFound("total.lessThan=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where total is greater than DEFAULT_TOTAL
        defaultOrderShouldNotBeFound("total.greaterThan=" + DEFAULT_TOTAL);

        // Get all the orderList where total is greater than SMALLER_TOTAL
        defaultOrderShouldBeFound("total.greaterThan=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discount equals to DEFAULT_DISCOUNT
        defaultOrderShouldBeFound("discount.equals=" + DEFAULT_DISCOUNT);

        // Get all the orderList where discount equals to UPDATED_DISCOUNT
        defaultOrderShouldNotBeFound("discount.equals=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discount not equals to DEFAULT_DISCOUNT
        defaultOrderShouldNotBeFound("discount.notEquals=" + DEFAULT_DISCOUNT);

        // Get all the orderList where discount not equals to UPDATED_DISCOUNT
        defaultOrderShouldBeFound("discount.notEquals=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discount in DEFAULT_DISCOUNT or UPDATED_DISCOUNT
        defaultOrderShouldBeFound("discount.in=" + DEFAULT_DISCOUNT + "," + UPDATED_DISCOUNT);

        // Get all the orderList where discount equals to UPDATED_DISCOUNT
        defaultOrderShouldNotBeFound("discount.in=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discount is not null
        defaultOrderShouldBeFound("discount.specified=true");

        // Get all the orderList where discount is null
        defaultOrderShouldNotBeFound("discount.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discount is greater than or equal to DEFAULT_DISCOUNT
        defaultOrderShouldBeFound("discount.greaterThanOrEqual=" + DEFAULT_DISCOUNT);

        // Get all the orderList where discount is greater than or equal to UPDATED_DISCOUNT
        defaultOrderShouldNotBeFound("discount.greaterThanOrEqual=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discount is less than or equal to DEFAULT_DISCOUNT
        defaultOrderShouldBeFound("discount.lessThanOrEqual=" + DEFAULT_DISCOUNT);

        // Get all the orderList where discount is less than or equal to SMALLER_DISCOUNT
        defaultOrderShouldNotBeFound("discount.lessThanOrEqual=" + SMALLER_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discount is less than DEFAULT_DISCOUNT
        defaultOrderShouldNotBeFound("discount.lessThan=" + DEFAULT_DISCOUNT);

        // Get all the orderList where discount is less than UPDATED_DISCOUNT
        defaultOrderShouldBeFound("discount.lessThan=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByDiscountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where discount is greater than DEFAULT_DISCOUNT
        defaultOrderShouldNotBeFound("discount.greaterThan=" + DEFAULT_DISCOUNT);

        // Get all the orderList where discount is greater than SMALLER_DISCOUNT
        defaultOrderShouldBeFound("discount.greaterThan=" + SMALLER_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllOrdersByFeeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where fee equals to DEFAULT_FEE
        defaultOrderShouldBeFound("fee.equals=" + DEFAULT_FEE);

        // Get all the orderList where fee equals to UPDATED_FEE
        defaultOrderShouldNotBeFound("fee.equals=" + UPDATED_FEE);
    }

    @Test
    @Transactional
    void getAllOrdersByFeeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where fee not equals to DEFAULT_FEE
        defaultOrderShouldNotBeFound("fee.notEquals=" + DEFAULT_FEE);

        // Get all the orderList where fee not equals to UPDATED_FEE
        defaultOrderShouldBeFound("fee.notEquals=" + UPDATED_FEE);
    }

    @Test
    @Transactional
    void getAllOrdersByFeeIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where fee in DEFAULT_FEE or UPDATED_FEE
        defaultOrderShouldBeFound("fee.in=" + DEFAULT_FEE + "," + UPDATED_FEE);

        // Get all the orderList where fee equals to UPDATED_FEE
        defaultOrderShouldNotBeFound("fee.in=" + UPDATED_FEE);
    }

    @Test
    @Transactional
    void getAllOrdersByFeeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where fee is not null
        defaultOrderShouldBeFound("fee.specified=true");

        // Get all the orderList where fee is null
        defaultOrderShouldNotBeFound("fee.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByFeeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where fee is greater than or equal to DEFAULT_FEE
        defaultOrderShouldBeFound("fee.greaterThanOrEqual=" + DEFAULT_FEE);

        // Get all the orderList where fee is greater than or equal to UPDATED_FEE
        defaultOrderShouldNotBeFound("fee.greaterThanOrEqual=" + UPDATED_FEE);
    }

    @Test
    @Transactional
    void getAllOrdersByFeeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where fee is less than or equal to DEFAULT_FEE
        defaultOrderShouldBeFound("fee.lessThanOrEqual=" + DEFAULT_FEE);

        // Get all the orderList where fee is less than or equal to SMALLER_FEE
        defaultOrderShouldNotBeFound("fee.lessThanOrEqual=" + SMALLER_FEE);
    }

    @Test
    @Transactional
    void getAllOrdersByFeeIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where fee is less than DEFAULT_FEE
        defaultOrderShouldNotBeFound("fee.lessThan=" + DEFAULT_FEE);

        // Get all the orderList where fee is less than UPDATED_FEE
        defaultOrderShouldBeFound("fee.lessThan=" + UPDATED_FEE);
    }

    @Test
    @Transactional
    void getAllOrdersByFeeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where fee is greater than DEFAULT_FEE
        defaultOrderShouldNotBeFound("fee.greaterThan=" + DEFAULT_FEE);

        // Get all the orderList where fee is greater than SMALLER_FEE
        defaultOrderShouldBeFound("fee.greaterThan=" + SMALLER_FEE);
    }

    @Test
    @Transactional
    void getAllOrdersByActualTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where actualTotal equals to DEFAULT_ACTUAL_TOTAL
        defaultOrderShouldBeFound("actualTotal.equals=" + DEFAULT_ACTUAL_TOTAL);

        // Get all the orderList where actualTotal equals to UPDATED_ACTUAL_TOTAL
        defaultOrderShouldNotBeFound("actualTotal.equals=" + UPDATED_ACTUAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByActualTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where actualTotal not equals to DEFAULT_ACTUAL_TOTAL
        defaultOrderShouldNotBeFound("actualTotal.notEquals=" + DEFAULT_ACTUAL_TOTAL);

        // Get all the orderList where actualTotal not equals to UPDATED_ACTUAL_TOTAL
        defaultOrderShouldBeFound("actualTotal.notEquals=" + UPDATED_ACTUAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByActualTotalIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where actualTotal in DEFAULT_ACTUAL_TOTAL or UPDATED_ACTUAL_TOTAL
        defaultOrderShouldBeFound("actualTotal.in=" + DEFAULT_ACTUAL_TOTAL + "," + UPDATED_ACTUAL_TOTAL);

        // Get all the orderList where actualTotal equals to UPDATED_ACTUAL_TOTAL
        defaultOrderShouldNotBeFound("actualTotal.in=" + UPDATED_ACTUAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByActualTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where actualTotal is not null
        defaultOrderShouldBeFound("actualTotal.specified=true");

        // Get all the orderList where actualTotal is null
        defaultOrderShouldNotBeFound("actualTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllOrdersByActualTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where actualTotal is greater than or equal to DEFAULT_ACTUAL_TOTAL
        defaultOrderShouldBeFound("actualTotal.greaterThanOrEqual=" + DEFAULT_ACTUAL_TOTAL);

        // Get all the orderList where actualTotal is greater than or equal to UPDATED_ACTUAL_TOTAL
        defaultOrderShouldNotBeFound("actualTotal.greaterThanOrEqual=" + UPDATED_ACTUAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByActualTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where actualTotal is less than or equal to DEFAULT_ACTUAL_TOTAL
        defaultOrderShouldBeFound("actualTotal.lessThanOrEqual=" + DEFAULT_ACTUAL_TOTAL);

        // Get all the orderList where actualTotal is less than or equal to SMALLER_ACTUAL_TOTAL
        defaultOrderShouldNotBeFound("actualTotal.lessThanOrEqual=" + SMALLER_ACTUAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByActualTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where actualTotal is less than DEFAULT_ACTUAL_TOTAL
        defaultOrderShouldNotBeFound("actualTotal.lessThan=" + DEFAULT_ACTUAL_TOTAL);

        // Get all the orderList where actualTotal is less than UPDATED_ACTUAL_TOTAL
        defaultOrderShouldBeFound("actualTotal.lessThan=" + UPDATED_ACTUAL_TOTAL);
    }

    @Test
    @Transactional
    void getAllOrdersByActualTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where actualTotal is greater than DEFAULT_ACTUAL_TOTAL
        defaultOrderShouldNotBeFound("actualTotal.greaterThan=" + DEFAULT_ACTUAL_TOTAL);

        // Get all the orderList where actualTotal is greater than SMALLER_ACTUAL_TOTAL
        defaultOrderShouldBeFound("actualTotal.greaterThan=" + SMALLER_ACTUAL_TOTAL);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderShouldBeFound(String filter) throws Exception {
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].total").value(hasItem(DEFAULT_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(DEFAULT_DISCOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].fee").value(hasItem(DEFAULT_FEE.doubleValue())))
            .andExpect(jsonPath("$.[*].actualTotal").value(hasItem(DEFAULT_ACTUAL_TOTAL.doubleValue())));

        // Check, that the count call also returns 1
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderShouldNotBeFound(String filter) throws Exception {
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order
        Order updatedOrder = orderRepository.findById(order.getId()).get();
        // Disconnect from session so that the updates on updatedOrder are not directly saved in db
        em.detach(updatedOrder);
        updatedOrder
            .status(UPDATED_STATUS)
            .total(UPDATED_TOTAL)
            .discount(UPDATED_DISCOUNT)
            .fee(UPDATED_FEE)
            .actualTotal(UPDATED_ACTUAL_TOTAL);
        OrderDTO orderDTO = orderMapper.toDto(updatedOrder);

        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrder.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testOrder.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
        assertThat(testOrder.getFee()).isEqualTo(UPDATED_FEE);
        assertThat(testOrder.getActualTotal()).isEqualTo(UPDATED_ACTUAL_TOTAL);
    }

    @Test
    @Transactional
    void putNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder.status(UPDATED_STATUS).discount(UPDATED_DISCOUNT).fee(UPDATED_FEE).actualTotal(UPDATED_ACTUAL_TOTAL);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrder.getTotal()).isEqualTo(DEFAULT_TOTAL);
        assertThat(testOrder.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
        assertThat(testOrder.getFee()).isEqualTo(UPDATED_FEE);
        assertThat(testOrder.getActualTotal()).isEqualTo(UPDATED_ACTUAL_TOTAL);
    }

    @Test
    @Transactional
    void fullUpdateOrderWithPatch() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order using partial update
        Order partialUpdatedOrder = new Order();
        partialUpdatedOrder.setId(order.getId());

        partialUpdatedOrder
            .status(UPDATED_STATUS)
            .total(UPDATED_TOTAL)
            .discount(UPDATED_DISCOUNT)
            .fee(UPDATED_FEE)
            .actualTotal(UPDATED_ACTUAL_TOTAL);

        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrder))
            )
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrder.getTotal()).isEqualTo(UPDATED_TOTAL);
        assertThat(testOrder.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
        assertThat(testOrder.getFee()).isEqualTo(UPDATED_FEE);
        assertThat(testOrder.getActualTotal()).isEqualTo(UPDATED_ACTUAL_TOTAL);
    }

    @Test
    @Transactional
    void patchNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, order.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
