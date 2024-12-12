import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
public interface ContadorPalabrasServidorRemoto extends Remote {
    void registrarCliente(ContadorPalabrasClienteRemoto cliente) throws RemoteException;
    void distribuirTareas(String filePath, int numTareas, String metodo) throws RemoteException;
    List<String> obtenerClientesConectados() throws RemoteException;
    int contarPalabrasSecuencial(String filePath) throws RemoteException;
    int contarPalabrasConcurrente(String filePath, int numTareas) throws RemoteException;
    int contarPalabras(String filePath, long start, long end) throws RemoteException;


    
}
