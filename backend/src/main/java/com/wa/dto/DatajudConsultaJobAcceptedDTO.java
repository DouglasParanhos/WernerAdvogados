package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatajudConsultaJobAcceptedDTO {

    private String jobId;
    /** PENDING enquanto a consulta roda no servidor */
    private String status;
}
