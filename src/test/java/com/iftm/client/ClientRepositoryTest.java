package com.iftm.client;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ClientRepositoryTest {
    @Autowired
    private ClientRepository clienteRepository;

    @Test
    @DisplayName("Verificar se a busca por nome retorna o cliente correto.")
    public void testarBuscaPorNomeRetornaClienteCorreto() {
        String nomeExistente = "Clarice Lispector";
        String cpfExistente = "10919444522";
        Client clienteExistente = new Client(3L, nomeExistente, cpfExistente, null, null, null);

        Optional<Client> respostaObtidaExistente = clienteRepository.findByNameIgnoreCase(nomeExistente);
        Assertions.assertThat(respostaObtidaExistente).isPresent();
        Assertions.assertThat(respostaObtidaExistente.get()).isEqualTo(clienteExistente);

        String nomeNaoExistente = "Armando Costa";
        Optional<Client> respostaObtidaNaoExistente = clienteRepository.findByNameIgnoreCase(nomeNaoExistente);
        Assertions.assertThat(respostaObtidaNaoExistente).isNotPresent();
    }

    @Test
    @DisplayName("Verificar se a busca por nome similar retorna a lista de clientes correta.")
    public void testarBuscaPorNomeSimilarRetornaListaCorreta() {
        String nomeExistente = "Jorge Amado";
        String nomeNaoExistente = "Márcio Alfredo";
        String nomeVazio = "";

        List<Client> clientesComNomeExistente = clienteRepository.searchByName(nomeExistente);
        assertFalse(clientesComNomeExistente.isEmpty());
        // Verificar se a lista de clientes contém todos os clientes com parte do nome "Silva"
        assertTrue(clientesComNomeExistente.stream().allMatch(c -> c.getName().contains(nomeExistente)));

        List<Client> clientesComNomeNaoExistente = clienteRepository.searchByName(nomeNaoExistente);
        assertTrue(clientesComNomeNaoExistente.isEmpty());

        List<Client> todosClientes = clienteRepository.findAll();
        List<Client> clientesComNomeVazio = clienteRepository.searchByName(nomeVazio);
        // Verificar se a lista de clientes com nome vazio contém todos os clientes
        assertEquals(clientesComNomeVazio, todosClientes);
    }

    @Test
    @DisplayName("Verificar se a busca por salário superior retorna os clientes corretos")
    public void testarBuscaPorSalarioSuperiorRetornaClientesCorretos() {
        Double salario = 10000.00;
        List<Client> clientesComSalarioSuperior = clienteRepository.buscarClientesComSalarioMaiorQue(salario);
        for (Client cliente : clientesComSalarioSuperior) {
            assertTrue(cliente.getIncome() < salario);
        }
    }

    @Test
    @DisplayName("Verificar se a busca por salário inferior retorna os clientes corretos")
    public void testarBuscaPorSalarioInferiorRetornaClientesCorretos() {
        Double salario = 10000.00;
        List<Client> clientesComSalarioInferior = clienteRepository.buscarClientesComSalarioMenorQue(salario);
        for (Client cliente : clientesComSalarioInferior) {
            assertTrue(cliente.getIncome() < salario);
        }
    }

    @Test
    @DisplayName("Verificar se a busca por salário no intervalo retorna os clientes corretos")
    public void testarBuscaPorSalarioNoIntervaloRetornaClientesCorretos() {
        Double salarioInicial = 4000.00;
        Double salarioFinal = 6000.00;
        List<Client> clientesComSalarioNoIntervalo = clienteRepository.buscarClientesPorFaixaDeSalario(salarioInicial, salarioFinal);
        assertTrue(clientesComSalarioNoIntervalo.stream()
                .allMatch(c -> c.getIncome().compareTo(salarioInicial) >= 0 && c.getIncome().compareTo(salarioFinal) <= 0));
    }

    @Test
    @DisplayName("Verificar se a busca por data de aniversário retorna os clientes corretos")
    public void testarBuscaPorDataDeAniversarioRetornaClientesCorretos() {
        Instant dataInicio = Instant.parse("2017-12-25T20:30:50Z");
        Instant dataFim = Instant.now();
        List<Client> clientesComDataDeAniversario = clienteRepository.findClientByBirthDateBetween(dataInicio, dataFim);
        assertTrue(clientesComDataDeAniversario.stream().allMatch(c -> {
            LocalDate dataDeAniversario = c.getBirthDate().atZone(ZoneOffset.UTC).toLocalDate();
            return dataDeAniversario != null && dataDeAniversario.isAfter(LocalDate.parse("1979-12-31")) && dataDeAniversario.isBefore(LocalDate.now().plusDays(1));
        }));
    }

    @Test
    public void testarAtualizacaoDeCliente() {

        Client cliente = new Client();
        cliente.setName("Carlos");
        cliente.setCpf("4324566784");
        cliente.setIncome(3000.0);
        cliente.setBirthDate(Instant.parse("2003-07-01T00:00:00Z"));


        cliente = clienteRepository.save(cliente);

        cliente.setName("Rayanne");
        cliente.setIncome(2000.0);
        cliente.setBirthDate(Instant.parse("1995-05-05T00:00:00Z"));

        cliente = clienteRepository.save(cliente);

        Optional<Client> clienteAtualizado = clienteRepository.findById(cliente.getId());
        assertTrue(clienteAtualizado.isPresent());
        assertEquals("Rayanne", clienteAtualizado.get().getName());
        assertEquals(2000.0, clienteAtualizado.get().getIncome(), 0.001);
        assertEquals(Instant.parse("1995-05-05T00:00:00Z"), clienteAtualizado.get().getBirthDate());
    }

}