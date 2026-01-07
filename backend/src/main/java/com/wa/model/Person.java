package com.wa.model;

import com.wa.validation.Cpf;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "person")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;
    
    @Column(name = "nome_completo", unique = true, nullable = false, length = 50)
    private String fullname;
    
    @Column(nullable = true, length = 255)
    @Email(message = "Email deve ter um formato válido")
    private String email;
    
    @Column(unique = true, nullable = false, length = 16)
    @Cpf(message = "CPF inválido")
    private String cpf;
    
    @Column(unique = true, nullable = false, length = 16)
    private String rg;
    
    @Column(name = "estado_civil", nullable = false, length = 16)
    private String estadoCivil;
    
    @Column(name = "data_nascimento", nullable = false)
    private LocalDateTime dataNascimento;
    
    @Column(nullable = false, length = 255)
    private String profissao;
    
    @Column(nullable = true, length = 16)
    private String telefone;
    
    @Column(nullable = false)
    private Boolean vivo;
    
    @Column(length = 255)
    private String representante;
    
    @Column(name = "id_funcional", length = 16)
    private String idFuncional;
    
    @Column(length = 32)
    private String nacionalidade;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    private Address address;
    
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Matriculation> matriculations;
    
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();
    
    @Column(name = "modified_on", nullable = false)
    private LocalDateTime modifiedOn = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }
}

