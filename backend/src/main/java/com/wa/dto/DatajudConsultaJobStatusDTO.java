package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatajudConsultaJobStatusDTO {

    private String jobId;
    /** PENDING, COMPLETED ou FAILED */
    private String status;
    private DatajudMovimentoConsultaResponseDTO result;
    private String errorMessage;
}
