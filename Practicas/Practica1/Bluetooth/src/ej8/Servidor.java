package ej8;

import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import chatUI.ChatWindow;

public class Servidor {

    public static void main(String args[]) throws IOException {
    	
    	/* Activar la escucha de peticiones (en el caso del servidor) */
    	
    	String url = "btspp://localhost:" + new UUID(0x1101).toString() + ";name=chat";
		StreamConnectionNotifier service = (StreamConnectionNotifier) Connector.open(url);
		
		LocalDevice ld = LocalDevice.getLocalDevice();
		System.out.println("Datos del servidor: " + ld.getBluetoothAddress() + " - " + ld.getFriendlyName());
		System.out.println("\nServidor Iniciado. Esperando clientes...");
		
		StreamConnection con = (StreamConnection) service.acceptAndOpen();
		RemoteDevice dev = RemoteDevice.getRemoteDevice(con);
		System.out.println("Direccion del dispositivo remoto: " + dev.getBluetoothAddress());
		System.out.println("Nombre del dispositivo remoto: " + dev.getFriendlyName(true));
    	
    	/* A continuaci�n, obtenemos el inputStream y outputStream y los usamos desde la ventana */
		
		InputStream is = con.openInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		OutputStream os	= con.openOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
    	
    	/* Invocamos la ventana (que se ejecuta como un thread en segundo plano) y 
    	 * definimos la acci�n de enviar lo que insertemos por teclado */
        final chatUI.ChatWindow _window;
        _window = new ChatWindow();
        _window.setVisible(true);
        _window.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	
            	/* MODIFICAR EL C�DIGO PARA EL ENV�O AQU� */
            	try {
               		String response = _window.getIn(); // metodo que lee de la entrada
                    _window.setOut("Servidor: "+response); //Metodo que escribe en la salida de la ventana
               		bw.write(response);
               		bw.newLine();
               		bw.flush();
               	}catch (Exception i) {
               		i.printStackTrace();
               	}

            }
        });
        
        
        
        /*
         *
         * L�neas obligatorias: hay que registrar un listener para los eventos de ventana y
         * sobre el de window closing, realizar el cierre de conexiones.
         *
         *
         */

        _window.addWindowListener(new java.awt.event.WindowListener() {
            public void windowClosing(WindowEvent e) {
                System.out.println("Window closing event .... close connections");
            }

            public void windowClosed(WindowEvent e) {
                System.out.println("Window closed event ");
            }

            public void windowDeactivated(WindowEvent e) {
                System.out.println("Window deactivated event ");
            }

            public void windowOpened(WindowEvent e) {
                System.out.println("Window event ");
            }

            public void windowIconified(WindowEvent e) {
                System.out.println("Window event ");
            }

            public void windowDeiconified(WindowEvent e) {
                System.out.println("Window event ");
            }

            public void windowActivated(WindowEvent e) {
                System.out.println("Window event ");
            }

        });
        
        
			/* En este punto, una vez iniciada la ventana, nos ponemos en bucle a recibir la informaci�n del otro extremo */
        	/* MODIFICAR EL C�DIGO PARA LA RECEPCI�N AQU� */
		String mensaje = "";
		while (!mensaje.equals("FIN.")) {
			mensaje = br.readLine();
			_window.setOut("El cliente dice: " + mensaje);
		}
		_window.setOut("Client successfully disconnected from the server");
		br.close();
		bw.close();
		con.close();
    
    }
}

