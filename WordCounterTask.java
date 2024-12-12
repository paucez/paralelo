import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JTextArea;
import java.rmi.RemoteException;

public class WordCounterTask implements Runnable {
    private ContadorPalabrasServidorRemoto contador;
    private String filePath;
    private long start;
    private long end;
    private JTextArea threadStatusArea;
    private int wordCount;

    public WordCounterTask(ContadorPalabrasServidorRemoto contador, String filePath, long start, long end, JTextArea threadStatusArea) {
        this.contador = contador;
        this.filePath = filePath;
        this.start = start;
        this.end = end;
        this.threadStatusArea = threadStatusArea;
        this.wordCount = 0;
    }

    @Override
    public void run() {
        try {
            if (contador != null) {
                wordCount = contador.contarPalabras(filePath, start, end);
            } else {
                wordCount = contarPalabrasLocal(filePath, start, end);
            }
            threadStatusArea.append(Thread.currentThread().getName() + " completado\n");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private int contarPalabrasLocal(String filePath, long start, long end) {
        int wordCount = 0;
        long currentPosition = start;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.skip(start);
            String line;

            while ((line = reader.readLine()) != null && currentPosition < end) {
                wordCount += line.split("\\s+").length;
                currentPosition += line.length() + 1; // Sumamos la longitud de la línea y el salto de línea
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordCount;
    }

    public int getWordCount() {
        return wordCount;
    }
    public WordCounterTask(String filePath, long start, long end) {

        this.filePath = filePath;

        this.start = start;

        this.end = end;

        this.wordCount = 0;

    }
}

