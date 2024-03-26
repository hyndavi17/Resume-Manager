
package edu.vt.resumemanager.utils.pojo;

// This class provides a Plain Old Java Object (POJO) representing a Country returned from the API
public class JobsApi {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */

    /*
     A unique 'id' is required by the <p:dataTable> attribute rowKey="#{aCountry.id}"
     for listing and sorting the countries found as a result of the API search.
     */
    private String id = null;
    private String title;
    private String url;
    private String location;
    private String organizationName;
    private String positionSchedule;
    private String positionType;
    private String startDate;
    private String endDate;

    /*
    ==================
    Constructor Method
    ==================
     */
    public JobsApi(String id, String title, String url, String location, String organizationName, String positionSchedule, String positionType, String startDate, String endDate) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.location = location;
        this.organizationName = organizationName;
        this.positionSchedule = positionSchedule;
        this.positionType = positionType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getPositionSchedule() {
        return positionSchedule;
    }

    public void setPositionSchedule(String positionSchedule) {
        this.positionSchedule = positionSchedule;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
