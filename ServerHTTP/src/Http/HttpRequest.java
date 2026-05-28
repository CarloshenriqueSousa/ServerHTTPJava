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
	        String tamanhoStr = cabecalhos.get("content-length");
	        if (tamanhoStr != null) {
	            try {
	                int tamanho = Integer.parseInt(tamanhoStr.trim());
	                if (tamanho > 0) {
	                    char[] buffer = new char[tamanho];
	                    int lidos = leitor.read(buffer, 0, tamanho);
	                    if (lidos > 0) {
	                        corpo = new String(buffer, 0, lidos);
	                    }
	                }
	            } catch (NumberFormatException e) {
	                // Content-Length inválido → ignora corpo
	            }
	        }
	        
	        return new HttpRequest(metodo, caminho, versaoHttp, cabecalhos, parametrosQuery, corpo);
	}
	
	/**
	 * CONCEITO: for-each
	 * Itera sobre arrays e coleções de forma mais legível que o for tradicional com índice
	 */
	
	private static void parsearQueryString(String query, Map<String, String> params) {
		String[] pares = query.split("&");
		for (String par: pares) {
			String[] kv = par.split("=", 2);
			if (kv.length == 2) {
				params.put(decodificarUrl(kv[0]), decodificarUrl(kv[1]));
			} else if (kv.length == 1 && !kv[0].isBlank()) {
				params.put(decodificarUrl(kv[0]), "");
			}
		}
	}
	
	private static String decodificarUrl(String s) {
		return s.replace("+", " ")
                .replace("%20", " ")
                .replace("%3A", ":")
                .replace("%2F", "/");
	}

	/**
	 * GETTER: Acesso controlado aos campos privados
	 * Retornando copias defensivas das coleções para que o código externo não possa modificar o estado interno
	 */
	
	
	public HttpMethod getMetodo() {
		return metodo;
	}

	public String getCaminho() {
		return caminho;
	}

	public String getVersaoHttp() {
		return versaoHttp;
	}

	public String getCabecalhos(String nome) {
		return cabecalhos.getOrDefault(nome.toLowerCase(), "");
	}

	public String getParametrosQuery(String nome) {
		return parametrosQuery.getOrDefault(nome, "");
	}
	
	public Map<String, String> getParametrosQuery() {
		return Collections.unmodifiableMap(parametrosQuery);
	}

	public String getCorpo() {
		return corpo;
	}
	
	@Override
	public String toString() {
		return metodo + " " + caminho + " [" + versaoHttp + "] ";
	}
	
}
