package Main;

import Data.JsonBuilder;
import Http.HttpException;
import Http.HttpResponse;
import Http.HttpServer;
import Http.HttpStatus;
import Router.Router;
import Data.StaticFilesHandler;

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
                        <li><span class="tag">GET</span>  <a href="/api/info">/api/info</a></li>
                        <li><span class="tag">GET</span>  <a href="/api/echo?mensagem=ola">/api/echo?mensagem=ola</a></li>
                        <li><span class="tag">GET</span>  <a href="/api/linguagens">/api/linguagens</a></li>
                        <li><span class="tag">GET</span>  <a href="/api/calc?a=10&b=3&op=soma">/api/calc?a=10&b=3&op=soma</a></li>
                        <li><span class="tag">POST</span> <code>/api/dados</code></li>
                      </ul>
                      <h2>Testando com curl</h2>
                      <pre>
                    curl http://localhost:8080/api/info
                    curl "http://localhost:8080/api/echo?mensagem=Java+puro"
                    curl "http://localhost:8080/api/calc?a=10&b=3&op=div"
                    curl -X POST http://localhost:8080/api/dados -H "Content-Type: application/json" -d "{\"linguagem\":\"Java\"}"
                      </pre>
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
		
		StaticFilesHandler workspaceDirectory = new StaticFilesHandler("public/Workspace");
		router.get("/workspace/*", workspaceDirectory);
	}
}
