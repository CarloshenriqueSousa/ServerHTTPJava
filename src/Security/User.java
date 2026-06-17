package Security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa um usuário do sistema.
 * Cada usuário possui um cargo (Role) e permissões granulares
 * que definem exatamente quais pastas/recursos pode acessar.
 */
public class User {

	private final int id;
	private final String username;
	private String senhaHash;
	private String salt;
	private Role cargo;
	private boolean ativo;
	private boolean deveTrocarSenha;
	private final List<Permissao> permissoes;

	public User(int id, String username, String senhaHash, String salt, Role cargo) {
		this.id = id;
		this.username = username;
		this.senhaHash = senhaHash;
		this.salt = salt;
		this.cargo = cargo;
		this.ativo = true;
		this.deveTrocarSenha = false;
		this.permissoes = new ArrayList<>();
	}

	// ==================== Getters ====================

	public int getId() { return id; }
	public String getUsername() { return username; }
	public String getSenhaHash() { return senhaHash; }
	public String getSalt() { return salt; }
	public Role getCargo() { return cargo; }
	public boolean isAtivo() { return ativo; }
	public boolean isDeveTrocarSenha() { return deveTrocarSenha; }
	public List<Permissao> getPermissoes() { return Collections.unmodifiableList(permissoes); }

	// ==================== Setters ====================

	public void setCargo(Role cargo) { this.cargo = cargo; }
	public void setAtivo(boolean ativo) { this.ativo = ativo; }
	public void setDeveTrocarSenha(boolean deveTrocarSenha) { this.deveTrocarSenha = deveTrocarSenha; }

	/**
	 * Atualiza a senha gerando novo salt e hash.
	 */
	public void setSenha(String novaSenha) {
		this.salt = SenhaUtil.gerarSalt();
		this.senhaHash = SenhaUtil.hashear(novaSenha, this.salt);
	}

	/**
	 * Verifica se a senha fornecida corresponde ao hash armazenado.
	 */
	public boolean verificarSenha(String senha) {
		return SenhaUtil.verificar(senha, this.senhaHash, this.salt);
	}

	// ==================== Permissões ====================

	public void adicionarPermissao(Permissao permissao) {
		permissoes.add(permissao);
	}

	public void limparPermissoes() {
		permissoes.clear();
	}

	/** Verifica se o usuário pode VISUALIZAR determinado caminho */
	public boolean podeVer(String caminho) {
		if (cargo == Role.ADMIN) return true;
		return permissoes.stream()
				.anyMatch(p -> p.cobreCaminho(caminho) && p.isPodeVer());
	}

	/** Verifica se o usuário pode EDITAR determinado caminho */
	public boolean podeEditar(String caminho) {
		if (cargo == Role.ADMIN) return true;
		return permissoes.stream()
				.anyMatch(p -> p.cobreCaminho(caminho) && p.isPodeEditar());
	}

	/** Verifica se o usuário pode DELETAR determinado caminho */
	public boolean podeDeletar(String caminho) {
		if (cargo == Role.ADMIN) return true;
		return permissoes.stream()
				.anyMatch(p -> p.cobreCaminho(caminho) && p.isPodeDeletar());
	}

	/** Verifica se o usuário pode CRIAR em determinado caminho */
	public boolean podeCriar(String caminho) {
		if (cargo == Role.ADMIN) return true;
		return permissoes.stream()
				.anyMatch(p -> p.cobreCaminho(caminho) && p.isPodeCriar());
	}

	// ==================== Serialização ====================

	/**
	 * Serializa para formato de armazenamento.
	 * Formato: id|username|senhaHash|salt|cargo|ativo|deveTrocarSenha|perm1;perm2;...
	 */
	public String serializar() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append("|");
		sb.append(username).append("|");
		sb.append(senhaHash).append("|");
		sb.append(salt).append("|");
		sb.append(cargo.name()).append("|");
		sb.append(ativo).append("|");
		sb.append(deveTrocarSenha).append("|");

		for (int i = 0; i < permissoes.size(); i++) {
			if (i > 0) sb.append(";");
			sb.append(permissoes.get(i).serializar());
		}
		return sb.toString();
	}

	/**
	 * Deserializa do formato de armazenamento.
	 */
	public static User deserializar(String linha) {
		String[] partes = linha.split("\\|", 8);
		if (partes.length < 7) {
			throw new IllegalArgumentException("Formato de usuário inválido: " + linha);
		}

		int id = Integer.parseInt(partes[0]);
		String username = partes[1];
		String senhaHash = partes[2];
		String salt = partes[3];
		Role cargo = Role.fromString(partes[4]);
		boolean ativo = Boolean.parseBoolean(partes[5]);
		boolean deveTrocarSenha = Boolean.parseBoolean(partes[6]);

		User user = new User(id, username, senhaHash, salt, cargo);
		user.setAtivo(ativo);
		user.setDeveTrocarSenha(deveTrocarSenha);

		// Parse permissões (campo 8, opcional)
		if (partes.length == 8 && !partes[7].isBlank()) {
			String[] permStrs = partes[7].split(";");
			for (String permStr : permStrs) {
				if (!permStr.isBlank()) {
					user.adicionarPermissao(Permissao.deserializar(permStr));
				}
			}
		}

		return user;
	}
}
