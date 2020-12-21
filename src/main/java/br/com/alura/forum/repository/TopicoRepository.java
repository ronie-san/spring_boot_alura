package br.com.alura.forum.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.alura.forum.modelo.Topico;

//não é necessário colocar anotações por se tratar de uma interface
public interface TopicoRepository extends JpaRepository<Topico, Long> {

	Page<Topico> findByCurso_Nome(String nomeCurso, Pageable paginacao);

	@Query("select t from Topico t where t.curso.nome = :nomeCurso")
	List<Topico> getLstByNomeCurso(@Param("nomeCurso") String nomeCurso);
}
