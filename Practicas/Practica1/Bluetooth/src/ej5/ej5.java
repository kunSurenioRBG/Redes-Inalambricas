package ej5;

import java.util.List;

import javax.bluetooth.*;

public class ej5 {

    static int SERVICE_NAME_ATTRID = 0x0100;
    static int SERVICE_ID_ATTRID = 0x1000;

    public static void main(String[] args) {

        final Object inquiryCompleteEvent = new Object();
        String device = "[TV] Samsung 6 Series (49)"; //nombre del dispositivo que quieres encontrar

        try {
            // El descubrimiento de dispositivos consta de 3 pasos
            // 1. Referencia al objeto local (proporciona acceso y control al dispositivo
            // local)
            LocalDevice ld = LocalDevice.getLocalDevice();

            // 2. Obtener referencia de donde esta mi dispositivo local
            DiscoveryAgent da = ld.getDiscoveryAgent();

            // 3. Utilizar el DiscoveryAgent para encontrar dispositivos cercanos
            MyDiscoveryListener listener = new MyDiscoveryListener(inquiryCompleteEvent, device);

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