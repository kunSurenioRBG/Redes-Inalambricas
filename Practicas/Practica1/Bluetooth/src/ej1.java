import javax.bluetooth.*;

public class ej1 {

	public static void main(String[] args) {
		try {
			LocalDevice ld = LocalDevice.getLocalDevice();

			System.out.println("Direccion del dispositivo Bluetooth: " + ld.getBluetoothAddress());
			System.out.println("Nombre del dispositivo Bluetooth: " + ld.getFriendlyName());
			System.out.println("Encendido dispositivo Bluetooth: " + ld.isPowerOn());
			System.out.println("Propiedades del dispositivo Bluetooth: ");
			System.out.println("\t* Version: " + ld.getProperty("bluecove"));
			System.out.println("\t* Conecciones activas: " + ld.getProperty("bluecove.connections"));

			// RemoteDevice dev = RemoteDevice.getRemoteDevice(con);
		} catch (BluetoothStateException e) {
			System.err.println(e.toString());
		}
	}
}
