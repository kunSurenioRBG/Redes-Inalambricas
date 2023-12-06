package ej2;

import javax.bluetooth.*;

public class ej2 {

	public static void main(String[] args) {

		Object inquiryCompleteEvent = new Object();

		try {
			// El descubrimiento de dispositivos consta de 3 pasos
			// 1. Referencia al objeto local (proporciona acceso y control al dispositivo
			// local)
			LocalDevice ld = LocalDevice.getLocalDevice();

			// 2. Obtener referencia de donde esta mi dispositivo local
			DiscoveryAgent da = ld.getDiscoveryAgent();

			// 3. Utilizar el DiscoveryAgent para encontrar dispositivos cercanos
			DiscoveryListener listener = new MyDiscoveryListener(inquiryCompleteEvent);

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