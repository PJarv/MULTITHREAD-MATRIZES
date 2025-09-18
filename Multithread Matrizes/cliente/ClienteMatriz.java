import java.io.*;
import java.net.*;
import java.util.Random;

public class ClienteMatriz {
    public static void main(String[] args) throws Exception {

        int TAMANHO_MATRIZ = 5;

        System.out.println("\nConectando ao servidor...");
        Socket socket = new Socket("localhost", 9000);
        System.out.println("Conectado.");

        int[][] A = gerarMatrizAleatoria(TAMANHO_MATRIZ, TAMANHO_MATRIZ);
        int[][] B = gerarMatrizAleatoria(TAMANHO_MATRIZ, TAMANHO_MATRIZ);

        System.out.println("Matriz A:");
        imprimirMatriz(A);
        System.out.println("Matriz B:");
        imprimirMatriz(B);

        ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
        saida.writeObject(A);
        saida.writeObject(B);
        System.out.println("Matrizes enviadas ao servidor. Aguardando resultado...");

        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
        int[][] resultado = (int[][]) entrada.readObject();

        System.out.println("Resultado recebido:");
        imprimirMatriz(resultado);

        socket.close();
        System.out.println("Conexão encerrada.");
    }

    public static int[][] gerarMatrizAleatoria(int linhas, int colunas) {
        Random rand = new Random();
        int[][] matriz = new int[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                matriz[i][j] = rand.nextInt(50) + 1;
            }
        }
        return matriz;
    }

    public static void imprimirMatriz(int[][] matriz) {
        for (int[] linha : matriz) {
            for (int valor : linha) {
                System.out.print(valor + "\t");
            }
            System.out.println();
        }
    }
}
