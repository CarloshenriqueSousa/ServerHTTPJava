package Main;

import java.util.Arrays;
import java.util.List;

import Data.JsonBuilder;
import Http.HttpResponse;
import Http.HttpStatus;
import Http.HttpException; // Adicionado o import que faltava aqui
import Router.Router;

public class Main {

	public static void main(String[] args) throws Exception { // Corrigido 'Main' para 'main' minúsculo
		Router router = new Router();

		// CORREÇÃO 1: Adicionado o 'return' com o HTML gerado
		router.get("/", req -> {
			String html = """
					<!DOCTYPE html>
					<html lang="pt-BR">
					<head>
					  <meta charset="UTF-8">
					  <title>Servidor Java HTTP</title>
					  ... (seu CSS) ...
					</head>
					<body>
					  <h1>☕ Servidor HTTP em Java puro</h1>
					  ...
					</body>
					</html>
					""";
			
			return HttpResponse.ok(html); // <-- PRECISA RETORNAR O HTTPRESPONSE AQUI!
		});

		// Esta rota já estava certa porque tinha o 'return' no final!
		router.get("/api/info", req -> {
			String versaoJava = System.getProperty("java.version");
			String so = System.getProperty("os.name");
			int nucleos = Runtime.getRuntime().availableProcessors();
			long memoriaLivreMB = Runtime.getRuntime().freeMemory() / (1024 * 1024);

			String json = new JsonBuilder().add("servidor", "Java HTTP Server").add("versao", "1.0.0")
					.add("Java", versaoJava).add("sistema", so).add("nucleosCPU", nucleos)
					.add("memoriaLivreMB", memoriaLivreMB).build();

			return HttpResponse.ok(json).json(json); 
		});

		// CORREÇÃO 2: Adicionado o retorno caso a validação passe
		router.get("/api/echo", req -> { // Corrigido também de 'api/echo' para '/api/echo' (com barra)
			String mensagem = req.getParametrosQuery("mensagem");

			if (mensagem == null || mensagem.isBlank()) { // Boa prática validar se é null antes do isBlank
				throw new HttpException(HttpStatus.BAD_REQUEST,
						"Parametro 'mensagem' é obrigatório. EX: /api/echo?mensagem=ola");
			}
			
			String revertida = new StringBuilder(mensagem).reverse().toString();
			
			String json = new JsonBuilder()
					.add("original", mensagem)
					.add("revertida", revertida)
					.add("tamanho", mensagem.length())
					.add("maiúscula", mensagem.toUpperCase())
					.build();
			
			// Se não lançou a exceção acima, precisamos retornar uma resposta de sucesso!
			return HttpResponse.ok(mensagem).json(new JsonBuilder().add("echo", mensagem).build());
		});
		
		router.get("api/linguagens", req -> {
			List<String> linguagens = Arrays.asList(
					"Java", "Python", "Go", "Rust", "C", "TypeScript", "Kotlin"
			);
			
			String json = new JsonBuilder()
					.add("linguagens", linguagens)
					.add("tamanho", linguagens.size())
					.build();
			
			return HttpResponse.ok(json).json(json);
		});
	}
}