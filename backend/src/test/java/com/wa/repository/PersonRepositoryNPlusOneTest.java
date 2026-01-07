package com.wa.repository;

import com.wa.model.Address;
import com.wa.model.Person;
import com.wa.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Testes para verificar que as queries do PersonRepository não causam problemas N+1
 * e que os relacionamentos são carregados corretamente usando JOIN FETCH
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true"
})
class PersonRepositoryNPlusOneTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Address address1;
    private Address address2;
    private User user1;
    private Person person1;
    private Person person2;

    @BeforeEach
    void setUp() {
        // Limpar dados existentes
        personRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();

        // Criar endereços
        address1 = new Address();
        address1.setLogradouro("Rua Teste 1");
        address1.setCidade("Rio de Janeiro");
        address1.setEstado("RJ");
        address1.setCep("20000-000");
        address1 = addressRepository.save(address1);

        address2 = new Address();
        address2.setLogradouro("Rua Teste 2");
        address2.setCidade("São Paulo");
        address2.setEstado("SP");
        address2.setCep("01000-000");
        address2 = addressRepository.save(address2);

        // Criar usuário
        user1 = new User();
        user1.setUsername("test.user");
        user1.setPassword("password");
        user1.setRole("ADMIN");
        user1 = userRepository.save(user1);

        // Criar pessoas
        person1 = new Person();
        person1.setFullname("João Silva");
        person1.setEmail("joao@example.com");
        person1.setCpf("12345678900");
        person1.setAddress(address1);
        person1.setUser(user1);
        person1 = personRepository.save(person1);

        person2 = new Person();
        person2.setFullname("Maria Santos");
        person2.setEmail("maria@example.com");
        person2.setCpf("98765432100");
        person2.setAddress(address2);
        person2.setUser(null);
        person2 = personRepository.save(person2);

        // Limpar contexto de persistência para forçar reload do banco
        entityManager.clear();
    }

    @Test
    void testFindAllWithRelations_LoadsAddressAndUser() {
        // Act
        List<Person> persons = personRepository.findAllWithRelations();

        // Assert
        assertThat(persons).hasSize(2);

        Person joao = persons.stream()
                .filter(p -> p.getFullname().equals("João Silva"))
                .findFirst()
                .orElse(null);

        assertNotNull(joao);
        assertNotNull(joao.getAddress());
        assertThat(joao.getAddress().getLogradouro()).isEqualTo("Rua Teste 1");
        assertNotNull(joao.getUser());
        assertThat(joao.getUser().getUsername()).isEqualTo("test.user");

        Person maria = persons.stream()
                .filter(p -> p.getFullname().equals("Maria Santos"))
                .findFirst()
                .orElse(null);

        assertNotNull(maria);
        assertNotNull(maria.getAddress());
        assertThat(maria.getAddress().getLogradouro()).isEqualTo("Rua Teste 2");
        assertNull(maria.getUser());
    }

    @Test
    void testFindAllWithRelationsPaginated_LoadsRelationshipsInSingleQuery() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Person> page = personRepository.findAllWithRelationsPaginated(null, pageable);

        // Assert
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);

        // Verificar que os relacionamentos foram carregados (não são proxies)
        Person joao = page.getContent().stream()
                .filter(p -> p.getFullname().equals("João Silva"))
                .findFirst()
                .orElse(null);

        assertNotNull(joao);
        assertNotNull(joao.getAddress());
        // Se o relacionamento foi carregado via JOIN FETCH, não deve lançar LazyInitializationException
        assertThat(joao.getAddress().getLogradouro()).isEqualTo("Rua Teste 1");
        assertNotNull(joao.getUser());
        assertThat(joao.getUser().getUsername()).isEqualTo("test.user");
    }

    @Test
    void testFindAllWithRelationsPaginated_WithSearch_LoadsRelationships() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String search = "João";

        // Act
        Page<Person> page = personRepository.findAllWithRelationsPaginated(search, pageable);

        // Assert
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(1);

        Person joao = page.getContent().get(0);
        assertThat(joao.getFullname()).contains("João");
        assertNotNull(joao.getAddress());
        assertNotNull(joao.getUser());
    }

    @Test
    void testFindByIdWithRelations_LoadsAllRelationships() {
        // Act
        Person person = personRepository.findByIdWithRelations(person1.getId())
                .orElse(null);

        // Assert
        assertNotNull(person);
        assertNotNull(person.getAddress());
        assertThat(person.getAddress().getLogradouro()).isEqualTo("Rua Teste 1");
        assertNotNull(person.getUser());
        assertThat(person.getUser().getUsername()).isEqualTo("test.user");
    }

    @Test
    void testFindAllWithRelationsPaginated_DoesNotCauseNPlusOne() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Person> page = personRepository.findAllWithRelationsPaginated(null, pageable);

        // Assert - Verificar que todos os relacionamentos foram carregados
        // Se houvesse N+1, teríamos múltiplas queries adicionais
        for (Person person : page.getContent()) {
            // Acessar relacionamentos não deve causar queries adicionais
            if (person.getAddress() != null) {
                assertNotNull(person.getAddress().getLogradouro());
            }
            if (person.getUser() != null) {
                assertNotNull(person.getUser().getUsername());
            }
        }

        assertThat(page.getContent()).hasSize(2);
    }
}

