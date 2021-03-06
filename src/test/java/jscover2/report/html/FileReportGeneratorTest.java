package jscover2.report.html;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jscover2.instrument.Configuration;
import jscover2.instrument.Instrumenter;
import jscover2.report.FileData;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FileReportGeneratorTest {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Invocable invocable = (Invocable) engine;
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;
    private String code;
    private String pathname = "src/test/resources/script.js";

    @Before
    public void before() throws Exception {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
        code = FileUtils.readFileToString(new File(pathname));
        String instrumented = instrumenter.instrument(pathname, code);
        engine.eval(instrumented);
    }

    @Test
    public void shouldGenerateHtml() throws ScriptException, NoSuchMethodException, IOException {
        assertThat(invocable.invokeFunction("check", -1), equalTo("invalid input"));
        assertThat(invocable.invokeFunction("check", 1), equalTo("one or two"));
        assertThat(invocable.invokeFunction("check", 2), equalTo("one or two"));
        assertThat(invocable.invokeFunction("check", 4), equalTo("four"));
        assertThat(invocable.invokeFunction("aORbANDc", false, true, false), equalTo(false));

        JSCover2Data jsCover2Data = new JSCover2Data((ScriptObjectMirror) engine.eval(config.getCoverVariableName()));
        FileData fileData = jsCover2Data.getDataMap().get(pathname);
        assertThat(fileData.getStatements().size(), equalTo(17));

        FileReportGenerator fileReportGeneratorReport = new FileReportGenerator(pathname, code, fileData);
        String html = fileReportGeneratorReport.generateHtml();
        FileUtils.writeStringToFile(new File("file.html"), html);
    }
}
