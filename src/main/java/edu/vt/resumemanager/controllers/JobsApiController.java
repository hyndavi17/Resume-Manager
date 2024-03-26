
package edu.vt.resumemanager.controllers;

import edu.vt.resumemanager.utils.Constants;
import edu.vt.resumemanager.utils.Methods;
import edu.vt.resumemanager.utils.pojo.JobsApi;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.Getter;
import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONException;
import org.primefaces.shaded.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
@SessionScoped
@Named("jobsApiController")

public class JobsApiController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String searchField;
    private String searchString;

    // selected = object reference of the selected CountryApi object
    private JobsApi selected;
    private List<JobsApi> listOfJobsFound = null;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */


    public String cancel() {
        // Unselect previously selected company object if any
        selected = null;
        return "/apiSearch/apiSearchResults?faces-redirect=true";
    }
    /*
    ================
    Instance Methods
    ================
     */

    /*
     *******************************************
     *   Unselect Selected CountryApi Object   *
     *******************************************
     */
    public void unselect() {
        selected = null;
    }

    /*
     ************************************
     *   Perform Countries API Search   *
     ************************************
     */
    public String performSearch() throws Exception {

        // Unselect previously selected CountryApi object if any before showing the search results
        selected = null;

        //--------------------------------------------------
        // Instantiate a new listOfCountriesFound array list
        //--------------------------------------------------
        listOfJobsFound = new ArrayList<>();

        // Initializations of Local Variables
        String searchApiUrl = null;
        String id = null;
        String title;
        String url;
        String location;
        String organizationName;
        String positionSchedule;
        String positionType;
        String startDate;
        String endDate;


        switch (searchField) {
            case "Keyword":
                // Search query can be entire common name or just part of it.
                searchApiUrl = "https://data.usajobs.gov/api/Search?Keyword=" + searchString;
                break;
            case "PositionTitle":
                // Search by country full name
                searchApiUrl = "https://data.usajobs.gov/api/Search?PositionTitle=" + searchString ;
                break;
            case "LocationName":
                // Search by cca2, ccn3, cca3 or cioc country code
                searchApiUrl = "https://data.usajobs.gov/api/Search?LocationName=" + searchString;
                break;
            case "Organization":
                // Search by capital city name
                searchApiUrl = "https://data.usajobs.gov/api/Search?Organization=" + searchString;
                break;
            case "PositionSchedule":
                // Search by currency name or currency code
                searchApiUrl = "https://data.usajobs.gov/api/Search?PositionSchedule=" + searchString;
                break;
            case "DatePosted":
                // Search by language name or language iso639_2 code
                searchApiUrl = "https://data.usajobs.gov/api/Search?DatePosted=" + searchString;
                break;
            default:
                System.out.println("Search Field Value is Out of Range!");
                break;
        }

        /*
        Redirecting to show a JSF page involves more than one subsequent requests and
        the messages would die from one request to another if not kept in the Flash scope.
        Since we will redirect to show the search Results page, we invoke preserveMessages().
         */
        Methods.preserveMessages();

        try {
            System.out.println("SearchAPI:"+searchApiUrl);
            URL urlLink = new URL(searchApiUrl);
            HttpURLConnection connection = (HttpURLConnection) urlLink.openConnection();
            connection.setRequestProperty("Host", "data.usajobs.gov");
            connection.setRequestProperty("User-Agent", "hyndavi@vt.edu");
            connection.setRequestProperty("Authorization-Key", Constants.JOBS_API_KEY);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestMethod("GET");
            connection.connect();


            // Obtain the JSON file from the searchApiUrl
            String searchResultsJsonData = Methods.getUrlContent(connection);
            System.out.println("searchResultsJsonData:"+searchResultsJsonData);
            JSONObject jsonObject = new JSONObject(searchResultsJsonData);

            /*
             If no search results are returned from the API for the user's search query, then
             throw a new IOException so that the Catch block at the end below is executed.
             */
            if (jsonObject.isEmpty()) throw new IOException("IOException Occurred");
            JSONObject searchResultObject = jsonObject.getJSONObject("SearchResult");

            JSONArray itemsArr = searchResultObject.getJSONArray("SearchResultItems");

            int index = 0;
            System.out.println("itemsArr.length():"+itemsArr.length());
            while (itemsArr.length() > index) {
                System.out.println("index:"+index);
                JSONObject jobsJsonObject = itemsArr.getJSONObject(index);
                JSONObject matchedObjectDescriptor = jobsJsonObject.getJSONObject("MatchedObjectDescriptor");


                    title = matchedObjectDescriptor.optString("PositionTitle");
                    url = matchedObjectDescriptor.optString("PositionURI");
                    location = matchedObjectDescriptor.optString("PositionLocationDisplay");
                    organizationName = matchedObjectDescriptor.optString("OrganizationName");

                    JSONArray positionScheduleArr = matchedObjectDescriptor.getJSONArray("PositionSchedule");
                    JSONObject positionScheduleObj = positionScheduleArr.getJSONObject(0);
                    positionSchedule = positionScheduleObj.optString("Name");

                    JSONArray positionOfferingArr = matchedObjectDescriptor.getJSONArray("PositionOfferingType");
                    JSONObject positionOfferingObj = positionOfferingArr.getJSONObject(0);
                    positionType = positionOfferingObj.optString("Name");

                startDate = matchedObjectDescriptor.optString("PositionStartDate");
                endDate = matchedObjectDescriptor.optString("PositionEndDate");


                JobsApi jobFound = new JobsApi(id,title , url, location, organizationName, positionSchedule, positionType, startDate, endDate);

                // Add the newly created CountryApi object to the list of countries found
                System.out.println("job:"+jobFound.getUrl());
                listOfJobsFound.add(jobFound);
                index++;
            }

        } catch (IOException ex) {
            Methods.showMessage("Information", "Unrecognized Search Query!",
                    "The REST Jobs API provides no data for the search query entered!");

            /*
             * *****************************************************************
             * Current JSF page, on top of which the search dialog is displayed,
             * should be shown when no result is obtained from the API.
             * *****************************************************************
             */

            // Obtain the current HTTP request object from the user's session instance
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

            String requestURI = request.getRequestURI();

            String appName = request.getContextPath();

            // Remove app name from requestURI to obtain the current page name to return to
            String currentPage = requestURI.replace(appName, "");
            // currentPage = /publicVideo/SearchResults.xhtml

            return currentPage + "?faces-redirect=true";
        }
        System.out.println("done");
        // Reset search queries
        searchString = "";
        searchField = null;
        System.out.println(listOfJobsFound.toString());
        return "/apiSearch/apiSearchResults?faces-redirect=true";
    }
}
