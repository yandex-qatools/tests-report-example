package ru.yandex.qatools.examples.report;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.CanReadFileFilter;
import org.apache.commons.io.filefilter.CanWriteFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import javax.xml.bind.JAXB;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static ch.lambdaj.Lambda.convert;
import static ru.yandex.qatools.examples.report.TemplateProcessorUtil.processTemplate;

/**
 * User: eroshenkoam
 * Date: 7/15/13, 6:29 PM
 */
@SuppressWarnings("unused")
@Mojo(name = "generate-report", defaultPhase = LifecyclePhase.SITE)
public class ScreenShotDifferReport extends AbstractMavenReport {

	private static final List<String> STATIC_REPORT_FILE_NAMES = Arrays.asList(
			"custom.css",
			"custom.js"
	);

	ReportProperties properties = new ReportProperties();

    @Component
    @SuppressWarnings("unused")
    private PluginDescriptor pluginDescriptor;

    @SuppressWarnings("unused")
    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject mavenProject;

    @Component
    @SuppressWarnings("unused")
    private Renderer siteRenderer;

    @SuppressWarnings("unused")
    @Parameter(defaultValue = "${project.basedir}")
    private File baseDir;

    @Override
    public String getOutputName() {
        return pluginDescriptor.getArtifactId();
    }

    protected File getReportDirectory() {
        return new File(baseDir.getAbsolutePath(), "target/site/custom");
    }

    @Override
    protected String getOutputDirectory() {
        return getReportDirectory().getAbsolutePath();
    }

    @Override
    public String getName(Locale locale) {
        return "My Custom Report";
    }

    @Override
    public String getDescription(Locale locale) {
        return "My Custom Report Description";
    }

    @Override
    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    @Override
    protected MavenProject getProject() {
        return mavenProject;
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {

        try {
            if (!getReportDirectory().exists()) {
                getLog().warn(String.format("Report directory <%s> doesn't exists", getReportDirectory()));
                return;
            }

			ClassLoader classLoader = this.getClass().getClassLoader();
			for (String fileName : STATIC_REPORT_FILE_NAMES) {
				FileUtils.copyInputStreamToFile(
						classLoader.getResourceAsStream(fileName),
						new File(getReportDirectory(), fileName)
				);
			}

            //Find all readable test case files in the report directory
            Collection<File> files = FileUtils.listFiles(
                    getReportDirectory(),
                    new AndFileFilter(new RegexFileFilter(".*-testcase.xml"), CanReadFileFilter.CAN_READ),
                    new AndFileFilter(CanReadFileFilter.CAN_READ, CanWriteFileFilter.CAN_WRITE));

            getLog().info(String.format("Found <%s> test case files in directory %s", files.size(), getReportDirectory()));

            //Unmarshal all founded files to the test cases collection
            Collection<TestCaseResult> testCases = convert(files, new Converter<File, TestCaseResult>() {
                @Override
                public TestCaseResult convert(File o) {
                    return JAXB.unmarshal(o, TestCaseResult.class);
                }
            });

            getLog().info(String.format("Generate report for <%s> test cases", testCases.size()));

            TestSuiteResult testSuiteResult = new TestSuiteResult();
            testSuiteResult.getTestCases().addAll(testCases);

            String source = processTemplate(getClass(), "custom-report.ftl", testSuiteResult);

            createEmbeddedReportPage(locale, source);
        } catch (Exception e) {
            throw new MavenReportException("Can not build report", e);
        }
    }

    private void createEmbeddedReportPage(Locale locale, String source) {
        Sink sink = getSink();
        sink.head();
        sink.title();
        sink.text(getName(locale));
        sink.title_();
        sink.head_();

        sink.body();
        sink.rawText(source);
        sink.body_();

        sink.flush();
        sink.close();
    }

}
