package Router;

import java.util.*;
import Http.HttpMethod;
import Handler.Handler;
import Http.HttpResponse;
import Http.HttpStatus;
import Http.HttpRequest;
import Http.HttpException;

import Data.JsonBuilder;

import java.util.ArrayList;
import java.util.List;

// ============================================================
// CONCEITO: PADRÃO ROTEADOR
// Mapeia combinações (método HTTP + caminho) para funções Handler.
// ============================================================

public class Router {

	// ============================================================
	// CONCEITO: CLASSE INTERNA ESTÁTICA (STATIC INNER CLASS)
	// Declarada dentro de Router. Só existe no contexto dele.
	// 'static' = não precisa de instância de Router para existir.
	// ============================================================
	private static class Rota {
		final HttpMethod metodo;
		final String padrao;
		final Handler handler;

		Rota(HttpMethod metodo, String padrao, Handler handler) {
			this.metodo = metodo;
			this.padrao = padrao;
			this.handler = handler;
		}

		boolean combina(HttpMethod m, String caminho) {
			return this.metodo == m && padraoCorresponde(padrao, caminho);
		}
	}

	// ============================================================
	// CONCEITO: ARRAYLIST
	// List é uma interface. ArrayList é sua implementação com
	// array dinâmico que cresce automaticamente.
	// ============================================================
	private final List<Rota> rotas = new ArrayList<>();

	private Handler handlerNaoEncontrado = req -> HttpResponse.naoEncontrado();

	// ============================================================
	// CONCEITO: LAMBDAS COMO ARGUMENTOS
	// O parâmetro 'handler' é do tipo Handler (interface funcional).
	// Quem chama pode passar uma lambda diretamente:
	//
	// router.get("/ping", req -> HttpResponse.ok().texto("pong"));
	// ============================================================
	public Router get(String caminho, Handler handler) {
		rotas.add(new Rota(HttpMethod.GET, caminho, handler));
		return this;
	}

	public Router post(String caminho, Handler handler) {
		rotas.add(new Rota(HttpMethod.POST, caminho, handler));
		return this;
	}

	public Router put(String caminho, Handler handler) {
		rotas.add(new Rota(HttpMethod.PUT, caminho, handler));
		return this;
	}

	public Router delete(String caminho, Handler handler) {
		rotas.add(new Rota(HttpMethod.DELETE, caminho, handler));
		return this;
	}

	public Router naoEncontrado(Handler handler) {
		this.handlerNaoEncontrado = handler;
		return this;
	}

	public Router fallback(Handler handler) {
		this.handlerNaoEncontrado = handler;
		return this;
	}

	// ============================================================
	// CONCEITO: POLIMORFISMO VIA INTERFACE
	// dispatch() não sabe qual implementação concreta de Handler
	// está chamando. Ele chama handle() e a JVM resolve em runtime.
	// ============================================================
	public HttpResponse despachar(HttpRequest requisicao) {
		for (Rota rota : rotas) {
			if (rota.combina(requisicao.getMetodo(), requisicao.getCaminho())) {
				try {
					return rota.handler.handle(requisicao);
				} catch (HttpException e) {
					return new HttpResponse().status(e.getStatus()).json(JsonBuilder.erro(e.getMessage()));
				} catch (Exception e) {
					System.err.println("Erro no handler: " + e.getMessage());
					return HttpResponse.erroInterno("Erro interno ao processar requisição.");
				}
			}
		}
		return handlerNaoEncontrado.handle(requisicao);
	}

	// Suporta "/rota/exata" e "/prefixo/*"
	private static boolean padraoCorresponde(String padrao, String caminho) {
		if (padrao.equals(caminho))
			return true;
		if (padrao.endsWith("/*")) {
			String prefixo = padrao.substring(0, padrao.length() - 1);
			return caminho.startsWith(prefixo);
		}
		return false;
	}
}