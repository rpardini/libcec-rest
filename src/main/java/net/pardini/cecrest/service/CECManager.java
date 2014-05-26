package net.pardini.cecrest.service;

import net.pardini.cecrest.model.CECDevice;

import java.util.List;

/**
 * Created by pardini on 26/05/2014.
 */
public interface CECManager {
    List<CECDevice> scanDevices();

    CECDevice turnOnDevice(Long deviceId);

    CECDevice turnOffDevice(Long deviceId);

    CECDevice changeSourceTo(Long source);
}
