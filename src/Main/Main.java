package Main;

import Data.JsonBuilder;
import Data.StaticFilesHandler;
import Http.HttpException;
import Http.HttpResponse;
import Http.HttpServer;
import Http.HttpStatus;
import Router.Router;
import Security.DashboardPages;
import Security.LoginPages;
import Security.Role;
import Security.User;
import Security.UserStore;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

	public static void main(String[] args) throws Exception {
		Router router = new Router();

		// Rota inicial pública
		router.get("/api/info", req -> HttpResponse.ok()
				.html("""
						<!DOCTYPE html>
                    <html lang="pt-BR">
                    <head>
                      <meta charset="UTF-8">
                      <title>Servidor Java HTTP</title>
                      <style>
                        body { font-family: monospace; max-width: 700px; margin: 40px auto; padding: 0 20px; }
                        h1   { color: #c0392b; }
                        a    { color: #2980b9; }
                        code { background: #f0f0f0; padding: 2px 6px; border-radius: 3px; }
                        pre  { background: #1e1e1e; color: #d4d4d4; padding: 16px; border-radius: 6px; }
                        .tag { background: #2980b9; color: white; padding: 2px 8px;
                               border-radius: 3px; font-size: 12px; margin-right: 6px; }
                      </style>
                    </head>
                    <body>
                      <h1>Servidor HTTP em Java puro</h1>
                      <p>Construído do zero — sem frameworks, sem dependências externas.</p>
                      <hr>
                      <h2>Rotas disponíveis</h2>
                      <ul>
                        <li><span class="tag">GET</span>  <a href="/login">/login</a> (Página de login segura)</li>
                        <li><span class="tag">GET</span>  <a href="/api/info-server">/api/info-server</a></li>
                        <li><span class="tag">GET</span>  <a href="/api/echo?mensagem=ola">/api/echo?mensagem=ola</a></li>
                      </ul>
                      <h2>Testando com curl</h2>
                      <pre>
                    curl http://localhost:8080/api/info-server
                    curl "http://localhost:8080/api/echo?mensagem=Java+puro"
                      </pre>
                    </body>
                    </html>
						"""));

		router.get("/", req -> {
			if (req.getUser() != null) {
				if (req.getUser().getCargo() == Role.ADMIN) {
					return HttpResponse.redirect("/workspace/dashboard");
				} else {
					return HttpResponse.redirect("/workspace/home");
				}
			}
			return HttpResponse.ok().html(LoginPages.paginaLogin(""));
		});

		router.post("/login", req -> {
			Map<String, String> formData = req.getFormData();
			String usuario = formData.get("usuario");
			String senha = formData.get("senha");

			if (usuario == null || senha == null || usuario.isBlank() || senha.isBlank()) {
				return HttpResponse.ok().html(LoginPages.paginaLogin("Preencha todos os campos."));
			}

			UserStore store = router.getUserStore();
			Optional<User> optUser = store.buscarPorUsername(usuario);

			if (optUser.isEmpty() || !optUser.get().verificarSenha(senha)) {
				return HttpResponse.ok().html(LoginPages.paginaLogin("Usuário ou senha incorretos."));
			}

			User user = optUser.get();
			if (!user.isAtivo()) {
				return HttpResponse.ok().html(LoginPages.paginaLogin("Usuário inativo. Contate o administrador."));
			}

			// Gerar token
			String token = router.getAuthManager().gerarToken(user);
			int maxAge = 24 * 3600; // 24 horas em segundos

			String redirectUrl = "/workspace/home";
			if (user.getCargo() == Role.ADMIN) {
				redirectUrl = "/workspace/dashboard";
			}

			return HttpResponse.redirect(redirectUrl)
					.cookie("session_token", token, maxAge);
		});

		router.get("/logout", req -> {
			return HttpResponse.redirect("/login").limparCookie("session_token");
		});

		// --- ROTAS DA ÁREA DE TRABALHO SEGURA ---

		router.get("/workspace/dashboard", req -> {
			User user = req.getUser();
			if (user == null || user.getCargo() != Role.ADMIN) {
				return HttpResponse.redirect("/login");
			}

			// Listar usuários em JSON para injetar na página
			UserStore store = router.getUserStore();
			List<User> todos = store.listarTodos();
			StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < todos.size(); i++) {
				if (i > 0) sb.append(",");
				User u = todos.get(i);
				sb.append(new JsonBuilder()
						.add("id", u.getId())
						.add("username", u.getUsername())
						.add("cargo", u.getCargo().name())
						.add("cargo_nome", u.getCargo().getNome())
						.add("ativo", u.isAtivo())
						.build());
			}
			sb.append("]");

			return HttpResponse.ok().html(DashboardPages.paginaAdmin(user, sb.toString()));
		});

		router.get("/workspace/home", req -> {
			User user = req.getUser();
			if (user == null) {
				return HttpResponse.redirect("/login");
			}
			return HttpResponse.ok().html(DashboardPages.paginaUserHome(user));
		});

		// --- API ENDPOINTS ---

		router.get("/api/info-server", req -> {
			String json = new JsonBuilder()
					.add("servidor", "Java HTTP Server")
					.add("versao", "1.0.0")
					.add("java", System.getProperty("java.version"))
					.build();
			return HttpResponse.ok().json(json);
		});

		router.get("/api/echo", req -> {
			String mensagem = req.getParametrosQuery("mensagem");
			if (mensagem.isBlank()) {
				throw new HttpException(HttpStatus.BAD_REQUEST,
						"Parametro 'mensagem' é obrigatório. Ex: /api/echo?mensagem=ola");
			}
			String json = new JsonBuilder()
					.add("original", mensagem)
					.add("revertida", new StringBuilder(mensagem).reverse().toString())
					.build();
			return HttpResponse.ok().json(json);
		});

		router.get("/api/me", req -> {
			User user = req.getUser();
			if (user == null) {
				return new HttpResponse().status(HttpStatus.UNAUTHORIZED).json(JsonBuilder.erro("Não autenticado."));
			}
			String json = new JsonBuilder()
					.add("id", user.getId())
					.add("username", user.getUsername())
					.add("cargo", user.getCargo().name())
					.add("cargo_nome", user.getCargo().getNome())
					.add("ativo", user.isAtivo())
					.build();
			return HttpResponse.ok().json(json);
		});

		// --- ROTAS ADMINISTRATIVAS DE API ---

		router.get("/api/admin/usuarios", req -> {
			User user = req.getUser();
			if (user == null || user.getCargo() != Role.ADMIN) {
				return new HttpResponse().status(HttpStatus.FORBIDDEN).json(JsonBuilder.erro("Não autorizado."));
			}

			UserStore store = router.getUserStore();
			List<User> todos = store.listarTodos();
			StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < todos.size(); i++) {
				if (i > 0) sb.append(",");
				User u = todos.get(i);
				sb.append(new JsonBuilder()
						.add("id", u.getId())
						.add("username", u.getUsername())
						.add("cargo", u.getCargo().name())
						.add("cargo_nome", u.getCargo().getNome())
						.add("ativo", u.isAtivo())
						.build());
			}
			sb.append("]");

			return HttpResponse.ok().json(sb.toString());
		});

		router.post("/api/admin/usuarios", req -> {
			User user = req.getUser();
			if (user == null || user.getCargo() != Role.ADMIN) {
				return new HttpResponse().status(HttpStatus.FORBIDDEN).json(JsonBuilder.erro("Não autorizado."));
			}

			String corpo = req.getCorpo();
			String username = extrairCampoJson(corpo, "username");
			String senha = extrairCampoJson(corpo, "senha");
			String cargoStr = extrairCampoJson(corpo, "cargo");

			if (username == null || senha == null || cargoStr == null ||
					username.isBlank() || senha.isBlank() || cargoStr.isBlank()) {
				return HttpResponse.requisicaoInvalida("Campos 'username', 'senha' e 'cargo' são obrigatórios.");
			}

			Role cargo = Role.fromString(cargoStr);
			UserStore store = router.getUserStore();
			User novoUser = store.criarUsuario(username, senha, cargo);

			if (novoUser == null) {
				return HttpResponse.requisicaoInvalida("Usuário '" + username + "' já existe.");
			}

			String json = new JsonBuilder()
					.add("status", "success")
					.add("message", "Usuário criado com sucesso")
					.add("id", novoUser.getId())
					.add("username", novoUser.getUsername())
					.add("cargo", novoUser.getCargo().name())
					.build();
			return HttpResponse.ok().json(json);
		});

		router.delete("/api/admin/usuarios", req -> {
			User user = req.getUser();
			if (user == null || user.getCargo() != Role.ADMIN) {
				return new HttpResponse().status(HttpStatus.FORBIDDEN).json(JsonBuilder.erro("Não autorizado."));
			}

			String idStr = req.getParametrosQuery("id");
			if (idStr.isBlank()) {
				return HttpResponse.requisicaoInvalida("ID do usuário é obrigatório. Ex: ?id=2");
			}
			try {
				int id = Integer.parseInt(idStr);
				UserStore store = router.getUserStore();
				if (id == 1) {
					return HttpResponse.requisicaoInvalida("Não é possível remover o administrador principal.");
				}
				boolean removido = store.removerUsuario(id);
				if (!removido) {
					return HttpResponse.naoEncontrado();
				}
				return HttpResponse.ok().json(JsonBuilder.sucesso("Usuário removido com sucesso."));
			} catch (NumberFormatException e) {
				return HttpResponse.requisicaoInvalida("ID inválido.");
			}
		});

		HttpServer servidor = new HttpServer(8080, router);
		servidor.iniciar();

		StaticFilesHandler workspaceDirectory = new StaticFilesHandler("public/Workspace");
		router.get("/workspace/*", workspaceDirectory);
	}

	private static String extrairCampoJson(String json, String campo) {
		String busca = "\"" + campo + "\":\"";
		int inicio = json.indexOf(busca);
		if (inicio == -1) {
			busca = "\"" + campo + "\":";
			inicio = json.indexOf(busca);
			if (inicio == -1) return null;
			inicio += busca.length();
			StringBuilder sb = new StringBuilder();
			for (int i = inicio; i < json.length(); i++) {
				char c = json.charAt(i);
				if (c == ',' || c == '}' || Character.isWhitespace(c)) break;
				sb.append(c);
			}
			return sb.toString().replace("\"", "").trim();
		}
		inicio += busca.length();
		int fim = json.indexOf("\"", inicio);
		if (fim == -1) return null;
		return json.substring(inicio, fim);
	}
}
