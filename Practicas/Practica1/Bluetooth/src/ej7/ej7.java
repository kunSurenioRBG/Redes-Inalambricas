package ej7;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Scanner;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class ej7 {

    static int SERVICE_NAME_ATTRID = 0x0100;
    static int SERVICE_ID_ATTRID = 0x1000;

    public static void main(String[] args) {

        final Object inquiryCompleteEvent = new Object();
        String device = "DESKTOP-4UK9A55"; // nombre del dispositivo que quieres encontrar
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

                                InputStream is = conexion.openInputStream();
                                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                                OutputStream os = conexion.openOutputStream();
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                                Scanner sc = new Scanner(System.in);
                                String response = "";
                                String mensaje = "";

                                while (!response.equals("FIN.")) {

                                    System.out.print(ld.getFriendlyName() + ": ");
                                    response = sc.nextLine();
                                    bw.write(response);
                                    bw.newLine();
                                    bw.flush();
                                    System.out.println("Esperano respuesta del servidor...");
                                    mensaje = br.readLine();
                                    System.out.println(
                                            dev.getBluetoothAddress() + " - " + dev.getFriendlyName(true) + ": "
                                                    + mensaje);
                                }
                                System.out.println(
                                        "Dispositivo " + dev.getBluetoothAddress() + " - " + dev.getFriendlyName(true)
                                                + " desconectado correctamente");
                                br.close();
                                bw.close();
                                sc.close();
                                mensaje = "";
                                conexion.close();
                            }
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        } catch (BluetoothStateException e) {
            System.err.println(e.toString());
        }
    }
}