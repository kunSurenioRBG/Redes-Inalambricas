package ej2;

import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.*;

public class MyDiscoveryListener implements DiscoveryListener {

    private List<RemoteDevice> devices;
    private Object inquiryCompletedEvent;

    public MyDiscoveryListener(Object inquiryCompleteEvent) {
        this.inquiryCompletedEvent = inquiryCompleteEvent;
        devices = new ArrayList<>();
    }

    public List<RemoteDevice> getDevices() {
        return devices;
    }
    @Override
    public void deviceDiscovered(RemoteDevice arg0, DeviceClass arg1) {
        try {
            System.out.println("Dispositivo encontrado: " + arg0.getBluetoothAddress() + " " + arg0.getFriendlyName(true));
            devices.add(arg0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void inquiryCompleted(int arg0) {
        System.out.println("Busqueda terminada. ");
        synchronized (inquiryCompletedEvent) {
            inquiryCompletedEvent.notifyAll();
        }
    }

    @Override
    public void serviceSearchCompleted(int arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {
        // TODO Auto-generated method stub

    }

}