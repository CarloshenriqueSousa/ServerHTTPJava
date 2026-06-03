package Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import Handler.Handler;
import Http.HttpRequest;
import Http.HttpResponse;

/**
 * Serve arquivos estáticos de um diretório base (ex.: public/).
 */
public class StaticFilesHandler implements Handler {

	private final Path diretorioBase;

	public StaticFilesHandler(String diretorio) {
		this.diretorioBase = Paths.get(diretorio).toAbsolutePath().normalize();
	}

	public HttpResponse servir(HttpRequest requisicao) {
		String caminho = requisicao.getCaminho();
		if (caminho.equals("/")) {
			caminho = "/index.html";
		}

		Path arquivo = diretorioBase.resolve(caminho.substring(1)).normalize();

		// Evita path traversal (ex.: /../../etc/passwd)
		if (!arquivo.startsWith(diretorioBase) || !Files.isRegularFile(arquivo)) {
			return HttpResponse.naoEncontrado();
		}

		try {
			byte[] conteudo = Files.readAllBytes(arquivo);
			String tipo = MimeTypes.porCaminho(arquivo.toString());
			return HttpResponse.ok().corpo(conteudo, tipo);
		} catch (IOException e) {
			return HttpResponse.erroInterno("Erro ao ler arquivo estático.");
		}
	}

	@Override
	public HttpResponse handle(HttpRequest request) {
		return servir(request);
	}
}
