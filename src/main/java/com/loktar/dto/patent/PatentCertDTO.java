package com.loktar.dto.patent;

import com.loktar.domain.patent.PatentDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatentCertDTO extends PatentDetail {
    private String certId;
    private String inventorName;
    private String address;
}
