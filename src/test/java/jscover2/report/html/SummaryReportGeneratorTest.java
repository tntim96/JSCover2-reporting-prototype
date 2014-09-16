package jscover2.report.html;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jscover2.instrument.Configuration;
import jscover2.instrument.Instrumenter;
import jscover2.report.JSCover2Data;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;

public class SummaryReportGeneratorTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Invocable invocable = (Invocable) engine;
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() throws Exception {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
        loadFile("src/test/resources/dir1/complexBE.js");
        loadFile("src/test/resources/dir2/fib.js");
    }

    private void loadFile(String filePath) throws IOException, ScriptException {
        File file = new File(filePath);
        String code = FileUtils.readFileToString(file);
        String instrumented = instrumenter.instrument(filePath, code);
        engine.eval(instrumented);
    }

    @Test
    public void shouldGenerateIndexHtml() throws ScriptException, NoSuchMethodException, IOException {
        invocable.invokeFunction("validCode", "var x = y;");
        invocable.invokeFunction("validCode", "");
        JSCover2Data jsCover2Data = new JSCover2Data((ScriptObjectMirror) engine.eval(config.getCoverVariableName()));
        SummaryReportGenerator summaryReportGenerator = new SummaryReportGenerator(jsCover2Data, new File("."), new File("target/report"));
        String html = summaryReportGenerator.generateIndexHtml();
        FileUtils.writeStringToFile(new File("index.html"), html);
    }

    @Test
    public void shouldGenerateReport() throws ScriptException, NoSuchMethodException, IOException {
        invocable.invokeFunction("validCode", "var x = y;");
        invocable.invokeFunction("validCode", "");
        invocable.invokeFunction("fib", 3);
        JSCover2Data jsCover2Data = new JSCover2Data((ScriptObjectMirror) engine.eval(config.getCoverVariableName()));
        SummaryReportGenerator summaryReportGenerator = new SummaryReportGenerator(jsCover2Data, new File("."), new File("target/report"));
        summaryReportGenerator.generateReport();
    }

}