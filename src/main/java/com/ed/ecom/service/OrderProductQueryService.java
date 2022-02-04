package com.ed.ecom.service;

import com.ed.ecom.domain.*; // for static metamodels
import com.ed.ecom.domain.OrderProduct;
import com.ed.ecom.repository.OrderProductRepository;
import com.ed.ecom.service.criteria.OrderProductCriteria;
import com.ed.ecom.service.dto.OrderProductDTO;
import com.ed.ecom.service.mapper.OrderProductMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OrderProduct} entities in the database.
 * The main input is a {@link OrderProductCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderProductDTO} or a {@link Page} of {@link OrderProductDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderProductQueryService extends QueryService<OrderProduct> {

    private final Logger log = LoggerFactory.getLogger(OrderProductQueryService.class);

    private final OrderProductRepository orderProductRepository;

    private final OrderProductMapper orderProductMapper;

    public OrderProductQueryService(OrderProductRepository orderProductRepository, OrderProductMapper orderProductMapper) {
        this.orderProductRepository = orderProductRepository;
        this.orderProductMapper = orderProductMapper;
    }

    /**
     * Return a {@link List} of {@link OrderProductDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderProductDTO> findByCriteria(OrderProductCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OrderProduct> specification = createSpecification(criteria);
        return orderProductMapper.toDto(orderProductRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OrderProductDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderProductDTO> findByCriteria(OrderProductCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OrderProduct> specification = createSpecification(criteria);
        return orderProductRepository.findAll(specification, page).map(orderProductMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderProductCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OrderProduct> specification = createSpecification(criteria);
        return orderProductRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderProductCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrderProduct> createSpecification(OrderProductCriteria criteria) {
        Specification<OrderProduct> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OrderProduct_.id));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), OrderProduct_.price));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), OrderProduct_.quantity));
            }
            if (criteria.getTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotal(), OrderProduct_.total));
            }
            if (criteria.getOrderId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getOrderId(), root -> root.join(OrderProduct_.order, JoinType.LEFT).get(Order_.id))
                    );
            }
            if (criteria.getProductId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductId(),
                            root -> root.join(OrderProduct_.product, JoinType.LEFT).get(Product_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
