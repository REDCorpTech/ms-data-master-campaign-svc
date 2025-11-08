package com.ms.data.master.campaign.respository;

import com.ms.data.master.campaign.model.Campaign;
import com.ms.data.master.campaign.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID>, JpaSpecificationExecutor<Coupon> {
    List<Coupon> findAllByCouponStatus(String couponStatus);
}
