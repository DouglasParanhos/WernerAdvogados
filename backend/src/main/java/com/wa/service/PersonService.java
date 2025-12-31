package com.wa.service;

import com.wa.dto.AddressDTO;
import com.wa.dto.MatriculationDTO;
import com.wa.dto.MatriculationRequestDTO;
import com.wa.dto.PersonDTO;
import com.wa.dto.PersonRequestDTO;
import com.wa.model.Address;
import com.wa.model.Matriculation;
import com.wa.model.Person;
import com.wa.repository.AddressRepository;
import com.wa.repository.MatriculationRepository;
import com.wa.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {
    
    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final MatriculationRepository matriculationRepository;
    
    public List<PersonDTO> findAll() {
        return personRepository.findAllWithRelations().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PersonDTO findById(Long id) {
        Person person = personRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));
        
        // Carregar processos das matrículas em uma query separada para evitar MultipleBagFetchException
        if (person.getMatriculations() != null && !person.getMatriculations().isEmpty()) {
            List<com.wa.model.Matriculation> matriculationsWithProcesses = 
                    matriculationRepository.findByPersonIdWithProcesses(id);
            
            // Atualizar as matrículas da pessoa com os processos carregados
            person.getMatriculations().forEach(mat -> {
                matriculationsWithProcesses.stream()
                        .filter(m -> m.getId().equals(mat.getId()))
                        .findFirst()
                        .ifPresent(m -> mat.setProcesses(m.getProcesses()));
            });
        }
        
        return convertToDTO(person);
    }
    
    @Transactional
    public PersonDTO create(PersonRequestDTO request) {
        Person person = new Person();
        person.setFullname(request.getFullname());
        // Converte string vazia para null para evitar violação de constraint UNIQUE
        person.setEmail(request.getEmail() != null && !request.getEmail().trim().isEmpty() 
            ? request.getEmail().trim() : null);
        person.setCpf(request.getCpf());
        person.setRg(request.getRg());
        person.setEstadoCivil(request.getEstadoCivil());
        person.setDataNascimento(request.getDataNascimento());
        person.setProfissao(request.getProfissao());
        person.setTelefone(request.getTelefone());
        person.setVivo(request.getVivo());
        person.setRepresentante(request.getRepresentante());
        person.setIdFuncional(request.getIdFuncional());
        person.setNacionalidade(request.getNacionalidade());
        
        if (request.getAddress() != null) {
            Address address = convertToEntity(request.getAddress());
            address = addressRepository.save(address);
            person.setAddress(address);
        } else if (request.getAddressId() != null) {
            Address address = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
            person.setAddress(address);
        }
        
        person = personRepository.save(person);
        
        // Processar matrículas
        processMatriculations(person, request);
        
        return convertToDTO(personRepository.findByIdWithRelations(person.getId()).orElse(person));
    }
    
    @Transactional
    public PersonDTO update(Long id, PersonRequestDTO request) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));
        
        person.setFullname(request.getFullname());
        // Converte string vazia para null para evitar violação de constraint UNIQUE
        person.setEmail(request.getEmail() != null && !request.getEmail().trim().isEmpty() 
            ? request.getEmail().trim() : null);
        person.setCpf(request.getCpf());
        person.setRg(request.getRg());
        person.setEstadoCivil(request.getEstadoCivil());
        person.setDataNascimento(request.getDataNascimento());
        person.setProfissao(request.getProfissao());
        person.setTelefone(request.getTelefone());
        person.setVivo(request.getVivo());
        person.setRepresentante(request.getRepresentante());
        person.setIdFuncional(request.getIdFuncional());
        person.setNacionalidade(request.getNacionalidade());
        
        if (request.getAddress() != null) {
            Address address = convertToEntity(request.getAddress());
            if (person.getAddress() != null && person.getAddress().getId() != null) {
                address.setId(person.getAddress().getId());
            }
            address = addressRepository.save(address);
            person.setAddress(address);
        } else if (request.getAddressId() != null) {
            Address address = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
            person.setAddress(address);
        }
        
        person = personRepository.save(person);
        
        // Processar matrículas
        processMatriculations(person, request);
        
        return convertToDTO(personRepository.findByIdWithRelations(person.getId()).orElse(person));
    }
    
    private void processMatriculations(Person person, PersonRequestDTO request) {
        // Buscar matrículas existentes da pessoa
        List<Matriculation> existingMatriculations = matriculationRepository.findByPersonId(person.getId());
        
        // Processar primeira matrícula
        if (request.getMatriculation1() != null) {
            MatriculationRequestDTO mat1 = request.getMatriculation1();
            if (isMatriculationValid(mat1)) {
                Matriculation matriculation1;
                if (!existingMatriculations.isEmpty()) {
                    // Atualizar primeira matrícula existente
                    matriculation1 = existingMatriculations.get(0);
                    updateMatriculationFromDTO(matriculation1, mat1);
                } else {
                    // Criar nova matrícula
                    matriculation1 = convertToMatriculationEntity(mat1);
                    matriculation1.setPerson(person);
                }
                matriculationRepository.save(matriculation1);
            }
        }
        
        // Processar segunda matrícula
        if (request.getMatriculation2() != null) {
            MatriculationRequestDTO mat2 = request.getMatriculation2();
            if (isMatriculationValid(mat2)) {
                Matriculation matriculation2;
                if (existingMatriculations.size() > 1) {
                    // Atualizar segunda matrícula existente
                    matriculation2 = existingMatriculations.get(1);
                    updateMatriculationFromDTO(matriculation2, mat2);
                } else {
                    // Criar nova matrícula
                    matriculation2 = convertToMatriculationEntity(mat2);
                    matriculation2.setPerson(person);
                }
                matriculationRepository.save(matriculation2);
            }
        } else if (existingMatriculations.size() > 1) {
            // Se não há segunda matrícula no request mas existe uma, remover
            matriculationRepository.delete(existingMatriculations.get(1));
        }
    }
    
    private void updateMatriculationFromDTO(Matriculation mat, MatriculationRequestDTO dto) {
        mat.setNumero(dto.getNumero());
        mat.setInicioErj(dto.getInicioErj());
        mat.setCargo(dto.getCargo());
        mat.setDataAposentadoria(dto.getDataAposentadoria());
        mat.setNivelAtual(dto.getNivelAtual());
        mat.setTrienioAtual(dto.getTrienioAtual());
        mat.setReferencia(dto.getReferencia());
    }
    
    private boolean isMatriculationValid(MatriculationRequestDTO mat) {
        return mat.getNumero() != null && !mat.getNumero().trim().isEmpty() &&
               mat.getCargo() != null && !mat.getCargo().trim().isEmpty() &&
               mat.getNivelAtual() != null && !mat.getNivelAtual().trim().isEmpty() &&
               mat.getTrienioAtual() != null && !mat.getTrienioAtual().trim().isEmpty() &&
               mat.getReferencia() != null && !mat.getReferencia().trim().isEmpty();
    }
    
    private Matriculation convertToMatriculationEntity(MatriculationRequestDTO dto) {
        Matriculation mat = new Matriculation();
        mat.setNumero(dto.getNumero());
        mat.setInicioErj(dto.getInicioErj());
        mat.setCargo(dto.getCargo());
        mat.setDataAposentadoria(dto.getDataAposentadoria());
        mat.setNivelAtual(dto.getNivelAtual());
        mat.setTrienioAtual(dto.getTrienioAtual());
        mat.setReferencia(dto.getReferencia());
        return mat;
    }
    
    @Transactional
    public void delete(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));
        personRepository.delete(person);
    }
    
    private PersonDTO convertToDTO(Person person) {
        PersonDTO dto = new PersonDTO();
        dto.setId(person.getId());
        dto.setFullname(person.getFullname());
        dto.setEmail(person.getEmail());
        dto.setCpf(person.getCpf());
        dto.setRg(person.getRg());
        dto.setEstadoCivil(person.getEstadoCivil());
        dto.setDataNascimento(person.getDataNascimento());
        dto.setProfissao(person.getProfissao());
        dto.setTelefone(person.getTelefone());
        dto.setVivo(person.getVivo());
        dto.setRepresentante(person.getRepresentante());
        dto.setIdFuncional(person.getIdFuncional());
        dto.setNacionalidade(person.getNacionalidade());
        dto.setUserId(person.getUser() != null ? person.getUser().getId() : null);
        dto.setAddressId(person.getAddress() != null ? person.getAddress().getId() : null);
        
        if (person.getAddress() != null) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setId(person.getAddress().getId());
            addressDTO.setLogradouro(person.getAddress().getLogradouro());
            addressDTO.setCidade(person.getAddress().getCidade());
            addressDTO.setEstado(person.getAddress().getEstado());
            addressDTO.setCep(person.getAddress().getCep());
            dto.setAddress(addressDTO);
        }
        
        if (person.getMatriculations() != null) {
            dto.setMatriculations(person.getMatriculations().stream()
                    .map(this::convertMatriculationToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private MatriculationDTO convertMatriculationToDTO(com.wa.model.Matriculation matriculation) {
        MatriculationDTO dto = new MatriculationDTO();
        dto.setId(matriculation.getId());
        dto.setNumero(matriculation.getNumero());
        dto.setInicioErj(matriculation.getInicioErj());
        dto.setCargo(matriculation.getCargo());
        dto.setDataAposentadoria(matriculation.getDataAposentadoria());
        dto.setNivelAtual(matriculation.getNivelAtual());
        dto.setTrienioAtual(matriculation.getTrienioAtual());
        dto.setReferencia(matriculation.getReferencia());
        dto.setPersonId(matriculation.getPerson().getId());
        
        if (matriculation.getProcesses() != null) {
            dto.setProcesses(matriculation.getProcesses().stream()
                    .map(this::convertProcessToDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private com.wa.dto.ProcessDTO convertProcessToDTO(com.wa.model.Process process) {
        com.wa.dto.ProcessDTO dto = new com.wa.dto.ProcessDTO();
        dto.setId(process.getId());
        dto.setNumero(process.getNumero());
        dto.setComarca(process.getComarca());
        dto.setVara(process.getVara());
        dto.setSistema(process.getSistema());
        dto.setValorOriginal(process.getValorOriginal());
        dto.setValorCorrigido(process.getValorCorrigido());
        dto.setPrevisaoHonorariosContratuais(process.getPrevisaoHonorariosContratuais());
        dto.setPrevisaoHonorariosSucumbenciais(process.getPrevisaoHonorariosSucumbenciais());
        dto.setDistribuidoEm(process.getDistribuidoEm());
        dto.setTipoProcesso(process.getTipoProcesso());
        dto.setStatus(process.getStatus());
        dto.setMatriculationId(process.getMatriculation().getId());
        return dto;
    }
    
    private Address convertToEntity(AddressDTO dto) {
        Address address = new Address();
        if (dto.getId() != null) {
            address.setId(dto.getId());
        }
        address.setLogradouro(dto.getLogradouro());
        address.setCidade(dto.getCidade());
        address.setEstado(dto.getEstado());
        address.setCep(dto.getCep());
        return address;
    }
}

