package com.wa.repository;

import com.wa.model.Process;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {
       List<Process> findByMatriculationId(Long matriculationId);

       @Query("SELECT p FROM Process p WHERE p.matriculation.person.id = :personId")
       List<Process> findByPersonId(@Param("personId") Long personId);

       @Query("SELECT DISTINCT p FROM Process p " +
                     "LEFT JOIN FETCH p.matriculation m " +
                     "LEFT JOIN FETCH m.person " +
                     "LEFT JOIN FETCH p.moviments mov " +
                     "LEFT JOIN FETCH mov.recurso " +
                     "WHERE p.id = :id")
       Optional<Process> findByIdWithMoviments(@Param("id") Long id);

       @Query("SELECT DISTINCT p FROM Process p " +
                     "LEFT JOIN FETCH p.matriculation m " +
                     "LEFT JOIN FETCH m.person per " +
                     "LEFT JOIN FETCH per.address " +
                     "WHERE p.id = :id")
       Optional<Process> findByIdWithRelations(@Param("id") Long id);

       @Query("SELECT DISTINCT p FROM Process p " +
                     "LEFT JOIN FETCH p.matriculation m " +
                     "LEFT JOIN FETCH m.person")
       List<Process> findAllWithRelations();

       @Query("SELECT DISTINCT p FROM Process p " +
                     "LEFT JOIN p.matriculation m " +
                     "LEFT JOIN m.person per " +
                     "WHERE (:numero IS NULL OR :numero = '' OR LOWER(p.numero) LIKE LOWER(CONCAT('%', :numero, '%'))) "
                     +
                     "AND (:nomeCliente IS NULL OR :nomeCliente = '' OR LOWER(per.fullname) LIKE LOWER(CONCAT('%', :nomeCliente, '%'))) "
                     +
                     "AND (:comarca IS NULL OR :comarca = '' OR p.comarca = :comarca) "
                     +
                     "AND (:vara IS NULL OR :vara = '' OR p.vara = :vara) " +
                     "AND (:tipoProcesso IS NULL OR :tipoProcesso = '' OR p.tipoProcesso = :tipoProcesso) "
                     +
                     "AND (:status IS NULL OR :status = '' OR p.status = :status) " +
                     "AND (:showArchived = true OR p.status IS NULL OR LOWER(p.status) NOT LIKE '%arquivado%')")
       Page<Process> findAllWithRelationsPaginated(
                     @Param("numero") String numero,
                     @Param("nomeCliente") String nomeCliente,
                     @Param("comarca") String comarca,
                     @Param("vara") String vara,
                     @Param("tipoProcesso") String tipoProcesso,
                     @Param("status") String status,
                     @Param("showArchived") Boolean showArchived,
                     Pageable pageable);

       @Query("SELECT DISTINCT p.comarca FROM Process p WHERE p.comarca IS NOT NULL AND LENGTH(TRIM(p.comarca)) > 0 ORDER BY p.comarca ASC")
       List<String> findDistinctComarcas();

       @Query("SELECT DISTINCT p.vara FROM Process p WHERE p.vara IS NOT NULL AND LENGTH(TRIM(p.vara)) > 0 ORDER BY p.vara ASC")
       List<String> findDistinctVaras();

       @Query("SELECT DISTINCT p.tipoProcesso FROM Process p WHERE p.tipoProcesso IS NOT NULL AND LENGTH(TRIM(p.tipoProcesso)) > 0 ORDER BY p.tipoProcesso ASC")
       List<String> findDistinctTipoProcesso();

       @Query("SELECT DISTINCT p FROM Process p " +
                     "LEFT JOIN FETCH p.matriculation m " +
                     "LEFT JOIN FETCH m.person per " +
                     "WHERE per.id = :personId")
       List<Process> findByPersonIdWithRelations(@Param("personId") Long personId);

       /**
        * Busca apenas os IDs dos processos que precisam de correção
        * (valorOriginal > 0 e distribuidoEm não nulo)
        * Retorna apenas IDs para economizar memória
        */
       @Query("SELECT p.id FROM Process p WHERE p.valorOriginal IS NOT NULL AND p.valorOriginal > 0 AND p.distribuidoEm IS NOT NULL")
       List<Long> findIdsForCorrection();

       /**
        * Busca processos por IDs em lotes, sem carregar relacionamentos desnecessários
        */
       @Query("SELECT p FROM Process p WHERE p.id IN :ids")
       List<Process> findByIds(@Param("ids") List<Long> ids);

       /**
        * Busca processos que precisam correção usando paginação
        */
       @Query("SELECT p FROM Process p WHERE p.valorOriginal IS NOT NULL AND p.valorOriginal > 0 AND p.distribuidoEm IS NOT NULL")
       Page<Process> findProcessesForCorrection(Pageable pageable);

       /**
        * Conta processos por tipo (query agregada para economizar memória)
        */
       @Query("SELECT p.tipoProcesso, COUNT(p) FROM Process p WHERE p.tipoProcesso IS NOT NULL GROUP BY p.tipoProcesso")
       List<Object[]> countByType();

       /**
        * Conta processos por comarca e tipo (query agregada)
        */
       @Query("SELECT p.comarca, p.tipoProcesso, COUNT(p) FROM Process p WHERE p.comarca IS NOT NULL AND p.tipoProcesso IS NOT NULL GROUP BY p.comarca, p.tipoProcesso")
       List<Object[]> countByComarcaAndType();

       /**
        * Conta processos por status e tipo (query agregada)
        */
       @Query("SELECT p.status, p.tipoProcesso, COUNT(p) FROM Process p WHERE p.status IS NOT NULL AND p.tipoProcesso IS NOT NULL GROUP BY p.status, p.tipoProcesso")
       List<Object[]> countByStatusAndType();

       /**
        * Soma valores originais de todos os processos
        */
       @Query("SELECT COALESCE(SUM(p.valorOriginal), 0) FROM Process p WHERE p.valorOriginal IS NOT NULL")
       Double sumValorOriginal();

       /**
        * Soma valores corrigidos de todos os processos
        */
       @Query("SELECT COALESCE(SUM(p.valorCorrigido), 0) FROM Process p WHERE p.valorCorrigido IS NOT NULL")
       Double sumValorCorrigido();

       /**
        * Busca processos agrupados por tipo para cálculo de honorários
        * Retorna apenas os campos necessários: id, tipoProcesso, valorOriginal,
        * valorCorrigido,
        * previsaoHonorariosContratuais, previsaoHonorariosSucumbenciais
        */
       @Query("SELECT p.id, p.tipoProcesso, p.valorOriginal, p.valorCorrigido, " +
                     "p.previsaoHonorariosContratuais, p.previsaoHonorariosSucumbenciais " +
                     "FROM Process p WHERE p.tipoProcesso IS NOT NULL")
       List<Object[]> findProcessesForHonorarios();

       /**
        * Pares (id, numero) distintos do TJRJ (segmento CNJ .8.19.), excluindo arquivados.
        * Usa MIN(p.id) para garantir um id estável quando há mais de um processo com o mesmo número.
        * {@code %arquivad%} cobre "arquivado" e "arquivada"; status só "ARQ" também é excluído.
        */
       @Query("SELECT MIN(p.id), p.numero FROM Process p " +
                     "WHERE LOWER(p.numero) LIKE '%.8.19.%' " +
                     "AND (p.status IS NULL OR (LOWER(p.status) NOT LIKE '%arquivad%' "
                     + "AND UPPER(TRIM(p.status)) <> 'ARQ')) " +
                     "GROUP BY p.numero")
       List<Object[]> findIdAndNumeroTjrjAtivos();
}
