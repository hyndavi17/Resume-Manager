
package edu.vt.resumemanager.utils;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;


public final class Methods {

    public static void preserveMessages() {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    public static void showMessage(String messageType, String messageSummary, String messageDetail) {

        switch (messageType) {
            case "Information":
                FacesMessage infoMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, messageSummary, messageDetail);
                FacesContext.getCurrentInstance().addMessage(null, infoMessage);
                break;
            case "Warning":
                FacesMessage warningMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, messageSummary, messageDetail);
                FacesContext.getCurrentInstance().addMessage(null, warningMessage);
                break;
            case "Error":
                FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageSummary, messageDetail);
                FacesContext.getCurrentInstance().addMessage(null, errorMessage);
                break;
            case "Fatal Error":
                FacesMessage fatalErrorMessage = new FacesMessage(FacesMessage.SEVERITY_FATAL, messageSummary, messageDetail);
                FacesContext.getCurrentInstance().addMessage(null, fatalErrorMessage);
                break;
            default:
                System.out.print("Message Type is Out of Range!");
        }
    }
    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    public static String readUrlContent(String apiURL) throws Exception {

        BufferedReader reader = null;

        try {
            // Create a URL object from the webServiceURL given
            URL url = new URL(apiURL);

            reader = new BufferedReader(new InputStreamReader(url.openStream()));

            // Create a mutable sequence of characters and store its object reference into buffer
            StringBuilder buffer = new StringBuilder();

            // Create an array of characters of size 10240
            char[] chars = new char[10240];

            int numberOfCharactersRead;

            while ((numberOfCharactersRead = reader.read(chars)) != -1) {
                buffer.append(chars, 0, numberOfCharactersRead);
            }

            // Return the String representation of the created buffer
            return buffer.toString();

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    public static String getUrlContent(HttpURLConnection connection) throws Exception {
        /*
        reader is an object reference pointing to an object instantiated from the BufferedReader class.
        Initially, it is "null" pointing to nothing.
         */
        BufferedReader reader = null;

        try {
            /*
            The BufferedReader class reads text from a character-input stream, buffering characters
            so as to provide for the efficient reading of characters, arrays, and lines.
             */
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // Create a mutable sequence of characters and store its object reference into buffer
            StringBuilder buffer = new StringBuilder();

            // Create an array of characters of size 10240
            char[] chars = new char[10240];

            int numberOfCharactersRead;

            while ((numberOfCharactersRead = reader.read(chars)) != -1) {
                buffer.append(chars, 0, numberOfCharactersRead);
            }

            // Return the String representation of the created buffer
            return buffer.toString();

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
