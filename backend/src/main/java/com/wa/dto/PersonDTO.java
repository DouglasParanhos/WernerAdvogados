package com.wa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private Long id;
    private String fullname;
    private String email;
    private String cpf;
    private String rg;
    private String estadoCivil;
    private LocalDateTime dataNascimento;
    private String profissao;
    private String telefone;
    private Boolean vivo;
    private String representante;
    private String idFuncional;
    private String nacionalidade;
    private Long userId;
    private Long addressId;
    private AddressDTO address;
    private List<MatriculationDTO> matriculations;
}

