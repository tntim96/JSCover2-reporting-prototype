package jscover2.report.html;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jscover2.instrument.Configuration;
import jscover2.instrument.Instrumenter;
import jscover2.report.CoverageData;
import jscover2.report.FileData;
import jscover2.report.FileDataBuilder;
import jscover2.report.JSCover2Data;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestFileReport {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private Invocable invocable = (Invocable) engine;
    private Configuration config = new Configuration();
    private Instrumenter instrumenter;

    @Before
    public void before() throws Exception {
        config.setCoverVariableName("jscover");
        instrumenter = new Instrumenter(config);
        String code = FileUtils.readFileToString(new File("src/test/resources/script.js"));
        String instrumented = instrumenter.instrument("test.js", code);
        engine.eval(instrumented);
    }

    @Test
    public void should() throws ScriptException, NoSuchMethodException {
        assertThat(invocable.invokeFunction("check", 1), equalTo("one or two"));
        assertThat(invocable.invokeFunction("check", 4), equalTo("four"));

        JSCover2Data jsCover2Data = new JSCover2Data((ScriptObjectMirror) engine.eval(config.getCoverVariableName()));
        assertThat(jsCover2Data.getDataMap().get("test.js").getStatements().size(), equalTo(13));
    }
}
