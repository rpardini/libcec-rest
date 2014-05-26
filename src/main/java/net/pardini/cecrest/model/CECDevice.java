package net.pardini.cecrest.model;

/**
 * Created by pardini on 26/05/2014.
 */
public class CECDevice {
// ------------------------------ FIELDS ------------------------------

    private Long id;
    private String name;
    private String vendor;
    private String OSD;

// --------------------- GETTER / SETTER METHODS ---------------------

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(final String vendor) {
        this.vendor = vendor;
    }

// -------------------------- OTHER METHODS --------------------------

    public String getOSD() {
        return OSD;
    }

    public void setOSD(final String OSD) {
        this.OSD = OSD;
    }
}
