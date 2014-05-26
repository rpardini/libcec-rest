package net.pardini.cecrest.util;

import net.pardini.cecrest.model.CECDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pardini on 26/05/2014.
 */
public class CECClientParser {

    private static Logger logger = LoggerFactory.getLogger(CECClientParser.class);
    private static final Pattern MATCH_DEVICE_ID_AND_NAME = Pattern.compile("device #(.): (.*)");
    private static final Pattern MATCH_VENDOR = Pattern.compile("vendor: (.*)");
    private static final Pattern MATCH_OSD = Pattern.compile("osd string: (.*)");

    public static ArrayList<CECDevice> parseScanResult(final ArrayList<String> strings) {
        String[] split = org.springframework.util.StringUtils.collectionToDelimitedString(strings, "\n").split("\n\n");

        ArrayList<CECDevice> cecDevices = new ArrayList<>();
        for (String naco : split) {
            String clean = StringUtils.trimWhitespace(naco);
            logger.debug(String.format("Got: '%s'.", clean));

            try {
                CECDevice newDevice = new CECDevice();
                newDevice.setId(Long.valueOf(getRegexValue(clean, MATCH_DEVICE_ID_AND_NAME, 1)));
                newDevice.setName(getRegexValue(clean, MATCH_DEVICE_ID_AND_NAME, 2));
                newDevice.setVendor(getRegexValue(clean, MATCH_VENDOR, 1));
                newDevice.setOSD(getRegexValue(clean, MATCH_OSD, 1));
                cecDevices.add(newDevice);
            } catch (Exception ignored) {
            }
        }
        return cecDevices;
    }

    private static String getRegexValue(final String search, final Pattern pat, final int theGroup) {
        Matcher matcher = pat.matcher(search);
        if (matcher.find()) {
            String group = matcher.group(theGroup);
            logger.debug(String.format("Got group '%s' for in search '%s'.", group, search));
            return StringUtils.trimWhitespace(group);
        }
        return null;
    }

}
