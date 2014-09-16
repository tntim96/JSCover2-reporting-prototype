package jscover2.report.html;

import jscover2.report.*;
import org.apache.commons.lang3.StringEscapeUtils;

import static java.lang.String.format;

public class FileReportGenerator {
    private String code;
    private FileData data;
    private final SourceCodeRetriever sourceCodeRetriever;
    private String fileName;
    private final String pathToRoot;

    String getPathToRoot(String file) {
        int count = file.split("/").length - 1;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++)
            sb.append("../");
        return sb.toString();
    }

    public FileReportGenerator(String fileName, String code, FileData data) {
        this.fileName = fileName;
        this.pathToRoot = getPathToRoot(fileName);
        sourceCodeRetriever = new SourceCodeRetriever(fileName, code);
        this.code = code;
        this.data = data;
    }

    public String generateHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html>\n");
        sb.append("<html lang=\"en\">\n");
        buildHeader(sb);
        buildBody(sb);
        sb.append("</html>");
        return sb.toString();
    }

    private void buildHeader(StringBuilder sb) {
        sb.append("<head>\n");
        sb.append("    <title>JSCover2 Coverage Report</title>\n");
        sb.append(format("    <link rel=\"stylesheet\" href=\"%sjscover2.css\" type=\"text/css\"/>\n", pathToRoot));
        sb.append("</head>\n");
    }

    private void buildBody(StringBuilder sb) {
        sb.append("<body>\n");
        sb.append("<div class=\"content\">\n");
        CoverageSummaryData summaryData = new CoverageSummaryData(fileName, data);
        SummaryReportGenerator.buildSummaryMetricTable(sb, summaryData);
        sb.append(format("<a href=\"%sindex.html\">Back to summary</a>\n", pathToRoot));
        sb.append("<table class=\"coverage\">\n" +
                "    <tr>\n" +
                "        <td class=\"line-number\"><pre>");
        addLines(sb);
        sb.append("</pre></td>\n" +
                "        <td class=\"line-coverage\">");
        addHits(sb);
        sb.append("</td>\n" +
                "        <td class=\"code\"><pre>");
        addSource(sb);
        sb.append("</pre></td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "</div>\n" +
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
                sb.append(format("<span class=\"line\">&nbsp;</span>\n", currentLine));
            }
            LineCompleteData lineCompleteData = data.getLineData().get(line);
            int lineHits = lineCompleteData.getLineHits();
            String hitClass = lineCompleteData.isLineHit() && !lineCompleteData.isBooleanMissed() ? "hit" : "miss";
            String extraClass = lineCompleteData.isBooleanMissed() ? " booleanMissed" : "";
            String booleanExpressionExplanation = getBooleanExpressionHtml(lineCompleteData);
            sb.append(format("<span id=\"line%d\" class=\"line %s%s\">%d%s</span>\n", currentLine, hitClass, extraClass, lineHits, booleanExpressionExplanation));
        }
    }

    private String getBooleanExpressionHtml(LineCompleteData lineCompleteData) {
        if (!lineCompleteData.isBooleanMissed())
            return "";
        StringBuilder sb = new StringBuilder("<span><table class=\"code-snippet\">");
        for (BooleanExpressionData booleanExpressionData : lineCompleteData.getBooleanExpressions()) {
            if (!booleanExpressionData.hit()) {
                String code = sourceCodeRetriever.getSource(booleanExpressionData.getPosition());
                sb.append("<tr><td class=\"snippet\">");
                sb.append(StringEscapeUtils.escapeHtml4(code));
                sb.append("</td>");
                sb.append("<td class=\"description\">");
                if (booleanExpressionData.getTrueHits() == 0 && booleanExpressionData.getFalseHits() == 0)
                    sb.append("Never evaluated");
                else if (booleanExpressionData.getTrueHits() == 0)
                    sb.append("Never evaluated to true");
                else if (booleanExpressionData.getFalseHits() == 0)
                    sb.append("Never evaluated to false");
                sb.append("</td>");
                sb.append("</tr>");
            }
        }
        sb.append("</table></span>");
        return sb.toString();
    }

    private void addSource(StringBuilder sb) {
        sb.append(StringEscapeUtils.escapeHtml4(code));
    }
}
