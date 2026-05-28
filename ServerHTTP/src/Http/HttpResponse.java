package Http;

import Http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.*;

import Data.JsonBuilder;

public class HttpResponse{

	private Object status;
	
	/**
	 * CONCEITO: Linkedhasmap vs Hashmap
	 * Linkedhasmap mantem a ordem de inserção dos elementos.
	 * Usamos aqui ára que os cabeçalhos apareçam na resposta
	 * Http sempre na mesma ordem visível
	 */
	
	private final Map<String, String> cabecalhos = new LinkedHashMap<>();
	
	private byte[] corpo = new byte[0]; // Array de byte fazio por padrão
	
	public HttpResponse() {
		cabecalhos.put("Server", "JavaHTTP/1.0");
		cabecalhos.put("Connection", "close");
	}
	
	public HttpResponse status(Object badRequest) {
		this.status = badRequest;
		return this;
	}
	
	public HttpResponse corpo(String conteudo, String tipoConteudo) {
		this.corpo = conteudo.getBytes(StandardCharsets.UTF_8);
		cabecalhos.put("Content-type", tipoConteudo);
		cabecalhos.put("Content-Lenght", String.valueOf(this.corpo.length));
		return this;
	}
	
	public HttpResponse corpo(byte[] conteudo, String tipoConteudo) {
		this.corpo = conteudo;
		cabecalhos.put("Content-type", tipoConteudo);
		cabecalhos.put("Content-Lenght", String.valueOf(conteudo.length));
		return this;
	}
	
	public HttpResponse json(String json) {
		return corpo(json, "application/json; charset=uft-8");
	}
	
	public HttpResponse html(String html) {
		return corpo(html, "text/html; charset=uft-8");
	}
	
	public HttpResponse texto(String texto) {
		return corpo(texto, "text/plain; charset=uft-8");
	}
	
	//METODOS FABRICA ESTÁTICOS para os casos mais comuns
	
	public static HttpResponse ok() {
		return new HttpResponse().status(HttpResponse.ok());
	}
	
	public static HttpResponse naoEncontrado() {
		return new HttpResponse()
				.status(HttpStatus.BAD_REQUEST)
				.json(JsonBuilder.erro("Recurso não encontrado"));
	}
	
	public static HttpResponse requisicaoInvalida(String msg) {
        return new HttpResponse()
                .status(HttpStatus.BAD_REQUEST)
                .json(JsonBuilder.erro(msg));
    }
 
    public static HttpResponse erroInterno(String msg) {
        return new HttpResponse()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .json(JsonBuilder.erro(msg));
    }
    
    public void escreverEm(OutputStream saida) throws IOException {
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("HTTP/1.1").append(status).append("\r\n");
    	
    	for (Map.Entry<String, String> entrada : cabecalhos.entrySet()) {
    		sb.append(entrada.getKey())
    			.append(": ")
    			.append(entrada.getValue())
    			.append("\r\n");
    		
    	}
    	
    	sb.append("\r\n");
    	
    	saida.write(sb.toString().getBytes(StandardCharsets.UTF_8));
    	
    	if (corpo.length > 0) {
    		saida.write(corpo);
    	}
    	
    	saida.flush();
    	
    }

	public HttpMethod getMethod() {
		// TODO Auto-generated method stub
		return null;
	}
}
