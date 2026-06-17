package Security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Armazena e gerencia usuários em arquivo de texto.
 * Thread-safe via synchronized em todas as operações de escrita/leitura.
 * 
 * Formato do arquivo (config/users.dat):
 * Cada linha: id|username|senhaHash|salt|cargo|ativo|deveTrocarSenha|perm1;perm2;...
 */
public class UserStore {

	private final Path arquivoUsuarios;
	private final List<User> usuarios;
	private int proximoId;

	public UserStore(String caminhoArquivo) {
		this.arquivoUsuarios = Paths.get(caminhoArquivo);
		this.usuarios = new ArrayList<>();
		this.proximoId = 1;
		carregar();
	}

	// ==================== Carregar / Salvar ====================

	/**
	 * Carrega usuários do arquivo. Se não existir, cria admin padrão.
	 */
	private synchronized void carregar() {
		if (!Files.exists(arquivoUsuarios)) {
			System.out.println("[UserStore] Arquivo de usuários não encontrado. Criando admin padrão...");
			criarAdminPadrao();
			return;
		}

		try {
			List<String> linhas = Files.readAllLines(arquivoUsuarios);
			for (String linha : linhas) {
				if (linha.isBlank() || linha.startsWith("#")) continue;
				try {
					User user = User.deserializar(linha);
					usuarios.add(user);
					if (user.getId() >= proximoId) {
						proximoId = user.getId() + 1;
					}
				} catch (Exception e) {
					System.err.println("[UserStore] Erro ao parsear linha: " + e.getMessage());
				}
			}
			System.out.println("[UserStore] " + usuarios.size() + " usuário(s) carregado(s)");
		} catch (IOException e) {
			System.err.println("[UserStore] Erro ao carregar: " + e.getMessage());
			if (usuarios.isEmpty()) criarAdminPadrao();
		}
	}

	/**
	 * Cria o admin padrão na primeira execução.
	 */
	private void criarAdminPadrao() {
		String salt = SenhaUtil.gerarSalt();
		String hash = SenhaUtil.hashear("admin123", salt);
		User admin = new User(1, "admin", hash, salt, Role.ADMIN);
		admin.setDeveTrocarSenha(true);
		admin.adicionarPermissao(Permissao.total("/*"));
		usuarios.add(admin);
		proximoId = 2;
		salvar();
		System.out.println("╔══════════════════════════════════════════════╗");
		System.out.println("║  Admin padrão criado!                       ║");
		System.out.println("║  Usuário: admin                             ║");
		System.out.println("║  Senha:   admin123                          ║");
		System.out.println("║  ⚠ TROQUE A SENHA NO PRIMEIRO LOGIN!       ║");
		System.out.println("╚══════════════════════════════════════════════╝");
	}

	/**
	 * Salva todos os usuários no arquivo.
	 */
	private synchronized void salvar() {
		try {
			Files.createDirectories(arquivoUsuarios.getParent());
			List<String> linhas = new ArrayList<>();
			linhas.add("# Vaultra Secure Server — Usuários");
			linhas.add("# NÃO EDITE MANUALMENTE");
			linhas.add("# Formato: id|username|senhaHash|salt|cargo|ativo|deveTrocarSenha|permissoes");
			for (User user : usuarios) {
				linhas.add(user.serializar());
			}
			Files.write(arquivoUsuarios, linhas);
		} catch (IOException e) {
			System.err.println("[UserStore] Erro ao salvar: " + e.getMessage());
		}
	}

	// ==================== Consultas ====================

	/** Busca usuário por username (case-insensitive) */
	public synchronized Optional<User> buscarPorUsername(String username) {
		return usuarios.stream()
				.filter(u -> u.getUsername().equalsIgnoreCase(username))
				.findFirst();
	}

	/** Busca usuário por ID */
	public synchronized Optional<User> buscarPorId(int id) {
		return usuarios.stream()
				.filter(u -> u.getId() == id)
				.findFirst();
	}

