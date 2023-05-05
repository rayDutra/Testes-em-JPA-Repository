package com.iftm.client.repositories;

import com.iftm.client.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Repository
    public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query(value = "SELECT c FROM Client c WHERE c.id = :id")
    public Optional<Client> findById(Long id);

    @Query("SELECT c FROM Client c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Client> findByNameIgnoreCase(String name);

    @Query(value = "SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(concat('%', :name, '%'))")
    public List<Client> searchByName(String name);

    @Query("SELECT c FROM Client c WHERE c.income > :salario")
    List<Client> buscarClientesComSalarioMaiorQue(@Param("salario") Double salario);

    @Query("SELECT c FROM Client c WHERE c.income < :salario")
    List<Client> buscarClientesComSalarioMenorQue(@Param("salario") Double salario);

    @Query("SELECT c FROM Client c WHERE c.income BETWEEN :salarioMinimo AND :salarioMaximo")
    List<Client> buscarClientesPorFaixaDeSalario(@Param("salarioMinimo") Double salarioMinimo, @Param("salarioMaximo") Double salarioMaximo);

    List<Client> findClientByBirthDateBetween(Instant DataInicio, Instant DataTermino);


}