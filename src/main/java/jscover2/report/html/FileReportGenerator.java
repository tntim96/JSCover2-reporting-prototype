package jscover2.report.html;

import jscover2.report.FileData;
import jscover2.report.LineCompleteData;
import jscover2.report.SourceCodeRetriever;
import org.apache.commons.lang3.StringEscapeUtils;

import static java.lang.String.format;

public class FileReportGenerator {
    private String code;
    private FileData data;
    private final SourceCodeRetriever sourceCodeRetriever;

    public FileReportGenerator(String fileName, String code, FileData data) {
        sourceCodeRetriever = new SourceCodeRetriever(fileName, code);
        this.code = code;
        this.data = data;
    }

    public String generateHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        buildHeader(sb);
        buildBody(sb);
        sb.append("</html>");
        return sb.toString();
    }

    private void buildHeader(StringBuilder sb) {
        sb.append("<head>\n" +
                "    <title>JSCover2 Coverage Report</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-size: 10pt;\n" +
                "            font-family: monospace;\n" +
                "        }\n" +
                "        .line {\n" +
                "            display:block;\n" +
                "            text-align: right;\n" +
                "        }\n" +
                "        .hit {\n" +
                "            background-color: greenyellow;\n" +
                "        }\n" +
                "        .miss {\n" +
                "            background-color: orangered;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n");
    }

    private void buildBody(StringBuilder sb) {
        sb.append("<body>\n" +
                "<table>\n" +
                "    <tr>\n" +
                "        <td><pre>");
        addLines(sb);
        sb.append("</pre></td>\n" +
                "        <td valign=\"top\">");
        addHits(sb);
        sb.append("</td>\n" +
                "        <td>\n" +
                "<pre>");
        addSource(sb);
        sb.append("</pre>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "</body>\n");
    }

    private void addLines(StringBuilder sb) {
        for (int i = 1; i <= this.sourceCodeRetriever.getNumberOfLines(); i++) {
            sb.append(i);
            sb.append('\n');
        }
    }

    private void addHits(StringBuilder sb) {
        int currentLine = 0;
        for (Integer line : data.getLineData().keySet()) {
            while (++currentLine < line) {
                sb.append("<span class=\"line\">&nbsp;</span>\n");
            }
            LineCompleteData lineCompleteData = data.getLineData().get(line);
            int lineHits = lineCompleteData.getLineHits();
            String hitClass = lineCompleteData.hit() && !lineCompleteData.isBooleanMissed() ? "hit" : "miss";
            String extraClass = lineCompleteData.isBooleanMissed() ? " booleanMissed" : "";
            sb.append(format("<span id=\"line%d\" class=\"line %s%s\">%d</span>\n", currentLine, hitClass, extraClass, lineHits));
        }
    }

    private void addSource(StringBuilder sb) {
        sb.append(StringEscapeUtils.escapeHtml4(code));
    }
}
