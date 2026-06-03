package Main;

import Data.JsonBuilder;
import Http.HttpException;
import Http.HttpResponse;
import Http.HttpServer;
import Http.HttpStatus;
import Router.Router;

public class Main {

	public static void main(String[] args) throws Exception {
		Router router = new Router();

		router.get("/", req -> HttpResponse.ok()
				.html("""
						<!DOCTYPE html>
						<html lang="pt-BR">
						<head>
						  <meta charset="UTF-8">
						  <title>Servidor Java HTTP</title>
						</head>
						<body>
						  <h1>Servidor HTTP em Java puro</h1>
						  <p>Rotas: <a href="/api/info">/api/info</a></p>
						</body>
						</html>
						"""));

		router.get("/api/info", req -> {
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

		HttpServer servidor = new HttpServer(8080, router);
		servidor.iniciar();
	}
}
