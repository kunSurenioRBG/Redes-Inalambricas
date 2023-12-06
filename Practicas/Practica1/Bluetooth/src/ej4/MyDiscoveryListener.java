package ej4;

import java.util.ArrayList;
import java.util.List;

import javax.bluetooth.*;

public class MyDiscoveryListener implements DiscoveryListener {

    private List<RemoteDevice> devices;
    private Object inquiryCompletedEvent;
    private List<String> urlServices;

    public MyDiscoveryListener(Object inquiryCompleteEvent) {
        this.inquiryCompletedEvent = inquiryCompleteEvent;
        devices = new ArrayList<>();
        urlServices = new ArrayList<>();
    }

    public List<RemoteDevice> getDevices() {
        return devices;
    }

    public List<String> getUrlServices() {
        return urlServices;
    }

    @Override
    public void deviceDiscovered(RemoteDevice arg0, DeviceClass arg1) {
        try {
            System.out.println(
                    "Dispositivo encontrado: " + arg0.getBluetoothAddress() + " " + arg0.getFriendlyName(true));
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
        System.out.println("Busqueda de servicios terminada. ");
        synchronized (inquiryCompletedEvent) {
            inquiryCompletedEvent.notifyAll();
        }

    }

    @Override
    public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {
        for (ServiceRecord service : arg1) {
            
            String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

            if (url == null) {
                continue;
            }

            urlServices.add(url);

            if (service.getAttributeValue(0x0100) != null) {
                System.out.println("Servicio " + service.getAttributeValue(0x0100).getValue());
                System.out.println("URL: " + url);
            } else {
                System.out.println("Servicio: " + url);
            }
        }

    }

}