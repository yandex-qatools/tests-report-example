package ru.yandex.qatools.examples.report;

import ru.yandex.qatools.properties.PropertyLoader;
import ru.yandex.qatools.properties.annotations.Property;
import ru.yandex.qatools.properties.annotations.Resource;

import java.io.File;

/**
 * User: eroshenkoam
 * Date: 7/15/13, 12:16 PM
 */
@Resource.Classpath("report.properties")
public class ReportProperties {

    @Property("report.directory")
    private File reportDirectory;

    @Property("report.directory")
    private String reportDirectoryPath;

    public ReportProperties() {
        PropertyLoader.populate(this);
    }

    public File getReportDirectory() {
        return reportDirectory;
    }

    public String getReportDirectoryPath() {
        return reportDirectoryPath;
    }
}
