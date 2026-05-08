package shift_lab.crm.api.dto;

import shift_lab.crm.core.entity.SellerEntity;

import java.math.BigDecimal;

public interface TopSellerProjection {
    SellerEntity getSeller();
    BigDecimal getTotalAmount();
}
