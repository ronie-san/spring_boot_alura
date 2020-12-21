package br.com.alura.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//habilita suporte para conversão dos parâmetros de URL e converter para uma interface Pageable
@EnableSpringDataWebSupport
//habilita o uso de cache na aplicação
@EnableCaching
//documentação
@EnableSwagger2
//esta parte de baixo é SOMENTE para gerar um arquivo .war
//public class ForumApplication extends SpringBootServletInitializer {
//	
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		return builder.sources(ForumApplication.class);
//	}
public class ForumApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForumApplication.class, args);
	}

}
