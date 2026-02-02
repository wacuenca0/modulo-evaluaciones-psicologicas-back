package ec.mil.dsndft.servicio_gestion.service;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;
import java.io.InputStream;

@Service
public class FtpService {
    public boolean subirArchivo(String servidor, int puerto, String usuario, String clave, String rutaRemota, String nombreArchivo, InputStream archivoStream) throws Exception {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(servidor, puerto);
            ftpClient.login(usuario, clave);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            boolean hecho = ftpClient.storeFile(rutaRemota + "/" + nombreArchivo, archivoStream);
            archivoStream.close();
            ftpClient.logout();
            ftpClient.disconnect();
            return hecho;
        } catch (Exception e) {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
            throw e;
        }
    }
}
