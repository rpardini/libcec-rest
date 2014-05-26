package net.pardini.cecrest.controller;

import net.pardini.cecrest.model.CECDevice;
import net.pardini.cecrest.service.CECManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by pardini on 26/05/2014.
 */

@RestController
@RequestMapping(value = "/cec")
public class CECController {
// ------------------------------ FIELDS ------------------------------

    @Autowired
    private CECManager CECManager;

// -------------------------- OTHER METHODS --------------------------

    @RequestMapping(value = "/source/{source}", method = RequestMethod.GET)
    public CECDevice changeSourceTo(@PathVariable Long source) {
        return CECManager.changeSourceTo(source);
    }

    @RequestMapping(value = "/{deviceId}", method = RequestMethod.GET)
    public CECDevice getDevice(@PathVariable Long deviceId) {
        return new CECDevice();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<CECDevice> getDevices() {
        return CECManager.scanDevices();
    }

    @RequestMapping(value = "/{deviceId}/off", method = RequestMethod.GET)
    public CECDevice turnOffDevice(@PathVariable Long deviceId) {
        return CECManager.turnOffDevice(deviceId);
    }

    @RequestMapping(value = "/{deviceId}/on", method = RequestMethod.GET)
    public CECDevice turnOnDevice(@PathVariable Long deviceId) {
        return CECManager.turnOnDevice(deviceId);
    }
}
