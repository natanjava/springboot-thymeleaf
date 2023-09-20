package demo.springbootthymeleaf.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import demo.springbootthymeleaf.model.Pessoa;
import demo.springbootthymeleaf.model.Telefone;
import demo.springbootthymeleaf.repository.PessoaRepository;
import demo.springbootthymeleaf.repository.ProfissaoRepository;
import demo.springbootthymeleaf.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;
	

	@RequestMapping(method = RequestMethod.GET, value = "/cadastroPessoa")
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");

		modelAndView.addObject("pessoaobj", new Pessoa());
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome")));
		modelAndView.addObject("pessoas", pessoasIt);
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
				
		return modelAndView;
	}
	
	@GetMapping("/pessoaspag")
	public ModelAndView carregarPessoasPorPaginacao(
			@PageableDefault(size = 5) Pageable pageable,
			ModelAndView model, @RequestParam("nomePesquisa") String nomPesquisa) {
		
		Page<Pessoa> pagePessoa = pessoaRepository.findByNamePage(nomPesquisa, pageable);
		model.addObject("pessoas", pagePessoa);
		model.addObject("pessoaobj", new Pessoa());
		model.addObject("nomePesquisa", nomPesquisa);
		model.setViewName("cadastro/cadastroPessoa");
		
		return model;		
	}
	

	@RequestMapping(method = RequestMethod.POST, 
					value = {"**/salvarPessoa"},
					consumes = {"multipart/form-data"})
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file) throws IOException {
		
		// @Valid to activate the validations and BindingResult to get the results of that
		// always when it saves or edit a register, it´s necessary to load and set again the Phone List to avoid problems
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
				
		
		
		if (bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
			/*
			Iterable<Pessoa> pessoasIt = pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome")));
			modelAndView.addObject("pessoas", pessoasIt);
			*/
			modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
			modelAndView.addObject("pessoaobj", pessoa);
			modelAndView.addObject("profissoes", profissaoRepository.findAll());
			
			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); // vem das @notações dos campos da classe model
				
			}
			modelAndView.addObject("msg", msg);
			
			return modelAndView;
		}
		
		if (file.getSize() > 0) { /*cadastrando curriculo */
			pessoa.setCurriculo(file.getBytes());
			pessoa.setTipoFileCurriculo(file.getContentType());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
		} else {												/* editando usuario que ja existe */		
			if (pessoa.getId() != null && pessoa.getId() > 0) { /* não pode perder o que esta salvo */
				
				Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get(); /*carrega a pessoa do banco*/
				pessoa.setCurriculo(pessoaTemp.getCurriculo());
				pessoa.setTipoFileCurriculo(pessoaTemp.getTipoFileCurriculo());
				pessoa.setNomeFileCurriculo(pessoaTemp.getNomeFileCurriculo());
			}
		}
		
		pessoaRepository.save(pessoa);

		ModelAndView modelAndView2 = new ModelAndView("cadastro/cadastroPessoa");
		//Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		modelAndView2.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView2.addObject("pessoaobj", new Pessoa());
		modelAndView2.addObject("profissoes", profissaoRepository.findAll());
		
		return modelAndView2;

		// return "cadastro/cadastroPessoa";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/listaPessoas")
	public ModelAndView listar() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		//Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("pessoaobj", new Pessoa());
		return modelAndView;
	}

	// @RequestMapping aqui podria usar o RequestMappin mas melhor usar notacao nova
	// do Spring
	@GetMapping("editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("id"))));

		return modelAndView;
	}
	
	@GetMapping("removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {

		pessoaRepository.deleteById(idpessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome")))); // rerona lista atualizada
		modelAndView.addObject("pessoaobj", new Pessoa());

		return modelAndView;
	}
	
	@PostMapping("**/pesquisarPessoa")
	public ModelAndView pesquisar(
			@RequestParam("nomePesquisa") String nomePesquisa,
			@RequestParam("sexoPesquisa") String sexoPesquisa,
			@PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
		
		Page<Pessoa> pessoas = null;
		
		if (sexoPesquisa != null && !sexoPesquisa.isEmpty() 
				&& nomePesquisa != null && !nomePesquisa.isEmpty()) {
			pessoas = pessoaRepository.findByNameSexoPage(nomePesquisa, sexoPesquisa, pageable);
		} else if (nomePesquisa != null && !nomePesquisa.isEmpty()) {
			pessoas = pessoaRepository.findByNamePage(nomePesquisa, pageable);
		} else if (sexoPesquisa != null && !sexoPesquisa.isEmpty()) {
			pessoas = pessoaRepository.findBySexoPage(sexoPesquisa, pageable);
		}
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("nomePesquisa", nomePesquisa);
		modelAndView.addObject("sexoPesquisa", sexoPesquisa);
		return modelAndView;
	}
	
	
	@GetMapping("telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);

		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));

		return modelAndView;
	}
	
	@PostMapping("**/addfonePessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		
		if(telefone != null && (telefone.getNumero() != null && telefone.getNumero().isEmpty())
				|| telefone.getNumero() == null)  {
			
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
			modelAndView.addObject("pessoaobj", pessoa);
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			List<String> msg = new ArrayList<String>();
			msg.add("Numero deve ser informado");
			modelAndView.addObject("msg", msg);
			return modelAndView;
		}
		
		
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone); 
		
		modelAndView.addObject("pessoaobj", pessoa); // isso faz manter os dados da pessoa na tela
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
		
		return modelAndView; 
	}
	
	
	@GetMapping("removertelefone/{idtelefone}")
	public ModelAndView excluirtelefone(@PathVariable("idtelefone") Long idtelefone) {

		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		telefoneRepository.deleteById(idtelefone);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId())); // rerona lista atualizada

		return modelAndView;
	}
	
	@GetMapping("**/baixarcurriculo/{idpessoa}")
	public void baixarcurriculo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response) throws IOException {
		
		//Consultar o objeto pessoa no banco de dados*/
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		if (pessoa.getCurriculo() != null) {
			
			/*setar o tamanho da resposta*/
			response.setContentLength(pessoa.getCurriculo().length);
			
			/*tipo do arquivo pra download ou pode ser generica application/octet-stream*/
			response.setContentType(pessoa.getTipoFileCurriculo());

			/*define o header da resposta*/
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment;filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerKey, headerValue);
			
			//Finaliza a resposta pssando o arquivo*/
			response.getOutputStream().write(pessoa.getCurriculo());
		} else {
			// page set a response 
		}
	}
	

	@RequestMapping(method = RequestMethod.GET, value = "/infoPage")
	public String infoPage() {
		return "infoPage";
	}
	
	
	

}
