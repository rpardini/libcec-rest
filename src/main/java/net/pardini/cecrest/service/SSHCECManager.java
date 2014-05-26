package net.pardini.cecrest.service;

import net.pardini.cecrest.model.CECDevice;
import net.pardini.cecrest.util.CECClientParser;
import net.pardini.cecrest.util.StreamGobbler;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Signal;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pardini on 26/05/2014.
 */
@Service
public class SSHCECManager implements CECManager {
// ------------------------------ FIELDS ------------------------------

    @Value("${cec.ssh.host}")
    private String cecSshHost;

    @Value("${cec.ssh.user}")
    private String cecSshUser;

    @Value("${cec.ssh.pass}")
    private String cecSshPass;

    @Value("${cec.osdName}")
    private String cecOsdName;


    private Logger logger = LoggerFactory.getLogger(getClass());
    private PrintWriter printWriter;
    private List<LineHandler> lineHandlerList = new ArrayList<>();
    private Session.Command command;
    private StreamGobbler streamGobbler;
    private InputStream inputStream;
    private OutputStream outputStream;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface CECManager ---------------------

    @Override
    public List<CECDevice> scanDevices() {
        sendCommand("scan");
        return CECClientParser.parseScanResult(waitForOutputLine("==================="));
    }

    @Override
    public CECDevice turnOnDevice(final Long deviceId) {
        sendCommand(String.format("on %d", deviceId));
        return getCurrentDeviceStatus(deviceId);
    }

    @Override
    public CECDevice turnOffDevice(final Long deviceId) {
        sendCommand(String.format("standby %d", deviceId));
        return getCurrentDeviceStatus(deviceId);
    }

    @Override
    public CECDevice changeSourceTo(final Long source) {
        sendCommand(String.format("tx 1F 82 %d0 00", source));
        return getCurrentDeviceStatus(0L);
    }

// -------------------------- OTHER METHODS --------------------------

    @PreDestroy
    public void cleanup() throws Exception {
        streamGobbler.interrupt();
        inputStream.close();
        outputStream.close();

        command.signal(Signal.HUP);
        command.join();
        command.close();
    }

    private CECDevice getCurrentDeviceStatus(final Long deviceId) {
        return new CECDevice();
    }

    @PostConstruct
    public void initialize() throws Exception {
        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new HostKeyVerifier() {
            @Override
            public boolean verify(final String s, final int i, final PublicKey publicKey) {
                return true;
            }
        });
        ssh.connect(cecSshHost);
        ssh.authPassword(cecSshUser, cecSshPass);
        final Session session = ssh.startSession();

        command = session.exec(String.format("cec-client --osd-name \"%s\"", cecOsdName));

        outputStream = command.getOutputStream();
        inputStream = command.getInputStream();

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        printWriter = new PrintWriter(outputStreamWriter);
        streamGobbler = new StreamGobbler(inputStream, new StreamGobbler.OnLineListener() {
            @Override
            public void onLine(final String line) {
                logger.info("CEC: " + line);
                for (LineHandler lineHandler : lineHandlerList) {
                    lineHandler.onLine(line);
                }
            }
        });
        streamGobbler.start();
    }

    private void sendCommand(final String command) {
        printWriter.println(command);
        printWriter.flush();
    }

    private ArrayList<String> waitForOutputLine(final String lineToWaitFor) {
        final boolean[] arrived = {false};
        final ArrayList<String> linesAfterArrived = new ArrayList<>();

        LineHandler listener = new LineHandler() {
            @Override
            public void onLine(final String line) {
                if (arrived[0]) {
                    linesAfterArrived.add(line);
                }
                if (line.equals(lineToWaitFor)) {
                    arrived[0] = true;
                }
            }
        };
        lineHandlerList.add(listener);

        try {
            while (!arrived[0]) {
                Thread.sleep(50);
            }
            return new ArrayList<>(linesAfterArrived);
        } catch (Exception ignored) {
        } finally {
            lineHandlerList.remove(listener);
        }
        return null;
    }
}
