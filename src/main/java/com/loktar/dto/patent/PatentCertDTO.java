package com.loktar.dto.patent;

import com.loktar.domain.patent.PatentDetail;
import lombok.Data;

@Data
public class PatentCertDTO extends PatentDetail {
    private String certId;
    private String inventorName;
    private String address;
}
