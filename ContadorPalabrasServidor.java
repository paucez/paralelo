/*import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ContadorPalabrasServidor extends UnicastRemoteObject implements ContadorPalabrasServidorRemoto {
    private final List<ContadorPalabrasClienteRemoto> clientesConectados = new ArrayList<>();
    private JTextArea clientesArea;

    // Constructor
    protected ContadorPalabrasServidor() throws RemoteException {
        super();
        initGUI(); // Inicializa la interfaz gráfica
    }

    // Método para inicializar la interfaz gráfica
    private void initGUI() {
        JFrame frame = new JFrame("Servidor RMI - Contador de Palabras");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        clientesArea = new JTextArea();
        clientesArea.setEditable(false);
        frame.add(new JScrollPane(clientesArea), BorderLayout.CENTER);

        JLabel label = new JLabel("Clientes conectados:");
        frame.add(label, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    // Método para registrar un cliente
    @Override
    public synchronized void registrarCliente(ContadorPalabrasClienteRemoto cliente) throws RemoteException {
        clientesConectados.add(cliente); // Agrega el cliente a la lista
        actualizarListaClientes(); // Actualiza la interfaz gráfica
        System.out.println("Cliente registrado con IP: " + cliente.obtenerIp());
    }

    // Método para obtener la lista de clientes conectados
    @Override
    public synchronized List<String> obtenerClientesConectados() throws RemoteException {
        List<String> ips = new ArrayList<>();
        for (ContadorPalabrasClienteRemoto cliente : clientesConectados) {
            ips.add(cliente.obtenerIp());
        }
        return ips;
    }

    // Método para actualizar la lista de clientes en la interfaz gráfica
    private void actualizarListaClientes() {
        try {
            List<String> clientes = obtenerClientesConectados();
            clientesArea.setText(String.join("\n", clientes)); // Muestra la lista en el JTextArea
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // Métodos restantes (vacíos o implementables)
   @Override
public synchronized void distribuirTareas(String filePath, int numTareas, String metodo) throws RemoteException {
    long fileSize = new File(filePath).length();
    long chunkSize = fileSize / numTareas;
    int numClientes = clientes.size();
    int totalWordCount = 0;

    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < numTareas; i++) {
        long start = i * chunkSize;
        long end = (i == numTareas - 1) ? fileSize : (i + 1) * chunkSize;
        ContadorPalabrasClienteRemoto cliente = clientes.get(i % numClientes);
        
        Thread thread = new Thread(() -> {
            try {
                int wordCount = cliente.contarPalabras(filePath, start, end);
                synchronized (this) {
                    totalWordCount += wordCount;
                }
                System.out.println("Cliente " + cliente.obtenerIp() + " procesó " + wordCount + " palabras.");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        threads.add(thread);
        thread.start();
    }

    for (Thread thread : threads) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    System.out.println("Total de palabras procesadas: " + totalWordCount);
}


    @Override
    public int contarPalabrasSecuencial(String filePath) throws RemoteException {
        return 0; // Implementar conteo secuencial
    }

    @Override
    public int contarPalabrasConcurrente(String filePath, int numTareas) throws RemoteException {
        return 0; // Implementar conteo concurrente
    }

    @Override
    public int contarPalabras(String filePath, long start, long end) throws RemoteException {
        return 0; // Implementar lógica de conteo
    }

    // Método principal
    public static void main(String[] args) {
        
        try {
            ContadorPalabrasServidor servidor = new ContadorPalabrasServidor();
            Registry registry = LocateRegistry.createRegistry(1099); // Crea el registro RMI en el puerto 1099
            registry.rebind("ContadorPalabrasServidor", servidor);
            System.out.println("Servidor listo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ContadorPalabrasServidor extends UnicastRemoteObject implements ContadorPalabrasServidorRemoto {
    private List<ContadorPalabrasClienteRemoto> clientes;

    public ContadorPalabrasServidor() throws RemoteException {
        this.clientes = new ArrayList<>();
    }

    @Override
    public void registrarCliente(ContadorPalabrasClienteRemoto cliente) throws RemoteException {
        clientes.add(cliente);
    }

    @Override
    public void distribuirTareas(String filePath, int numTareas, String metodo) throws RemoteException {
        // Implementar lógica para distribuir tareas
        int numClientes = clientes.size(); // Aquí obtienes el número de clientes registrados
        System.out.println("Número de clientes registrados: " + numClientes);
        // Lógica para distribuir tareas entre los clientes
    }

    @Override
    public List<String> obtenerClientesConectados() throws RemoteException {
        List<String> ips = new ArrayList<>();
        for (ContadorPalabrasClienteRemoto cliente : clientes) {
            ips.add(cliente.obtenerIp());
        }
        return ips;
    }

    @Override
    public int contarPalabrasSecuencial(String filePath) throws RemoteException {
        // Implementar lógica para conteo secuencial
        return 0;
    }

    @Override
    public int contarPalabrasConcurrente(String filePath, int numTareas) throws RemoteException {
        // Implementar lógica para conteo concurrente
        return 0;
    }

    @Override
    public int contarPalabras(String filePath, long start, long end) throws RemoteException {
        // Implementar lógica para conteo de palabras en un segmento
        return 0;
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "192.168.1.13");
            ContadorPalabrasServidor servidor = new ContadorPalabrasServidor();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("ContadorPalabrasServidor", servidor);
            System.out.println("Servidor RMI en ejecución en el puerto 1099");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
