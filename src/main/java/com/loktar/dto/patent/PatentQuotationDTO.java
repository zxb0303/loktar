package com.loktar.dto.patent;

import com.loktar.domain.patent.PatentDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatentQuotationDTO extends PatentDetail {
    private String expirationDate;
    private int amount;
    private int lateFeeAmount;
    private int price;
    private String validDate;
    private String remark;
}
