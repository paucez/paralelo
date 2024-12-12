import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.UnicastRemoteObject;


public class ContadorPalabrasCliente extends UnicastRemoteObject implements ContadorPalabrasClienteRemoto {
    private String ip;

    public ContadorPalabrasCliente() throws RemoteException {
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RemoteException("No se pudo obtener la dirección IP del host", e);
        }
    }

    @Override
    public int contarPalabras(String filePath, long start, long end) throws RemoteException {
        int wordCount = 0;
        long currentPosition = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.skip(start);
            currentPosition = start;
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

    @Override
    public String obtenerIp() throws RemoteException {
        return this.ip;
    }

    public static void main(String[] args) {
        //try { Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099); ContadorPalabrasServidorRemoto servidor = (ContadorPalabrasServidorRemoto) registry.lookup("ContadorPalabrasServidor"); // Llamar a métodos del servidor... } catch (Exception e) { e.printStackTrace(); } }
        JFrame frame = new JFrame("Cliente RMI Contador de Palabras");
        JTextField ipField = new JTextField("192.168.1.7");
        JTextField portField = new JTextField("1099");
        JTextField filePathField = new JTextField();
        JButton browseButton = new JButton("Buscar");
        JButton sequentialButton = new JButton("Secuencial");
        JButton concurrentButton = new JButton("Concurrente");
        JButton parallelButton = new JButton("Paralelo");
        JTextArea resultArea = new JTextArea();
        JTextArea threadStatusArea = new JTextArea();
        JTextArea timeArea = new JTextArea();
        JLabel timeLabel = new JLabel("Tiempo de procesamiento: ");
        JLabel modeLabel = new JLabel("Modo de procesamiento: ");

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(new JLabel("Dirección IP:"));
        frame.add(ipField);
        frame.add(new JLabel("Puerto:"));
        frame.add(portField);
        frame.add(new JLabel("Archivo de texto:"));
        frame.add(filePathField);
        frame.add(browseButton);
        frame.add(sequentialButton);
        frame.add(concurrentButton);
        frame.add(parallelButton);
        frame.add(new JLabel("Resultados:"));
        frame.add(new JScrollPane(resultArea));
        frame.add(new JLabel("Estado de los hilos:"));
        frame.add(new JScrollPane(threadStatusArea));
        frame.add(new JLabel("Tiempos de procesamiento:"));
        frame.add(new JScrollPane(timeArea));
        frame.add(modeLabel);
        frame.add(timeLabel);

        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                filePathField.setText(file.getAbsolutePath());
            }
        });

        sequentialButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            if (!filePath.isEmpty()) {
                long startTime = System.currentTimeMillis();
                int wordCount = countWordsSequential(filePath);
                long endTime = System.currentTimeMillis();
                resultArea.setText("Número de palabras: " + wordCount);
                modeLabel.setText("Modo de procesamiento: Secuencial");
                String timeTaken = (endTime - startTime) + " ms";
                timeLabel.setText("Tiempo de procesamiento: " + timeTaken);
                timeArea.append("Secuencial: " + timeTaken + "\n");
                System.out.println("Tiempo de procesamiento secuencial: " + timeTaken);
            }
        });

        concurrentButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            if (!filePath.isEmpty()) {
                int numThreads = 4; // Número de hilos (puede hacerse configurable)
                long startTime = System.currentTimeMillis();
                int wordCount = countWordsConcurrent(filePath, numThreads, threadStatusArea);
                long endTime = System.currentTimeMillis();
                resultArea.setText("Número de palabras: " + wordCount);
                modeLabel.setText("Modo de procesamiento: Concurrente");
                String timeTaken = (endTime - startTime) + " ms";
                timeLabel.setText("Tiempo de procesamiento: " + timeTaken);
                timeArea.append("Concurrente: " + timeTaken + "\n");
                System.out.println("Tiempo de procesamiento concurrente: " + timeTaken);
            }
        });

        parallelButton.addActionListener(e -> {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            String filePath = filePathField.getText();
            if (!filePath.isEmpty()) {
                int numThreads = 4; // Número de hilos (puede hacerse configurable)
                long startTime = System.currentTimeMillis();
                int wordCount = countWordsParallel(ip, port, filePath, numThreads, threadStatusArea);
                long endTime = System.currentTimeMillis();
                resultArea.setText("Número de palabras: " + wordCount);
                modeLabel.setText("Modo de procesamiento: Paralelo");
                String timeTaken = (endTime - startTime) + " ms";
                timeLabel.setText("Tiempo de procesamiento: " + timeTaken);
                timeArea.append("Paralelo: " + timeTaken + "\n");
                System.out.println("Tiempo de procesamiento paralelo: " + timeTaken);
            }
        });

        String serverIp = ipField.getText();
        int port = Integer.parseInt(portField.getText());

        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
            ContadorPalabrasServidorRemoto servidor = (ContadorPalabrasServidorRemoto) registry.lookup("ContadorPalabrasServidor");
            ContadorPalabrasCliente cliente = new ContadorPalabrasCliente();
            servidor.registrarCliente(cliente);
            System.out.println("Cliente registrado con IP: " + cliente.obtenerIp());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para el conteo secuencial
    public static int countWordsSequential(String filePath) {
        int wordCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordCount += line.split("\\s+").length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordCount;
    }

    // Método para el conteo concurrente (local)
    public static int countWordsConcurrent(String filePath, int numThreads, JTextArea threadStatusArea) {
        int totalWordCount = 0;
        File file = new File(filePath);
        long fileSize = file.length();
        long chunkSize = fileSize / numThreads;
        Thread[] threads = new Thread[numThreads];
        WordCounterTask[] tasks = new WordCounterTask[numThreads];

        for (int i = 0; i < numThreads; i++) {
            long start = i * chunkSize;
            long end = (i == numThreads - 1) ? fileSize : (i + 1) * chunkSize;
            tasks[i] = new WordCounterTask(filePath, start, end);
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
                totalWordCount += tasks[i].getWordCount();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return totalWordCount;
    }

    // Método para el conteo paralelo (distribuido mediante RMI)
    public static int countWordsParallel(String ip, int port, String filePath, int numThreads, JTextArea threadStatusArea) {
        int totalWordCount = 0;
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1",1099 );
            ContadorPalabrasServidorRemoto contador = (ContadorPalabrasServidorRemoto) registry.lookup("ContadorPalabras");

            File file = new File(filePath);
            long fileSize = file.length();
            long chunkSize = fileSize / numThreads;
            Thread[] threads = new Thread[numThreads];
            WordCounterTask[] tasks = new WordCounterTask[numThreads];

            for (int i = 0; i < numThreads; i++) {
                long start = i * chunkSize;
                long end = (i == numThreads - 1) ? fileSize : (i + 1) * chunkSize;
                tasks[i] = new WordCounterTask(contador, filePath, start, end, threadStatusArea);   
                threads[i] = new Thread(tasks[i]);
                threads[i].start();
            }

            for (int i = 0; i < numThreads; i++) {
                threads[i].join();
                totalWordCount += tasks[i].getWordCount();
            }
        } catch (RemoteException | NotBoundException | InterruptedException e) {
            e.printStackTrace();
        }

        return totalWordCount;
    }
}
