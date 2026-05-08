package shift_lab.crm.api.dto.projections;

import shift_lab.crm.core.entity.SellerEntity;

import java.math.BigDecimal;

public interface SellerBelowAmountProjection {
    SellerEntity getSeller();
    BigDecimal getTotalAmount();
}
