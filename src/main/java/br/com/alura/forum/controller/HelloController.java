package br.com.alura.forum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//@Controller -> Srping MVC - Feito para que o Spring consiga encontrar esta classe
//o próprio Spring que fará o gerenciamento da classe
@Controller
public class HelloController {

	//dizer qual a URL que o Spring chamará esse método
	@RequestMapping("/")
	//senão o Spring vai entender que o retorno é um página. Neste caso, ele iria procurar a
	//página com o nome "Hello World!"
	//com essa anotação, ela retorna a própria String pro navegador
	@ResponseBody
	public String hello() {
		return "Helllo World!";
	}
}
