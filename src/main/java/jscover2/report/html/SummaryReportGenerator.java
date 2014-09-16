package jscover2.report.html;

import jscover2.report.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;

public class SummaryReportGenerator {
    private JSCover2Data jsCover2Data;
    private File sourceRoot;
    private File reportDir;
    private JSCover2CoverageSummary summary;

    public SummaryReportGenerator(JSCover2Data jsCover2Data, File sourceRoot, File reportDir) {
        this.jsCover2Data = jsCover2Data;
        this.sourceRoot = sourceRoot;
        this.reportDir = reportDir;
        summary = new JSCover2CoverageSummary(jsCover2Data);
    }

    public void generateReport() {
        try {
            File cssFile = new File(reportDir, "jscover2.css");
            FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/jscover2.css"), cssFile);
            FileUtils.write(new File(reportDir, "index.html"), generateIndexHtml());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        generateFiles();
    }

    void generateFiles() {
        for (String fileName : jsCover2Data.getDataMap().keySet()) {
            try {
                String code = FileUtils.readFileToString(new File(sourceRoot, fileName));
                FileData fileData = jsCover2Data.getDataMap().get(fileName);
                FileReportGenerator frg = new FileReportGenerator(fileName, code, fileData);
                File dest = new File(reportDir, fileName + ".html");
                FileUtils.writeStringToFile(dest, frg.generateHtml());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    String generateIndexHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html>\n");
        sb.append("<html lang=\"en\">\n");
        buildHeader(sb);
        buildBody(sb);
        sb.append("</html>");
        return sb.toString();
    }

    private void buildHeader(StringBuilder sb) {
        sb.append("<head>\n" +
                "    <title>JSCover2 Coverage Report</title>\n" +
                "    <link rel=\"stylesheet\" href=\"jscover2.css\" type=\"text/css\"/>\n" +
                "</head>\n");
    }

    private void buildBody(StringBuilder sb) {
        sb.append("<body>\n");
        sb.append("<div class=\"content\">\n");
        buildSummaryMetricTable(sb, summary.getTotals());
        buildDetailMetricTable(sb, summary);
        sb.append("</div>\n");
        sb.append("</body>\n");
    }

    static void buildSummaryMetricTable(StringBuilder sb, CoverageSummaryData data) {
        sb.append("Coverage summary <b>");
        sb.append(data.getName());
        sb.append("</b>\n");
        sb.append("<table class=\"metric-summary\">\n");
        sb.append("<tr><th>Statement</th><th>Branch</th><th>Boolean Expression</th><th>Function</th><th>Line</th></tr>\n");
        sb.append("<tr>\n");
        buildMetric(sb, data.getStatementCoverage());
        buildMetric(sb, data.getBranchPathCoverage());
        buildMetric(sb, data.getBooleanExpressionCoverage());
        buildMetric(sb, data.getFunctionCoverage());
        buildMetric(sb, data.getLineCoverage());
        sb.append("</tr>\n");
        sb.append("</table>\n");
    }

    private void buildDetailMetricTable(StringBuilder sb, JSCover2CoverageSummary summary) {
        sb.append("<table class=\"metric-summary\">\n");
        sb.append("<tr><th colspan=\"2\">Name</th><th>Statement</th><th>Branch</th><th>Boolean Expression</th><th>Function</th><th>Line</th></tr>\n");
        for (CoverageSummaryData summaryData : summary.getFiles()) {
            sb.append("<tr>\n");
            sb.append("<td class=\"name\">");
            sb.append(format("<a href=\"%s.html\">%s</a>", summaryData.getName(), summaryData.getName()));
            sb.append("</td>\n");
            sb.append("<td class=\"graph\">");
            sb.append(format("<div class=\"coveredBackground\"><div class=\"covered\" style=\"width:%3.0fpx;\"></div></div>", summaryData.getLineCoverage().getRatio()*100));
            sb.append("</td>\n");
            buildMetric(sb, summaryData.getStatementCoverage());
            buildMetric(sb, summaryData.getBranchPathCoverage());
            buildMetric(sb, summaryData.getBooleanExpressionCoverage());
            buildMetric(sb, summaryData.getFunctionCoverage());
            buildMetric(sb, summaryData.getLineCoverage());
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");
    }

    private static void buildMetric(StringBuilder sb, CoverageSummaryItem data) {
        sb.append("<td>");
        sb.append("<span class=\"ratio\">");
        sb.append(data.getCovered());
        sb.append("/");
        sb.append(data.getTotal());
        sb.append("</span>\n");
        sb.append("<span class=\"ratioPercentage\">");
        sb.append(format("<b>%5.2f%%</b>&nbsp;", data.getRatio() * 100));
        sb.append("</span>\n");
        sb.append("</td>\n");
    }
}
