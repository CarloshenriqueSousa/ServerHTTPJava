package Http;

import java.util.*;
import java.io.*;
import Http.HttpMethod;

/**
 * CONCEITO: Imutabilidade + Fábrica estática Esta classe representa uma
 * requisição HTTP já parseada ( ou seja, transformado em um bloco de texto como
 * objeto direto para poder ser interpretado pelo navegado ) Uma vez criada, não
 * pode ser modificada, usada todas como final usamos sempre um construtor
 * privado + método fábrica estático para garantir que objetos saiam sempre em
 * um estádo válido
 */
public class HttpRequest {

	// Incapsulados e imutáveis
	private final HttpMethod metodo;
	private final String caminho;
	private final String versaoHttp;
	private final Map<String, String> cabecalhos;
	private final Map<String, String> parametrosQuery;
	private final String corpo;

	// Construtor privado: só irá chamar por parse()
	private HttpRequest(HttpMethod metodo, String caminho, String versaoHttp, Map<String, String> cabecalhos,
			Map<String, String> parametrosQuery, String corpo) {
		this.metodo = metodo;
		this.caminho = caminho;
		this.versaoHttp = versaoHttp;
		this.cabecalhos = cabecalhos;
		this.parametrosQuery = parametrosQuery;
		this.corpo = corpo;
	}

	/**
	 * CONCEITO: i/o com stremas
	 * InputStream é o fluxo bruto de butes do socket
	 * InputStreamReader converte bytes em caracters ( com charset)
	 * BufferedReader adiciona buffer e o método readLine()
	 * 
	 * camadas:
	 * socket -> inputStream -> InputStreamReader -> BufferedReader
	 */
	public static HttpRequest parse(InputStream entrada) throws IOException {
		BufferedReader leitor = new BufferedReader(new InputStreamReader (entrada));
		
		/**
		 * PASSO 1: Linha de requisicao
		 * FORMATO: GET/caminho?query HTTP/1.1
		 */
		String linhaDeRequisicao = leitor.readLine();
		if (linhaDeRequisicao == null || linhaDeRequisicao.isBlank()) {
			throw new IOException("Requisição vazia recebida");
		}
		
		String[] partes = linhaDeRequisicao.split(" ", 3);
		if (partes.length < 3) {
			throw new IOException("Linha de requisição malformada: " + linhaDeRequisicao);
		}
		
		HttpMethod metodo = HttpMethod.fromString(partes[0]);
		String caminhoCompleto = partes[1];
		String versaoHttp = partes[2];
		
		/**
		 * PASSO 2: Separar caminho dos parâmetros de Query
		 * busca?termo=java&pagina=1 -> caminho=/busca, params=...
		 */
		 String caminho;
		 Map<String, String> parametrosQuery = new HashMap<>();
		 
		 int ponto = caminhoCompleto.indexOf('?');
		 if (ponto != -1) {
			 caminho = caminhoCompleto.substring(0, ponto);
			 parsearQueryString(caminhoCompleto.substring(ponto + 1), parametrosQuery);
		 } else {
			 caminho = caminhoCompleto;
		 }
		 
		 /**
		  * PASSO 3: Leitura dos cabeçalhos
		  * Cada linha tem formato nome=valor
		  * A linha em braco "" indica o fim dos cabeça~hos
		  */
		 Map<String, String> cabecalhos = new HashMap<>();
		 String linha;
		 while ((linha = leitor.readLine()) != null && !linha.isEmpty()) {
			 int doisPontos = linha.indexOf(':');
			 if (doisPontos != -1) {
				 String nome = linha.substring(0, doisPontos).trim().toLowerCase();
				 String valor = linha.substring(doisPontos + 1).trim();
				 cabecalhos.put(nome, valor);
			 }
		 }
		 
		 /**
		  * PASSO 4: Ler corpo se Content-Lenght estiver presente
		  * apenas OST, PUT e etc enviam corpo normalmente
		  */
		 
		 String corpo = "";
		 String tamanhoStr = cabecalhos.get("Content-lenght");
		 if
	}
	
	
	
	private static void parsearQueryString() {
		
	}
}
