package com.wa.util;

import com.wa.model.Matriculation;
import com.wa.model.Person;
import com.wa.model.Process;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneratedDocumentFileNameBuilderTest {

    @Test
    void petitionPartFromTemplate_plainFile() {
        assertEquals("Replica_Piso", GeneratedDocumentFileNameBuilder.petitionPartFromTemplate("replica_piso.docx"));
    }

    @Test
    void petitionPartFromTemplate_withSubfolder() {
        assertEquals("Replica_Piso", GeneratedDocumentFileNameBuilder.petitionPartFromTemplate("piso/replica_piso.docx"));
    }

    @Test
    void petitionPartFromTemplate_windowsPath() {
        assertEquals("Peticao_Inicial_Piso",
                GeneratedDocumentFileNameBuilder.petitionPartFromTemplate("iniciais\\peticao_inicial_piso.docx"));
    }

    @Test
    void petitionPartFromTemplate_accentedBasename() {
        assertEquals("Replica_Piso", GeneratedDocumentFileNameBuilder.petitionPartFromTemplate("piso/réplica_piso.docx"));
    }

    @Test
    void clientPart_preservesMidWordCasingAndUnderscoresSpaces() {
        assertEquals("Maria_da_Silva", GeneratedDocumentFileNameBuilder.clientPart("Maria da Silva"));
    }

    @Test
    void clientPart_accentRemoval() {
        assertEquals("Maria_da_Silva", GeneratedDocumentFileNameBuilder.clientPart("Mária da Silva"));
    }

    @Test
    void safeFileToken_replacesInvalidCharacters() {
        assertEquals("12_34", GeneratedDocumentFileNameBuilder.safeFileToken("12/34"));
    }

    @Test
    void buildForProcess_fullExample() {
        Person person = new Person();
        person.setFullname("Maria da Silva");
        Matriculation mat = new Matriculation();
        mat.setPerson(person);
        Process process = new Process();
        process.setMatriculation(mat);
        process.setNumero("123456");

        assertEquals("Replica_Piso_Maria_da_Silva_123456",
                GeneratedDocumentFileNameBuilder.buildForProcess("piso/replica_piso.docx", process));
    }

    @Test
    void buildForClient_noProcessNumber() {
        Person person = new Person();
        person.setFullname("Maria da Silva");

        assertEquals("Replica_Piso_Maria_da_Silva",
                GeneratedDocumentFileNameBuilder.buildForClient("piso/replica_piso.docx", person));
    }
}
