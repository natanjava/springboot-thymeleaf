package demo.springbootthymeleaf.model;

public enum Cargo {
	
	/* implementação antiga, mais simples
	JUNIOR,
	PLENO,
	SENIOR
	*/
	
	/* implementação nova, mais elaborada */
	JUNIOR ("Junior"),
	PLENO ("Full"),
	SENIOR ("Senior");
	
	private String nome;
	
	private Cargo(String nome){
		this.nome = nome;
	}
	
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	

}
