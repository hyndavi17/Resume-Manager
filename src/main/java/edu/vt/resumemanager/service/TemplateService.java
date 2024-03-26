package edu.vt.resumemanager.service;


import edu.vt.resumemanager.dto.Profile;
import edu.vt.resumemanager.utils.Constants;
import freemarker.template.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.io.*;
@Named("templateService")

@ApplicationScoped
@Data
public class TemplateService implements Serializable{

    public String process(edu.vt.resumemanager.entity.Template template, Profile profile){
        String rv = "";
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_20);
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_20));
        Map<String, Object> root = new HashMap<>();
        root.put("profile",profile);
        try {
            freemarker.template.Template t = new freemarker.template.Template(template.getName(), new StringReader(template.getContent()), cfg);
            Writer out = new StringWriter();
            t.process(root, out);
            rv = out.toString();
        }catch (IOException | TemplateException e) {
            rv = "<h1>Error Processing template</h1>:<br /> " + e.getMessage();
        }
        return rv;
    }

    private String toPDF(String html, String htmlFile, String pdfFile){
        Runtime rt = Runtime.getRuntime();
        try {
            Files.writeString(Paths.get( Constants.FILES_ABSOLUTE_PATH + htmlFile),
                    html,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE);
            Process pr = rt.exec(Constants.PDF_CONVERTOR + " " + Constants.FILES_ABSOLUTE_PATH + htmlFile + " " + Constants.FILES_ABSOLUTE_PATH + pdfFile);
            pr.waitFor();
        }catch (Exception e){
            System.out.println("Error converting to pdf:"+e.getMessage());
            return null;
        }
        return pdfFile;
    }

    public String toPDF(edu.vt.resumemanager.entity.Template template, Profile p){
        String html = process(template,p);
        String htmlFile = p.getId() +".html";
        String pdfFile = p.getId() +".pdf";

        return  toPDF(html, htmlFile, pdfFile);
    }
    public String toPDF(edu.vt.resumemanager.entity.Template template, Profile p, int projectId){
        String html = process(template,p);
        String htmlFile = projectId + "_" + p.getId() +".html";
        String pdfFile = projectId + "_" + p.getId() +".pdf";

        return  toPDF(html, htmlFile, pdfFile);
    }
}