	/** Lista todos os usuários */
	public synchronized List<User> listarTodos() {
		return Collections.unmodifiableList(new ArrayList<>(usuarios));
	}

	// ==================== CRUD ====================

	/**
	 * Cria novo usuário com permissões padrão do cargo.
	 * @return o usuário criado, ou null se username já existe
	 */
	public synchronized User criarUsuario(String username, String senha, Role cargo) {
		if (buscarPorUsername(username).isPresent()) {
			return null;
		}

		String salt = SenhaUtil.gerarSalt();
		String hash = SenhaUtil.hashear(senha, salt);
		User novoUser = new User(proximoId++, username, hash, salt, cargo);

		atribuirPermissoesPadrao(novoUser);

		usuarios.add(novoUser);
		salvar();
		System.out.println("[UserStore] Usuário criado: " + username + " (cargo: " + cargo.getNome() + ")");
		return novoUser;
	}

	/**
	 * Remove usuário por ID. Não permite remover o admin principal (ID=1).
	 */
	public synchronized boolean removerUsuario(int id) {
		if (id == 1) return false;
		boolean removido = usuarios.removeIf(u -> u.getId() == id);
		if (removido) {
			salvar();
			System.out.println("[UserStore] Usuário ID " + id + " removido");
		}
		return removido;
	}

	/**
	 * Atualiza permissões de um usuário.
	 */
	public synchronized boolean atualizarPermissoes(int id, List<Permissao> novasPermissoes) {
		Optional<User> opt = buscarPorId(id);
		if (opt.isEmpty()) return false;

		User user = opt.get();
		user.limparPermissoes();
		for (Permissao p : novasPermissoes) {
			user.adicionarPermissao(p);
		}
		salvar();
		System.out.println("[UserStore] Permissões atualizadas para: " + user.getUsername());
		return true;
	}

	/**
	 * Atualiza cargo de um usuário.
	 */
	public synchronized boolean atualizarCargo(int id, Role novoCargo) {
		Optional<User> opt = buscarPorId(id);
		if (opt.isEmpty()) return false;

		User user = opt.get();
		user.setCargo(novoCargo);
		salvar();
		System.out.println("[UserStore] Cargo de " + user.getUsername() + " alterado para: " + novoCargo.getNome());
		return true;
	}

	/**
	 * Atualiza senha de um usuário.
	 */
	public synchronized boolean atualizarSenha(int id, String novaSenha) {
		Optional<User> opt = buscarPorId(id);
		if (opt.isEmpty()) return false;

		User user = opt.get();
		user.setSenha(novaSenha);
		user.setDeveTrocarSenha(false);
		salvar();
		System.out.println("[UserStore] Senha alterada para: " + user.getUsername());
		return true;
	}

	// ==================== Permissões Padrão por Cargo ====================

	/**
	 * Atribui permissões padrão baseado no cargo do usuário.
	 * O admin pode alterar depois via API.
	 */
	private void atribuirPermissoesPadrao(User user) {
		switch (user.getCargo()) {
			case ADMIN -> {
				user.adicionarPermissao(Permissao.total("/*"));
			}
			case MODELADOR -> {
				user.adicionarPermissao(Permissao.verEditar("/workspace/modelagem/*"));
				user.adicionarPermissao(Permissao.somenteVer("/workspace/docs/*"));
			}
			case ARQUITETO -> {
				user.adicionarPermissao(Permissao.verEditar("/workspace/arquitetura/*"));
				user.adicionarPermissao(Permissao.somenteVer("/workspace/docs/*"));
			}
			case DESENVOLVEDOR -> {
				user.adicionarPermissao(Permissao.verEditar("/workspace/src/*"));
				user.adicionarPermissao(Permissao.verEditar("/workspace/docs/*"));
			}
			case VISUALIZADOR -> {
				user.adicionarPermissao(Permissao.somenteVer("/workspace/*"));
			}
		}
	}
}
