package ej8;

import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import chatUI.ChatWindow;

public class ej8 {

    static int SERVICE_NAME_ATTRID = 0x0100;
    static int SERVICE_ID_ATTRID = 0x1000;

    public static void main(String[] args) {

        final Object inquiryCompleteEvent = new Object();
        String device = "Mi 10"; // nombre del dispositivo que quieres encontrar
        String service = "chat"; // servicio que quiero identificar

        try {
            // El descubrimiento de dispositivos consta de 3 pasos
            // 1. Referencia al objeto local (proporciona acceso y control al dispositivo
            // local)
            LocalDevice ld = LocalDevice.getLocalDevice();

            // 2. Obtener referencia de donde esta mi dispositivo local
            DiscoveryAgent da = ld.getDiscoveryAgent();

            // 3. Utilizar el DiscoveryAgent para encontrar dispositivos cercanos
            MyDiscoveryListener listener = new MyDiscoveryListener(inquiryCompleteEvent, device, service);

            // Contamos el tiempo que tarda en buscar dispositivos
            long initTime = System.nanoTime();

            // Se inicia la busqueda de dispositivos
            // GIAC: General Inquiry access Code - (General –buscamos dispositivos visibles
            // para todos)
            // LIAC: Limited Inquiry access Code) –(Limitado –visibles sólo para los que
            // conocemos)
            boolean start = da.startInquiry(DiscoveryAgent.GIAC, listener);

            // USAMOS SEMAFOROS - MONITORES para controlar el fin de la busqueda
            synchronized (inquiryCompleteEvent) {
                if (start) {
                    try {
                        System.out.println("Buscando dipositivos...");
                        inquiryCompleteEvent.wait();
                        long endTime = System.nanoTime();
                        System.out.println("Fin inquiry.");
                        System.out.println("Tiempo busqueda: " + (endTime - initTime) / 1e9 + "segundos.");
                        List<RemoteDevice> devices = listener.getDevices();
                        System.out.println("Dispositivos encontrados: " + devices.size());

                        if (!devices.isEmpty()) {
                            // A partir de una lista de dispositivos detectados se buscan los servicios de
                            // una determinada clase
                            UUID uuids[] = new UUID[1];
                            uuids[0] = new UUID(0x1002); // servicios ofrecidos publicamente

                            int attridset[] = new int[2];
                            attridset[0] = SERVICE_NAME_ATTRID;
                            attridset[1] = SERVICE_ID_ATTRID;

                            for (RemoteDevice rd : devices) {

                                System.out.println("Servicios activos del dispositivo: " + rd.getBluetoothAddress()
                                        + ":" + rd.getFriendlyName(true));
                                da.searchServices(attridset, uuids, rd, listener);
                                synchronized (inquiryCompleteEvent) {
                                    inquiryCompleteEvent.wait();
                                }
                            }

                            if (!listener.getUrlServices().isEmpty()) {
                                // URL del destino
                                String url = listener.getUrlServices().get(0);
                                System.out.println(url);
                                // Inicio de conexion
                                StreamConnection conexion = (StreamConnection) Connector.open(url);
                                RemoteDevice dev = RemoteDevice.getRemoteDevice(conexion);
                                System.out.println("Direccion Bluetooh del servidor: " + dev.getBluetoothAddress());
                                System.out.println("Nombre del servidor: " + dev.getFriendlyName(false));

                                // Obtenemos el imput de entrada y lo leemos en el buffer
                                InputStream is = conexion.openInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                                OutputStream os = conexion.openOutputStream();
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                                /*
                                 * Invocamos la ventana (que se ejecuta como un thread en segundo plano) y
                                 * definimos la acci�n de enviar lo que insertemos por teclado
                                 */
                                final chatUI.ChatWindow _window;
                                _window = new ChatWindow();
                                _window.setVisible(true);
                                _window.addActionListener(new java.awt.event.ActionListener() {
                                    public void actionPerformed(java.awt.event.ActionEvent evt) {

                                        /* MODIFICAR EL C�DIGO PARA EL ENV�O AQU� */
                                        try {
                                            String response = _window.getIn(); // metodo que lee de la entrada
                                            _window.setOut("Cliente: " + response); // Metodo que escribe en la salida
                                                                                    // de la ventana
                                            bw.write(response);
                                            bw.newLine();
                                            bw.flush();
                                        } catch (Exception i) {
                                            i.printStackTrace();
                                        }

                                    }
                                });

                                /*
                                 * L�neas obligatorias: hay que registrar un listener para los eventos de
                                 * ventana y
                                 * sobre el de window closing, realizar el cierre de conexiones
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

                                /*
                                 * En este punto, una vez iniciada la ventana, nos ponemos en bucle a
                                 * recibir la informaci�n del otro extremo
                                 */
                                /* MODIFICAR EL C�DIGO PARA LA RECEPCI�N AQU� */
                                String mensaje = "";
                                while (!mensaje.equals("FIN.")) {
                                    mensaje = br.readLine();
                                    _window.setOut("El Servidor dice: " + mensaje);
                                }
                                _window.setOut("Servidor successfully disconnected from the server");
                                br.close();
                                bw.close();
                                conexion.close();
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        } catch (

        BluetoothStateException e) {
            System.err.println(e.toString());
        }
    }
}