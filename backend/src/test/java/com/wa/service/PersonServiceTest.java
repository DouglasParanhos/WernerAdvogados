package com.wa.service;

import com.wa.dto.ClientCredentialsDTO;
import com.wa.dto.PersonDTO;
import com.wa.exception.PersonNotFoundException;
import com.wa.exception.UsernameAlreadyExistsException;
import com.wa.model.Person;
import com.wa.model.User;
import com.wa.repository.AddressRepository;
import com.wa.repository.MatriculationRepository;
import com.wa.repository.PersonRepository;
import com.wa.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private MatriculationRepository matriculationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PersonService personService;

    private Person person1;
    private Person person2;
    private Person person3;

    @BeforeEach
    void setUp() {
        person1 = new Person();
        person1.setId(1L);
        person1.setFullname("João Silva");
        person1.setEmail("joao@example.com");
        person1.setCpf("12345678900");
        person1.setTelefone("1234567890");
        person1.setAddress(null);
        person1.setMatriculations(new ArrayList<>());

        person2 = new Person();
        person2.setId(2L);
        person2.setFullname("Maria Santos");
        person2.setEmail("maria@example.com");
        person2.setCpf("98765432100");
        person2.setTelefone("9876543210");
        person2.setAddress(null);
        person2.setMatriculations(new ArrayList<>());

        person3 = new Person();
        person3.setId(3L);
        person3.setFullname("Pedro Oliveira");
        person3.setEmail("pedro@example.com");
        person3.setCpf("11122233344");
        person3.setTelefone("1112223334");
        person3.setAddress(null);
        person3.setMatriculations(new ArrayList<>());
    }

    @Test
    void testFindAllPaginated_WithoutSearch_ReturnsPaginatedResults() {
        // Arrange
        List<Person> persons = List.of(person1, person2, person3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Person> page = new PageImpl<>(persons, pageable, 3);

        when(personRepository.findAllWithRelationsPaginated(null, pageable)).thenReturn(page);

        // Act
        Page<PersonDTO> result = personService.findAllPaginated(null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(3, result.getContent().size());
        assertEquals("João Silva", result.getContent().get(0).getFullname());
        assertEquals("Maria Santos", result.getContent().get(1).getFullname());
        assertEquals("Pedro Oliveira", result.getContent().get(2).getFullname());
        verify(personRepository, times(1)).findAllWithRelationsPaginated(null, pageable);
    }

    @Test
    void testFindAllPaginated_WithSearch_ReturnsFilteredResults() {
        // Arrange
        String search = "João";
        List<Person> filteredPersons = List.of(person1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Person> page = new PageImpl<>(filteredPersons, pageable, 1);

        when(personRepository.findAllWithRelationsPaginated(eq(search), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<PersonDTO> result = personService.findAllPaginated(search, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("João Silva", result.getContent().get(0).getFullname());
        verify(personRepository, times(1)).findAllWithRelationsPaginated(search, pageable);
    }

    @Test
    void testFindAllPaginated_WithPagination_ReturnsCorrectPage() {
        // Arrange
        List<Person> page1Persons = List.of(person1, person2);
        List<Person> page2Persons = List.of(person3);
        Pageable pageable1 = PageRequest.of(0, 2);
        Pageable pageable2 = PageRequest.of(1, 2);
        Page<Person> page1 = new PageImpl<>(page1Persons, pageable1, 3);
        Page<Person> page2 = new PageImpl<>(page2Persons, pageable2, 3);

        when(personRepository.findAllWithRelationsPaginated(null, pageable1)).thenReturn(page1);
        when(personRepository.findAllWithRelationsPaginated(null, pageable2)).thenReturn(page2);

        // Act
        Page<PersonDTO> result1 = personService.findAllPaginated(null, pageable1);
        Page<PersonDTO> result2 = personService.findAllPaginated(null, pageable2);

        // Assert
        assertEquals(2, result1.getContent().size());
        assertEquals(1, result2.getContent().size());
        assertEquals(3, result1.getTotalElements());
        assertEquals(3, result2.getTotalElements());
        assertEquals(2, result1.getTotalPages());
        assertEquals(2, result2.getTotalPages());
    }

    @Test
    void testFindAllPaginated_WithEmptySearch_ReturnsAllResults() {
        // Arrange
        String search = "";
        List<Person> persons = List.of(person1, person2, person3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Person> page = new PageImpl<>(persons, pageable, 3);

        when(personRepository.findAllWithRelationsPaginated(eq(search), any(Pageable.class)))
                .thenReturn(page);

        // Act
        Page<PersonDTO> result = personService.findAllPaginated(search, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
    }

    @Test
    void testFindAllPaginated_WithDifferentPageSizes() {
        // Arrange
        List<Person> persons = List.of(person1, person2, person3);
        Pageable pageable10 = PageRequest.of(0, 10);
        Pageable pageable50 = PageRequest.of(0, 50);
        Pageable pageable100 = PageRequest.of(0, 100);
        Page<Person> page10 = new PageImpl<>(persons, pageable10, 3);
        Page<Person> page50 = new PageImpl<>(persons, pageable50, 3);
        Page<Person> page100 = new PageImpl<>(persons, pageable100, 3);

        when(personRepository.findAllWithRelationsPaginated(null, pageable10)).thenReturn(page10);
        when(personRepository.findAllWithRelationsPaginated(null, pageable50)).thenReturn(page50);
        when(personRepository.findAllWithRelationsPaginated(null, pageable100)).thenReturn(page100);

        // Act
        Page<PersonDTO> result10 = personService.findAllPaginated(null, pageable10);
        Page<PersonDTO> result50 = personService.findAllPaginated(null, pageable50);
        Page<PersonDTO> result100 = personService.findAllPaginated(null, pageable100);

        // Assert
        assertEquals(10, result10.getSize());
        assertEquals(50, result50.getSize());
        assertEquals(100, result100.getSize());
        assertEquals(3, result10.getTotalElements());
        assertEquals(3, result50.getTotalElements());
        assertEquals(3, result100.getTotalElements());
    }

    @Test
    void testGenerateUsernameSuggestion_WithFullName_ReturnsFirstAndLast() {
        // Arrange
        Person person = new Person();
        person.setId(1L);
        person.setFullname("João Silva Santos");
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        // Act
        String suggestion = personService.generateUsernameSuggestion(1L);

        // Assert
        assertEquals("joão.santos", suggestion);
        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    void testGenerateUsernameSuggestion_WithSingleName_ReturnsOnlyName() {
        // Arrange
        Person person = new Person();
        person.setId(1L);
        person.setFullname("João");
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        // Act
        String suggestion = personService.generateUsernameSuggestion(1L);

        // Assert
        assertEquals("joão", suggestion);
    }

    @Test
    void testGenerateUsernameSuggestion_WithTwoNames_ReturnsFirstAndLast() {
        // Arrange
        Person person = new Person();
        person.setId(1L);
        person.setFullname("Maria Santos");
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        // Act
        String suggestion = personService.generateUsernameSuggestion(1L);

        // Assert
        assertEquals("maria.santos", suggestion);
    }

    @Test
    void testGenerateUsernameSuggestion_WithEmptyName_ReturnsEmpty() {
        // Arrange
        Person person = new Person();
        person.setId(1L);
        person.setFullname("");
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        // Act
        String suggestion = personService.generateUsernameSuggestion(1L);

        // Assert
        assertEquals("", suggestion);
    }

    @Test
    void testGenerateUsernameSuggestion_PersonNotFound_ThrowsException() {
        // Arrange
        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PersonNotFoundException.class, () -> {
            personService.generateUsernameSuggestion(999L);
        });
    }

    @Test
    void testCreateOrUpdateCredentials_CreatesNewUser_WhenPersonHasNoUser() {
        // Arrange
        Person person = new Person();
        person.setId(1L);
        person.setFullname("João Silva");
        person.setUser(null);

        ClientCredentialsDTO credentials = new ClientCredentialsDTO();
        credentials.setUsername("joao.silva");
        credentials.setPassword("Test123!");

        User newUser = new User();
        newUser.setId(1L);
        newUser.setUsername("joao.silva");
        newUser.setRole("CLIENT");
        newUser.setPassword("encodedPassword");

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(userRepository.findByUsername("joao.silva")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Test123!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(personRepository.save(any(Person.class))).thenReturn(person);

        // Act
        personService.createOrUpdateCredentials(1L, credentials);

        // Assert
        verify(userRepository, times(1)).findByUsername("joao.silva");
        verify(passwordEncoder, times(1)).encode("Test123!");
        verify(userRepository, times(1)).save(any(User.class));
        verify(personRepository, times(1)).save(person);
    }

    @Test
    void testCreateOrUpdateCredentials_UpdatesExistingUser_WhenPersonHasUser() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("joao.silva");
        existingUser.setRole("CLIENT");
        existingUser.setPassword("oldEncodedPassword");

        Person person = new Person();
        person.setId(1L);
        person.setFullname("João Silva");
        person.setUser(existingUser);

        ClientCredentialsDTO credentials = new ClientCredentialsDTO();
        credentials.setUsername("joao.silva.new");
        credentials.setPassword("NewPass123!");

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(userRepository.findByUsername("joao.silva.new")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("NewPass123!")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(personRepository.save(any(Person.class))).thenReturn(person);

        // Act
        personService.createOrUpdateCredentials(1L, credentials);

        // Assert
        verify(userRepository, times(1)).findByUsername("joao.silva.new");
        verify(passwordEncoder, times(1)).encode("NewPass123!");
        verify(userRepository, times(1)).save(existingUser);
        verify(personRepository, times(1)).save(person);
    }

    @Test
    void testCreateOrUpdateCredentials_UsernameAlreadyExists_ThrowsException() {
        // Arrange
        Person person = new Person();
        person.setId(1L);
        person.setFullname("João Silva");
        person.setUser(null);

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("joao.silva");

        ClientCredentialsDTO credentials = new ClientCredentialsDTO();
        credentials.setUsername("joao.silva");
        credentials.setPassword("Test123!");

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(userRepository.findByUsername("joao.silva")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class, () -> {
            personService.createOrUpdateCredentials(1L, credentials);
        });
        
        assertTrue(exception.getMessage().contains("Username já está em uso"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateOrUpdateCredentials_UpdatesSameUser_DoesNotThrowException() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("joao.silva");

        Person person = new Person();
        person.setId(1L);
        person.setFullname("João Silva");
        person.setUser(existingUser);

        ClientCredentialsDTO credentials = new ClientCredentialsDTO();
        credentials.setUsername("joao.silva");
        credentials.setPassword("NewPass123!");

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(userRepository.findByUsername("joao.silva")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("NewPass123!")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(personRepository.save(any(Person.class))).thenReturn(person);

        // Act
        personService.createOrUpdateCredentials(1L, credentials);

        // Assert
        verify(userRepository, times(1)).save(existingUser);
        verify(personRepository, times(1)).save(person);
    }

    @Test
    void testCreateOrUpdateCredentials_PersonNotFound_ThrowsException() {
        // Arrange
        ClientCredentialsDTO credentials = new ClientCredentialsDTO();
        credentials.setUsername("joao.silva");
        credentials.setPassword("Test123!");

        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PersonNotFoundException.class, () -> {
            personService.createOrUpdateCredentials(999L, credentials);
        });
    }

    @Test
    void testConvertToDTO_WithUser_IncludesUsername() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("joao.silva");
        user.setRole("CLIENT");

        Person person = new Person();
        person.setId(1L);
        person.setFullname("João Silva");
        person.setEmail("joao@example.com");
        person.setCpf("12345678900");
        person.setUser(user);
        person.setMatriculations(new ArrayList<>());

        when(personRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(person));
        lenient().when(matriculationRepository.findByPersonIdWithProcesses(1L)).thenReturn(new ArrayList<>());

        // Act
        PersonDTO result = personService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("joao.silva", result.getUsername());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void testConvertToDTO_WithoutUser_UsernameIsNull() {
        // Arrange
        Person person = new Person();
        person.setId(1L);
        person.setFullname("João Silva");
        person.setEmail("joao@example.com");
        person.setCpf("12345678900");
        person.setUser(null);
        person.setMatriculations(new ArrayList<>());

        when(personRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(person));
        lenient().when(matriculationRepository.findByPersonIdWithProcesses(1L)).thenReturn(new ArrayList<>());

        // Act
        PersonDTO result = personService.findById(1L);

        // Assert
        assertNotNull(result);
        assertNull(result.getUsername());
        assertNull(result.getUserId());
    }
}

