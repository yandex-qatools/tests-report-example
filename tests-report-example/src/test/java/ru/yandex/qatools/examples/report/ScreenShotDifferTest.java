package ru.yandex.qatools.examples.report;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

/**
 * User: eroshenkoam
 * Date: 7/15/13, 11:48 AM
 */
@RunWith(Parameterized.class)
public class ScreenShotDifferTest {

    private String originPageUrl;

    private String modifiedPageUrl;

    private WebDriverProperties webDriverProperties = new WebDriverProperties();

    private WebDriver driver = new RemoteWebDriver(webDriverProperties.getServer(), DesiredCapabilities.firefox());

    @Rule
    public ScreenShotDifferRule screenShotDiffer = new ScreenShotDifferRule(driver);

    public ScreenShotDifferTest(String title, String originPageUrl, String modifiedPageUrl) {
        this.modifiedPageUrl = modifiedPageUrl;
        this.originPageUrl = originPageUrl;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> provideData() {
        return Arrays.asList(
                new Object[]{"Yandex Main Page", "http://www.yandex.ru/", "http://www.yandex.ru/"},
                new Object[]{"Yandex.Market Main Page", "http://market.yandex.ru/", "http://market.yandex.ru/"}
        );
    }

    @Test
    public void originShouldBeSameAsModified() throws Exception {
        BufferedImage originScreenShot = screenShotDiffer.takeOriginScreenShot(originPageUrl);
        BufferedImage modifiedScreenShot = screenShotDiffer.takeModifiedScreenShot(modifiedPageUrl);
        long diffPixels = screenShotDiffer.diff(originScreenShot, modifiedScreenShot);

        assertThat(diffPixels, lessThan((long) 20));
    }

    @After
    public void closeDriver() {
        driver.quit();
    }
}
