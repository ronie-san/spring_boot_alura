package br.com.alura.forum.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.alura.forum.modelo.Curso;

//específico para Repository
@DataJpaTest
//caso a configuração de banco seja outra, fora o h2
// @AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class CursoRepositoryTest {

	@Autowired
	private CursoRepository repository;
	
	@Test
	public void findCurso() {
		String nomeCurso = "HTML 5";
		Curso curso = repository.findByNome(nomeCurso);
		Assertions.assertNotNull(curso);
		Assertions.assertEquals(nomeCurso, curso.getNome());
	}

}
