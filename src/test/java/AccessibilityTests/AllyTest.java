package AccessibilityTests;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.deque.axe.AXE;

import io.github.bonigarcia.wdm.WebDriverManager;

public class AllyTest {

    WebDriver driver;

    private static final URL scriptUrl = AllyTest.class.getResource("/axe.min.js");

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.irctc.com/");

        // ✅ Wait for page load completely
        waitForPageLoad();
    }

    @Test
    public void AllyTest() {
        JSONObject responseJSON = new AXE.Builder(driver, scriptUrl).analyze();
        JSONArray violations = responseJSON.getJSONArray("violations");

        if (violations.length() == 0) {
            System.out.println("No violations found");
        } else {
            AXE.writeResults("AllyTest", responseJSON);
            System.out.println(violations.length() + " violations found");
            throw new AssertionError(AXE.report(violations));
        }
    }

    @AfterMethod(alwaysRun = true)
    public void generateAccessibilityReportsAndTearDown() {
        try {
            AccessibilityReportGenerator generator = new AccessibilityReportGenerator();
            generator.generateReports("AllyTest.json"); // ✅ generate CSV + HTML
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit(); // ✅ always quit browser
            }
        }
    }

    // ✅ Utility: Wait until JS ready state = complete
    private void waitForPageLoad() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < 30; i++) { // wait max ~30s
            String readyState = js.executeScript("return document.readyState").toString();
            if ("complete".equals(readyState)) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
