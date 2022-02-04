package com.ed.ecom.repository;

import com.ed.ecom.domain.OrderProduct;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the OrderProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long>, JpaSpecificationExecutor<OrderProduct> {}
