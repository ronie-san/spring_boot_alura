package br.com.alura.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.alura.forum.repository.UsuarioRepository;

//quando não tem coisas nessa classe, o Spring bloqueia TODAS as requisições
@EnableWebSecurity
@Configuration
@Profile(value = { "prod", "test" })
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {

	@Autowired
	private AutenticacaoService autenticacaoService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	// serve para configurar a autenticação (controle de acesso, login)
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
	}

	// serve para configurar a autorização (URL, quem pode acessar, perfis de
	// acesso)
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// permite todas as requisições GET para "/topicos"
		// permite todas as requisições GET para "/topicos/QUALQUERCOISA"
		// todas as outras necessitam de autenticação e para autenticar, aparecerá uma
		// telinha de login (padrão do Spring)
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/topicos").permitAll()
				.antMatchers(HttpMethod.GET, "/topicos/*").permitAll().anyRequest().authenticated()
				.antMatchers(HttpMethod.POST, "/auth").permitAll()
				// precisa de autorização
				// no banco de dados, o perfil DEVE SER "ROLE_MODERADOR"
				.antMatchers(HttpMethod.DELETE, "/topicos/*").hasRole("MODERADOR")
				//para produção, NÃO PODE DEIXAR!
				.antMatchers(HttpMethod.GET, "/actuator").permitAll()
				.antMatchers(HttpMethod.GET, "/actuator/**").permitAll()
				//.and().formLogin();
				.and().csrf().disable() //obrigatório para previnir ataques
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().addFilterBefore(new AutenticacaoTokenFilter(tokenService, usuarioRepository), UsernamePasswordAuthenticationFilter.class);
	}

	// serve para configurar de recursos estáticos (arquivo CSS, JS, imagens, etc)
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**", "/swagger-resources/**");
	}
}