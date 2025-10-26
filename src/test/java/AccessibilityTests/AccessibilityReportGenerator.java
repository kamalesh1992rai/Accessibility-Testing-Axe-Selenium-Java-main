package AccessibilityTests;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class AccessibilityReportGenerator {

    // Public method to be called from AllyTest
    public void generateReports(String jsonFilePath) throws IOException {
        JSONObject responseJSON = new JSONObject(
                new JSONTokener(new FileReader(jsonFilePath)));

        JSONArray violations = responseJSON.getJSONArray("violations");

        if (violations.length() == 0) {
            System.out.println("No accessibility issues found ✅");
        } else {
            System.out.println("Generating Accessibility Reports...");

            generateCSVReport(violations, "AccessibilityReport.csv");
            generateHTMLReport(violations, "AccessibilityReport.html");

            System.out.println("Reports generated successfully 🎯");
        }
    }

    private void generateCSVReport(JSONArray violations, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("Rule ID,Impact,Description,Help URL,Elements\n");
            for (int i = 0; i < violations.length(); i++) {
                JSONObject v = violations.getJSONObject(i);
                JSONArray nodes = v.getJSONArray("nodes");
                String elements = nodes.join(" | "); // join targets

                writer.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        v.getString("id"),
                        v.optString("impact", "N/A"),
                        v.getString("description").replace("\"", "'"),
                        v.getString("helpUrl"),
                        elements.replace("\"", "'")));
            }
        }
    }

    private void generateHTMLReport(JSONArray violations, String fileName) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Accessibility Report</title>");
        html.append("<style>body{font-family:Arial;} h1{color:#2E86C1;} .violation{border:1px solid #ccc;margin:10px;padding:10px;}</style>");
        html.append("</head><body>");
        html.append("<h1>Accessibility Violations Report</h1>");
        html.append("<p>Total Violations: ").append(violations.length()).append("</p>");

        for (int i = 0; i < violations.length(); i++) {
            JSONObject v = violations.getJSONObject(i);
            html.append("<div class='violation'>");
            html.append("<h2>").append(v.getString("help")).append("</h2>");
            html.append("<p><b>Rule ID:</b> ").append(v.getString("id")).append("</p>");
            html.append("<p><b>Impact:</b> ").append(v.optString("impact", "N/A")).append("</p>");
            html.append("<p><b>Description:</b> ").append(v.getString("description")).append("</p>");
            html.append("<p><b>Help:</b> <a href='").append(v.getString("helpUrl"))
                .append("' target='_blank'>Deque Guide</a></p>");
            html.append("</div>");
        }

        html.append("</body></html>");

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(html.toString());
        }
    }
}
