package com.wa.dto;

import com.wa.service.ProcessUpdateStatusService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStatusDTO {
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    
    public static ProcessStatusDTO fromStatusInfo(ProcessUpdateStatusService.StatusInfo statusInfo) {
        ProcessStatusDTO dto = new ProcessStatusDTO();
        dto.setStatus(statusInfo.getStatus().name());
        dto.setStartedAt(statusInfo.getStartedAt());
        dto.setCompletedAt(statusInfo.getCompletedAt());
        dto.setErrorMessage(statusInfo.getErrorMessage());
        return dto;
    }
}

