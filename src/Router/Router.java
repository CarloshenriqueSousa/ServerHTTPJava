package Router;

import java.util.*;
import Http.HttpMethod;
import Handler.Handler;
import Http.HttpResponse;
import Http.HttpStatus;
import Http.HttpRequest;
import Http.HttpException;

public class Router {

	/**
	 * CONCEITO: INNER CLASS ou uma classe interna privada
	 * É declarada dentro de outra classe e só irá fazer sentindo dentro do contexto do Router
	 * Agrupa um conjunto de dados de uma rota
	 */
	
	public static class Rota{
		final HttpMethod metodo;
		final String padrao;
		final Handler handler;
		
		Rota(HttpMethod metodo, String padrao, Handler handler) {
			this.metodo = metodo;
			this.handler = handler;
			this.padrao = padrao;
		}
		
		public Rota(HttpMethod put, String caminho, Handler handler2) {
			// TODO Auto-generated constructor stub
		}

		boolean combina(HttpMethod m, String caminho) {
			return this.metodo == m && padraoCorresponde(padrao, caminho);
		}
	}
	
	private final List<Rota> rotas = new ArrayList<>();
	
	/**
	 * CONCEITO: lambdas com argumentos
	 * O que é um Lambda?
	 * 		Mapear uma requisição( URL + Método) ou Injeção de comportamento
	 * O parametro 'handler' é do tipo Handler (interface funcional)
	 * Quem chama esses métodos pode passar diretamente pelo lambda diretamente
	 * 
	 * O Lambda implementa automaticamente o Handler.handler
	 */
	
	public Router get(String caminho, Handler handler) {
		rotas.add(new Rota(HttpMethod.GET, caminho, handler));
		return this;
	}
	
	public Router post(String caminho, Handler handler) {
		rotas.add(new Rota(HttpMethod.POST, caminho, handler));
		return this;
	}
	
	public Router delete(String caminho, Handler handler) {
		rotas.add(new Rota(HttpMethod.DELETE, caminho, handler));
		return this;
	}
	
	public Router put(String caminho, Handler handler) {
		rotas.add(new Rota(HttpMethod.PUT, caminho, handler));
		return this;
	}
	
	/**
	 * Definir um Handler customizadeo com erro 404
	 */
	
	public Router Erro404NaoEncontrado(String caminho, Handler handler) {
		this.handlerNaoEncontrado = handler;
		return this;
	}
	
	/**
	 * CONCEITO: Polimorfismo via interface
	 * dispatch() não sabe qual implemenação concreta do handler
	 * está sendo chamado
	 * ele simplesmente chama handle()
	 */
	
	public HttpResponse despachar(HttpResponse requisicao) {
		//Fist-match-wins (Percorrer as rotas em ordem de registro)
		for(Rota rota: rotas) {
			if(rota.combina(requisicao.getMethodo(),requisicao.getCaminho())) {
				try {
					return rota.handler.handle(requisicao);
				} catch(HttpException e) {
					return new HttpResponse()
							.status(e.getStatus())
							.json(JsonBuilder.erro(e.getMessage()));
				} catch(Exception e) {
					System.err.println("Erro no handler: " + e.getMessage());
					return HttpResponse.errpInterno("Erro interno ao processar requisição");
				}
			}
		}
		
		return handlerNaoEncontrado.handle(requisicao);
	}
	
	/**
	 * CONCEITO: Correspondência de padrões
	 * suportando dois tipos de padrão:
	 * 	Correspondencia exata
	 * 	Qualquer caminho com esse prefixo
	 */
	
	private static boolean padraoCorresponde(String padrao, String caminho) {
		if(padrao.equals(caminho)) {
			return true;
		}
		if(padrao.endsWith("/")) {
			String prefixo = padrao.substring(0, padrao.length() -1);
			return caminho.startsWith(prefixo);
		}
		return false;
	}
	
}