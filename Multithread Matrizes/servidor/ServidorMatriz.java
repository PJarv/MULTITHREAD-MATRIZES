import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorMatriz {

    public static void main(String[] args) throws IOException {
        int porta = 9000;
        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("\nIniciado na porta " + porta);

            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("\nCliente conectado: " + cliente.getInetAddress());
                new Thread(new Atendente(cliente)).start();
            }
        }
    }

    static class Atendente implements Runnable {
        private Socket socket;

        public Atendente(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
            ) {
                int[][] matrizA = (int[][]) entrada.readObject();
                int[][] matrizB = (int[][]) entrada.readObject();

                System.out.println("Matrizes recebidas. Iniciando multiplicação.");
                System.out.println("Matriz A:");
                imprimirMatriz(matrizA);
                System.out.println("Matriz B:");
                imprimirMatriz(matrizB);

                int linhas = matrizA.length;
                int colunas = matrizB[0].length;
                int[][] resultado = new int[linhas][colunas];

                int numThreads = 5;
                ExecutorService pool = Executors.newFixedThreadPool(numThreads);

                int bloco = (int) Math.ceil((double) linhas / numThreads);
                for (int t = 0; t < numThreads; t++) {
                    final int inicio = t * bloco;
                    final int fim = Math.min(inicio + bloco, linhas);

                    pool.execute(() -> {
                        System.out.println("[THREAD] Processando linhas de " + inicio + " até " + (fim - 1)
                                + " (Thread: " + Thread.currentThread().getName() + ")");
                        for (int i = inicio; i < fim; i++) {
                            for (int j = 0; j < colunas; j++) {
                                int soma = 0;
                                for (int k = 0; k < matrizB.length; k++) {
                                    soma += matrizA[i][k] * matrizB[k][j];
                                }
                                resultado[i][j] = soma;
                            }
                        }
                    });
                }

                pool.shutdown();
                pool.awaitTermination(1, TimeUnit.MINUTES);

                System.out.println("Resultado calculado:");
                imprimirMatriz(resultado);

                saida.writeObject(resultado);
                System.out.println("Resultado enviado ao cliente.");

            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void imprimirMatriz(int[][] matriz) {
            for (int[] linha : matriz) {
                for (int valor : linha) {
                    System.out.print(valor + "\t");
                }
                System.out.println();
            }
        }
    }
}
