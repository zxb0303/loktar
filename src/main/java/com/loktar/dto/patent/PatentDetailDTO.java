package com.loktar.dto.patent;

import com.loktar.domain.patent.PatentDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatentDetailDTO extends PatentDetail {
    private String encodeUrl;
    private String amount;
    private String payStatus;
    private String payDate;
    private String lateFeeAmount;
    private String price;
    private String remark;
}
