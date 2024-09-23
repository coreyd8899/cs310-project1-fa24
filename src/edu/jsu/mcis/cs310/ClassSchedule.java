package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;

public class ClassSchedule {

    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";

    public String convertCsvToJsonString(List<String[]> csv) {
        JsonObject jsonOutput = new JsonObject();
        JsonObject courseData = new JsonObject();

        for (int i = 1; i < csv.size(); i++) {
            String[] row = csv.get(i);
            JsonObject jsonObject = new JsonObject();

            jsonObject.put("subjectid", row[1] != null ? row[1] : "");
            jsonObject.put("num", row[2] != null ? row[2] : "");
            jsonObject.put("description", row[3] != null ? row[3] : "");

            String[] sections = row[4].split(",");
            JsonArray sectionArray = new JsonArray();
            for (String section : sections) {
                if (!section.trim().isEmpty()) {
                    sectionArray.add(section.trim());
                }
            }
            jsonObject.put("section", sectionArray);

            jsonObject.put("credits", row[6] != null ? row[6] : "");

            courseData.put(row[0], jsonObject);
        }

        jsonOutput.put("course", courseData);

        return Jsoner.serialize(jsonOutput);
    }

    public String convertJsonToCsvString(JsonObject json) {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");

        String[] header = { CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER, SECTION_COL_HEADER, TYPE_COL_HEADER, CREDITS_COL_HEADER, START_COL_HEADER, END_COL_HEADER, DAYS_COL_HEADER, WHERE_COL_HEADER, SCHEDULE_COL_HEADER, INSTRUCTOR_COL_HEADER };
        csvWriter.writeNext(header);

        JsonObject courses = (JsonObject) json.get("course");
        if (courses == null) {
            throw new NullPointerException("Key 'course' not found in JSON");
        }

        for (Object key : courses.keySet()) {
            JsonObject course = (JsonObject) courses.get(key);
            String[] row = new String[13];

            row[0] = key.toString();
            row[1] = course.get("subjectid") != null ? course.get("subjectid").toString() : "";
            row[2] = course.get("num") != null ? course.get("num").toString() : "";
            row[3] = course.get("description") != null ? course.get("description").toString() : "";

            JsonArray sectionArray = (JsonArray) course.get("section");
            if (sectionArray != null) {
                StringBuilder sections = new StringBuilder();
                for (Object section : sectionArray) {
                    if (sections.length() > 0) sections.append(",");
                    sections.append(section.toString());
                }
                row[4] = sections.toString();
            } else {
                row[4] = "";
            }

            row[5] = "";
            row[6] = course.get("credits") != null ? course.get("credits").toString() : "";
            row[7] = "";
            row[8] = "";
            row[9] = "";
            row[10] = "";
            row[11] = "";
            row[12] = "";

            csvWriter.writeNext(row);
        }

        return writer.toString();
    }

    public JsonObject getJson() {
        return getJson(getInputFileData(JSON_FILENAME));
    }

    public JsonObject getJson(String input) {
        JsonObject json = null;
        try {
            json = (JsonObject) Jsoner.deserialize(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public List<String[]> getCsv() {
        return getCsv(getInputFileData(CSV_FILENAME));
    }

    public List<String[]> getCsv(String input) {
        List<String[]> csv = null;
        try {
            CSVReader reader = new CSVReaderBuilder(new StringReader(input))
                .withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return csv;
    }

    public String getCsvString(List<String[]> csv) {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        csvWriter.writeAll(csv);
        return writer.toString();
    }

    private String getInputFileData(String filename) {
        StringBuilder buffer = new StringBuilder();
        String line;

        ClassLoader loader = ClassLoader.getSystemClassLoader();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }
}
