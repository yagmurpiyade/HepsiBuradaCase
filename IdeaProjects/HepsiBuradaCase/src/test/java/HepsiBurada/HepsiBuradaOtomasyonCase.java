package HepsiBurada;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HepsiBuradaOtomasyonCase {
    WebDriver driver;
    WebDriverWait wait;
    public static ExtentTest test;
    public static ExtentReports report;
    String url, productNameOnBasket, productPriceOnBasket, productNameOnSearchPage, productPriceOnSearchPage;

    @BeforeTest
    @Parameters("browser")
    public void setup(String browser) throws Exception {
        if (browser.equalsIgnoreCase("firefox")) {
            System.setProperty("webdriver.gecko.driver", "C:\\Users\\yagmur.kilic\\IdeaProjects\\HepsiBuradaCase\\src\\test\\driver\\geckodriver.exe");
            driver = new FirefoxDriver();
        } else if (browser.equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.chrome.driver", "C:\\Users\\yagmur.kilic\\IdeaProjects\\HepsiBuradaCase\\src\\test\\driver\\chromedriver.exe");
            driver = new ChromeDriver();
        } else {
            throw new Exception("Incorrect Browser");
        }
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, 30);
        driver.manage().window().maximize();
        report = new ExtentReports(System.getProperty("user.dir") + "ExtentReportResults.html");
        test = report.startTest("HepsiBuradaOtomasyonCase");
    }

    @Test
    @Parameters("NumberOfElementToAdd")
    public void Control(String NumberOfElementToAdd) throws IOException {
        try {
            url = "https://www.hepsiburada.com/";
            Yonlendir(url);
            ClickGirisYap();
            //login page ekranı dolduruluyor
            CheckLoginPage();
            Thread.sleep(5000);
            url = "https://www.hepsiburada.com/kampanyalar/yurt-disindan-urunler?wt_int=hytop.yurtdisi.kampanya";
            Yonlendir(url);
            //yurtdışı kampanya linkinde cookie kapatilir ve
            // ekran scroll edilerek soldaki menüden PetShop'a tıklanır
            CloseCookie();
            Thread.sleep(3000);
            LinkToPetShop();
            AddToBasket(NumberOfElementToAdd);
            //sepete git
            GoToBasket();
            //ürün bilgileri kıyasla
            Assertions.assertAll(
                    () -> Assertions.assertEquals(productNameOnSearchPage, productNameOnBasket),
                    () -> Assertions.assertEquals(productPriceOnSearchPage, productPriceOnBasket),
                    () -> Assertions.assertEquals("https://checkout.hepsiburada.com/sepetim", driver.getCurrentUrl())
            );
            test.log(LogStatus.PASS, "HepsiBurada case başarılı tamamlandı");
        } catch (Exception e) {
            test.log(LogStatus.FAIL, "Test Hatalı: " + e.getMessage());
            CaptureScreen();
            driver.quit();
        }


    }

    @AfterTest
    public static void endTest() {
        report.endTest(test);
        report.flush();
    }

    private void AddToBasket(String NumberOfElementToAdd) {
        int nthElement = Integer.parseInt(NumberOfElementToAdd);
        productNameOnSearchPage = driver.findElements(By.cssSelector("[data-test-id*='product-card-name']")).get(nthElement).getText();
        productPriceOnSearchPage = driver.findElements(By.cssSelector("[data-test-id*='price-current-price']")).get(nthElement).getText();
        WebElement product = driver.findElements(By.cssSelector("[data-test-id='product-info-wrapper']")).get(nthElement);
        product.click();
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles()); //yeni sekmede açıldığı için handle edilir
        driver.switchTo().window(tabs.get(1));
        driver.findElement(By.id("addToCart")).click();
    }

    private void LinkToPetShop() {

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,700)", "");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Pet Shop')]")));
        WebElement petShop = driver.findElement(By.xpath("//div[contains(text(),'Pet Shop')]"));
        //js.executeScript("arguments[0].click();", petShop);
        //js1.executeScript("window.document.getElementByXpath('//div[contains(text(),\"Pet Shop\")]').click()");
        driver.get("https://www.hepsiburada.com/kampanyalar/yurt-disindan-urunler?kategori=2147483616&wt_int=hytop.yurtdisi.kampanya");
    }

    private void Yonlendir(String url) {
        driver.get(url);
    }

    private void ClickGirisYap() {
        driver.findElement(By.cssSelector("[title='Giriş Yap']")).click();//giriş yap pop-up açma
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login")));
        driver.findElement(By.id("login")).click(); //giris yap ekranına git
    }

    private void CloseCookie() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("onetrust-accept-btn-handler")));
        driver.findElement(By.id("onetrust-accept-btn-handler")).click();
    }

    private void CheckLoginPage() throws InterruptedException {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#txtUserName")));
        WebElement username = driver.findElement(By.cssSelector("input#txtUserName"));
        username.sendKeys("yagmurtesthb@gmail.com");

        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnLogin")));
        WebElement loginButton = driver.findElement(By.id("btnLogin"));
        loginButton.click(); //giris yap butonu tıklama

        //password ekranı dolduruluyor
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#txtPassword")));
        WebElement password = driver.findElement(By.cssSelector("input#txtPassword"));
        password.sendKeys("Test123456+");

        wait.until(ExpectedConditions.elementToBeClickable(By.id("btnEmailSelect")));
        Thread.sleep(7000);
        WebElement loginButton2 = driver.findElement(By.id("btnEmailSelect"));
        loginButton2.click();

    }

    private void GoToBasket() {
        driver.findElement(By.id("shoppingCart")).click();
        productNameOnBasket = driver.findElement(By.cssSelector("[class*='product_name']")).getText();
        productPriceOnBasket = driver.findElement(By.cssSelector("[class*='product_price_box']")).getText();
    }

    private void CaptureScreen() throws IOException {

        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshotFile, new File("C:\\SoftwareTestingMaterial.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

