package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesTopicoDTO;
import br.com.alura.forum.controller.dto.TopicoDTO;
import br.com.alura.forum.controller.form.AtualiazacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

//@Controller
@RestController // avisa ao Spring que se trata de um tipo específico de Controller
//assim, não é mais necessário ficar colocando a anotação @ResquestBody nos métodos
@RequestMapping("/topicos")
public class TopicosController {

	@Autowired
	private TopicoRepository topicoRepository;
	@Autowired
	private CursoRepository cursoRepository;

	// @RequestMapping("/topicos") // endereço http://localhost:8080/topicos
	// @ResponseBody
	// classes DTO = classes VO (só para retornar as propriedades desejadas, caso
	// envie a classe do modelo,
	// o conversor padrão retornará TUDO
	// @RequestParam(required = true) <- considera que o parâmetro é obrigatório
	// (padrão). Caso esteja anotado
	// e não enviarem o parâmetro, o Spring devolve erro 400
	@GetMapping
	// value <- serve como um "id" para o Cacheable
	@Cacheable(value = "listaTopicos")
	public Page<TopicoDTO> lista(@RequestParam(required = false) String nomeCurso,
			@PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 10) Pageable paginacao) {
//	public Page<TopicoDTO> lista(@RequestParam(required = false) String nomeCurso, @RequestParam int pagina,
//			@RequestParam int qtd, @RequestParam String ordenacao) { // os parâmetros vêm na URL
		// (?parametro1=...;parametro2=...)
		// qtd <- quantidade por página
		// pagina <- index da página (início = zero)
		// ordenacao <- nome do campo a ordernar. Se vier um campo que não existe, o
		// Spring retorna erro 500

		// com o parâmetro Pageable:
		// qtd vira size
		// pagina vira page
		// ordenacao vira sort. Além disso, adicionando ",asc" ou ",desc" já ordena
		// @PageableDefault <- caso não seja enviado os parâmetros, este será
		// considerado

		Page<Topico> topicos;
		if (nomeCurso == null) {
			topicos = topicoRepository.findAll(paginacao);
		} else {
			// para filtrar por uma prop dentro de uma classe é só concatenar a classe com o
			// atributo, separando com _!
			topicos = topicoRepository.findByCurso_Nome(nomeCurso, paginacao);
		}
		return TopicoDTO.convert(topicos);
	}

	@PostMapping
	// se o retorno é void, retorna código 200 se sucesso e 500 se erro
	// uriBuilder: o próprio Spring se encarrega dele!
	// @Valid: diz pro Spring pra utilizar as anotações de BeanValidation da classe
	// TopicoForm
	// @RequestBody <- os parâmetros vêm no corpo da requisição
	@Transactional
	// limpa o cache relativo ao cache passado
	@CacheEvict(value = "listaTopicos", allEntries = true)
	public ResponseEntity<TopicoDTO> cadastrar(@RequestBody @Valid TopicoForm topicoForm,
			UriComponentsBuilder uriBuilder) {
		Topico topico = topicoForm.convert(cursoRepository);
		topicoRepository.save(topico);

		// retorna código 201 + cabeçalho HTTP chamado Location com a URL do novo
		// recurso criado +
		// representação do novo recurso criado no corpo da resposta
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDTO(topico));
	}

	@GetMapping("/{id}")
	// @PathVariable <- diz que o parâmetro virá na URL ao invés de parâmetro (?id=)
	// caso o nome do parâmetro seja diferente do anotado em @GetMapping, é só fazer
	// @PathVariable("[NOME EM GETMAPPING]")
	public ResponseEntity<DetalhesTopicoDTO> detalhar(@PathVariable Long id) {
		// //getOne <- dá exception se o id não existir!
		// Topico topico = topicoRepository.getOne(id);
		Optional<Topico> optional = topicoRepository.findById(id);

		if (optional.isPresent()) {
			return ResponseEntity.ok(new DetalhesTopicoDTO(optional.get()));
		}

		return ResponseEntity.notFound().build();
	}

	@PutMapping("/{id}")
	// o JPA já se encarrega de atualizar no banco de dados ao final deste método!
	@Transactional
	@CacheEvict(value = "listaTopicos", allEntries = true)
	public ResponseEntity<TopicoDTO> atualizar(@PathVariable Long id,
			@RequestBody @Valid AtualiazacaoTopicoForm topicoForm) {
		// Topico topico = topicoForm.atualizar(id, topicoRepository);
		// return ResponseEntity.ok(new TopicoDTO(topico));
		Optional<Topico> optional = topicoRepository.findById(id);

		if (optional.isPresent()) {
			Topico topico = topicoForm.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDTO(topico));
		}

		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaTopicos", allEntries = true)
	public ResponseEntity<?> remover(@PathVariable Long id) {
//		topicoRepository.deleteById(id);
//		return ResponseEntity.ok().build();
		Optional<Topico> optional = topicoRepository.findById(id);

		if (optional.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}

		return ResponseEntity.notFound().build();
	}
}