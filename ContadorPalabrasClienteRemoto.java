import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ContadorPalabrasClienteRemoto extends Remote {
    int contarPalabras(String filePath, long start, long end) throws RemoteException;
    String obtenerIp() throws RemoteException;
}
