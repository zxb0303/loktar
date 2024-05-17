package com.loktar.dto.patent;

import com.loktar.domain.patent.PatentDetail;
import lombok.Data;

@Data
public class PatentDetailDTO extends PatentDetail {
    private String encodeUrl;
    private String amount;
    private String payStatus;
    private String payDate;
    private String lateFeeAmount;
    private String feeReduction;
}
