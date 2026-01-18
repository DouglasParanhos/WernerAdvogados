package com.wa.dto;

import com.wa.validation.Cpf;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDTO {
    @NotBlank(message = "Nome completo é obrigatório")
    private String fullname;
    
    @Email(message = "Email deve ter um formato válido")
    private String email;
    
    @NotBlank(message = "CPF é obrigatório")
    @Cpf(message = "CPF inválido")
    private String cpf;
    
    @NotBlank(message = "RG é obrigatório")
    private String rg;
    
    @NotBlank(message = "Estado civil é obrigatório")
    private String estadoCivil;
    
    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDateTime dataNascimento;
    
    @NotBlank(message = "Profissão é obrigatória")
    private String profissao;
    
    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;
    
    @NotNull(message = "Campo 'vivo' é obrigatório")
    private Boolean vivo;
    
    private String representante;
    private String idFuncional;
    private String nacionalidade;
    private Long userId;
    private Long addressId;
    
    private AddressDTO address;
    
    private MatriculationRequestDTO matriculation1;
    private MatriculationRequestDTO matriculation2;
}

