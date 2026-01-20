package com.ms.data.master.campaign.model.mapper;

import com.ms.data.master.campaign.model.Coupon;
import com.ms.data.master.campaign.model.dto.campaign.CouponDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CouponMapper {
    CouponMapper INSTANCE = Mappers.getMapper(CouponMapper.class);

    Coupon toEntity(CouponDTO couponDTO);

    CouponDTO toDTO(Coupon coupon);
}
