package ru.yandex.qatools.examples.report;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.internal.AssumptionViolatedException;
import org.modeshape.common.text.Inflector;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXB;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Throwable;

/**
 * User: eroshenkoam
 * Date: 7/15/13, 11:31 AM
 */
public class ScreenShotDifferRule extends TestWatcher {

    private File reportDirectory = new ReportProperties().getReportDirectory();

    private WebDriver driver;

    private final TestCaseResult testCaseResult;

    public ScreenShotDifferRule(WebDriver driver) {
        this.testCaseResult = new TestCaseResult();
        this.driver = driver;
    }

    public BufferedImage takeOriginScreenShot(String url) {
        testCaseResult.getOrigin().setPageUrl(url);
        return takeScreenShot(url, testCaseResult.getOrigin().getFileName());
    }

    public BufferedImage takeModifiedScreenShot(String url) {
        testCaseResult.getModified().setPageUrl(url);
        return takeScreenShot(url, testCaseResult.getModified().getFileName());
    }

    public long diff(BufferedImage original, BufferedImage modified) {
        DiffImage diffImage = ImageDiffUtil.diffImage(original, modified);
        File diffFile = getReportFileFor(testCaseResult.getDiff().getFileName());
        try {
            ImageIO.write(diffImage.getImage(), "png", diffFile);
        } catch (IOException e) {
            throw new RuntimeException("Can't write diff images into: " + diffFile.getAbsolutePath());
        }
        return diffImage.getPixels();
    }

    protected void starting(Description description) {
        if (!(reportDirectory.exists() || reportDirectory.mkdirs())) {
            throw new RuntimeException("Can not access report directory: " + reportDirectory);
        }
        String uid = String.format("%s.%s", description.getClassName(), description.getMethodName());
        this.testCaseResult.setUid(uid);
        this.testCaseResult.setTitle(new Inflector().humanize(new Inflector().underscore(description.getMethodName())));

        this.testCaseResult.setOrigin(new ScreenShotData());
        this.testCaseResult.getOrigin().setFileName(getReportFileFor(uid, "origin", "png").getName());

        this.testCaseResult.setModified(new ScreenShotData());
        this.testCaseResult.getModified().setFileName(getReportFileFor(uid, "modified", "png").getName());

        this.testCaseResult.setDiff(new DiffData());
        this.testCaseResult.getDiff().setFileName(getReportFileFor(uid, "diff", "png").getName());
        super.starting(description);
    }

    protected void finished(Description description) {
        File file = getReportFileFor(testCaseResult.uid, "testcase", "xml");
        JAXB.marshal(testCaseResult, file.getAbsolutePath());
        super.finished(description);
    }

    protected void succeeded(Description description) {
        testCaseResult.setStatus(Status.OK);
        super.succeeded(description);
    }

    protected void failed(Throwable e, Description description) {
        testCaseResult.setMessage(e.getMessage());
        if (e instanceof AssumptionViolatedException) {
            testCaseResult.setStatus(Status.FAIL);
        } else {
            testCaseResult.setStatus(Status.ERROR);
        }
        super.failed(e, description);
    }

    protected void skipped(AssumptionViolatedException e, Description description) {
        super.skipped(e, description);
    }

    private BufferedImage takeScreenShot(String url, String fileName) {
        driver.get(url);
        byte[] bytes = ((TakesScreenshot) new Augmenter().augment(driver)).getScreenshotAs(OutputType.BYTES);
        File imageFile = getReportFileFor(fileName);

        try {
            if (!imageFile.createNewFile()) {
                throw new RuntimeException("Can not create file " + imageFile.getAbsolutePath());
            }
            FileUtils.writeByteArrayToFile(imageFile, bytes);
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException("Can not read file: " + imageFile, e);
        }
    }

    private File getReportFileFor(String name) {
        return new File(reportDirectory, name);
    }

    private File getReportFileFor(String uid, String postfix, String restriction) {
        return getReportFileFor(String.format("%s-%s.%s", uid, postfix, restriction));
    }

}
