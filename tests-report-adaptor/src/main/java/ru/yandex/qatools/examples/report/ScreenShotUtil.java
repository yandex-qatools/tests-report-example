package ru.yandex.qatools.examples.report;

import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * User: eroshenkoam
 * Date: 7/15/13, 12:21 PM
 */
public class ScreenShotUtil {

    public static File saveScreenShot(BufferedImage image) throws IOException {
        ReportProperties reportProperties = new ReportProperties();
        File imageFile = new File(reportProperties.getReportDirectory(), "imageNumberOne.png");
        if (imageFile.createNewFile()) {
            ImageIO.write(image, "png", imageFile);
            return imageFile;
        } else {
            throw new RuntimeException("Can not create image file: " + imageFile);
        }
    }

    public static BufferedImage getScreenShot(WebDriver driver) {
        byte[] screenBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        ByteArrayInputStream stream = null;
        BufferedImage pageScreen;
        try {
            stream = new ByteArrayInputStream(screenBytes);
            pageScreen = ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
        }

        return pageScreen;
    }

    public static BufferedImage getCroppedScreenShot(WebDriver driver, WebElement element) {
        BufferedImage pageScreen = getScreenShot(driver);
        Point location = element.getLocation();
        Dimension size = element.getSize();

        return pageScreen.getSubimage(
                location.getX(),
                location.getY(),
                size.getWidth(),
                size.getWidth()
        );
    }

}
