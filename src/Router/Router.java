package Router;

import java.util.*;
import Http.HttpMethod;
import Handler.Handler;
import Http.HttpResponse;
import Http.HttpStatus;
import Http.HttpRequest;
import Http.HttpException;
import Data.JsonBuilder;

public class Router {

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

	// Token de acesso para rotas privadas. Pode ser sobrescrito por variável de ambiente.
	private static final String TOKEN_MESTRE_PLATAFORMA = System.getenv().getOrDefault(
			"SERVER_MASTER_TOKEN",
			"vaultra_secure_token_2026");

	private final List<Rota> rotas = new ArrayList<>();
	private Handler handlerNaoEncontrado = req -> HttpResponse.naoEncontrado();

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

	/**
	 * DISPATCHER COM CORREÇÃO DE SEGURANÇA (GATEWAY CENTRAL)
	 */
	public HttpResponse despachar(HttpRequest requisicao) {
		
		// 1. FILTRO DE AUTENTICAÇÃO: Intercepta e protege rotas privadas do workspace
		if (!verificarAutenticacaoSegura(requisicao)) {
			System.err.println("[ACESSO BLOQUEADO] Tentativa de invasão não autenticada na rota: " + requisicao.getCaminho());

			return new HttpResponse()
					.status(HttpStatus.UNAUTHORIZED)
					.json(JsonBuilder.erro("Acesso não autorizado. Envie 'Authorization: Bearer <token>' válido."));
		}

		// 2. ROTEAMENTO PADRÃO (Se passou pela segurança, processa o Handler correspondente)
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

	/**
	 * Validador interno do Gateway de Cibersegurança
	 */
	private boolean verificarAutenticacaoSegura(HttpRequest requisicao) {
		String caminho = requisicao.getCaminho();
		
		// Rotas públicas essenciais para não quebrar navegação inicial.
		if (caminho.equals("/")
				|| caminho.equals("/login")
				|| caminho.equals("/favicon.ico")
				|| caminho.equals("/api/health")
				|| caminho.equals("/api/info")
				|| caminho.startsWith("/public/")) {
			return true;
		}

		String tokenRecebido = requisicao.getCabecalhos("Authorization");
		if (tokenRecebido == null || tokenRecebido.isBlank()) {
			return false;
		}

		String esperado = "Bearer " + TOKEN_MESTRE_PLATAFORMA;
		return esperado.equals(tokenRecebido.trim());
	}

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