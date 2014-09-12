package jscover2.report.html;

import jscover2.report.CoverageSummaryData;
import jscover2.report.CoverageSummaryItem;
import jscover2.report.JSCover2CoverageSummary;
import jscover2.report.JSCover2Data;

import java.io.File;

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
        sb.append("<head>\n" +
                "    <title>JSCover2 Coverage Report</title>\n" +
                "    <link rel=\"stylesheet\" href=\"jscover2.css\" type=\"text/css\"/>\n" +
                "</head>\n");
    }

    private void buildBody(StringBuilder sb) {
        sb.append("<body>\n");
        sb.append("<table border=\"1\">\n");
        sb.append("<tr><th>Statement</th><th>Branch</th><th>Boolean Expression</th><th>Function</th><th>Line</th></tr>\n");
        sb.append("<tr>\n");
        CoverageSummaryData data = summary.getTotals();
        buildMetric(sb, data.getStatementCoverage());
        buildMetric(sb, data.getBranchPathCoverage());
        buildMetric(sb, data.getBooleanExpressionCoverage());
        buildMetric(sb, data.getFunctionCoverage());
        buildMetric(sb, data.getLineCoverage());
        sb.append("</tr>\n");
        sb.append("</table>\n");
        sb.append("</body>\n");
    }

    private void buildMetric(StringBuilder sb, CoverageSummaryItem data) {
        sb.append("<td align=\"right\">");
        sb.append("<span>");
        sb.append(format("<b>%5.2f%%</b>&nbsp;", data.getRatio() * 100));
        sb.append(data.getCovered());
        sb.append("/");
        sb.append(data.getTotal());
        sb.append("</span>\n");
        sb.append("</td>\n");
    }
}
