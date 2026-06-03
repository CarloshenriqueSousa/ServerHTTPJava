package Handler;

import java.net.Socket;
import java.io.*;

import Router.Router;
import Http.HttpResponse;
import Http.HttpRequest;

public class ClientHandler implements Runnable { // <-- Mudado de 'interface' para 'class'
	 
    private final Socket socket;
    private final Router router;
 
    // Agora o construtor é perfeitamente válido!
    public ClientHandler(Socket socket, Router router) {
        this.socket = socket;
        this.router = router;
    }
 
    @Override // Boa prática adicionar a anotação já que implementa Runnable
    public void run() {
        try (Socket s = socket) {
 
            String nomeDaThread = Thread.currentThread().getName();
            String ipCliente    = s.getInetAddress().getHostAddress();
            System.out.printf("[%s] Nova conexão de %s%n", nomeDaThread, ipCliente);
 
            // PASSO 1: Parseia a requisição do stream do socket
            HttpRequest requisicao;
            try {
                requisicao = HttpRequest.parse(s.getInputStream());
                System.out.printf("[%s] %s%n", nomeDaThread, requisicao);
            } catch (IOException e) {
                System.err.printf("[%s] Requisição inválida: %s%n", nomeDaThread, e.getMessage());
                return;
            }
 
            // PASSO 2: Despacha para o router
            HttpResponse resposta;
            try {
                resposta = router.despachar(requisicao);
            } catch (Exception e) {
                System.err.printf("[%s] Erro interno: %s%n", nomeDaThread, e.getMessage());
                resposta = HttpResponse.erroInterno("Erro inesperado no servidor.");
            }
 
            // PASSO 3: Envia a resposta pelo socket
            try {
                resposta.escreverEm(s.getOutputStream());
            } catch (IOException e) {
                System.err.printf("[%s] Erro ao escrever resposta: %s%n", nomeDaThread, e.getMessage());
            }
 
        } catch (IOException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
