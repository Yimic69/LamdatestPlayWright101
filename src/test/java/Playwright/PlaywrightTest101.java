package Playwright;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.SelectOption;

public class PlaywrightTest101 {

	Playwright playwright;
	Browser browser;
	String caps;
	String cdpUrl;
	Page page;
	SoftAssert softAssert = new SoftAssert();

	@BeforeMethod
	@Parameters({ "browserName", "browserVersion", "platform" })
	public void setup(String browsername, String browserVersion, String platform) throws UnsupportedEncodingException {
		JsonObject capabilities = new JsonObject();
		JsonObject ltOptions = new JsonObject();
		String user = "samarjitkundu99";
		String accessKey = "LT_Fl6EVG8YdtqArLvzkp8RKtSzVHf72PPjQyzIs03x8hYmoWP";
		capabilities.addProperty("browsername", browsername);
		capabilities.addProperty("browserVersion", browserVersion);
		ltOptions.addProperty("platform", platform);
		ltOptions.addProperty("name", browsername.substring(3)+" - "+platform);
		ltOptions.addProperty("build", "Playwright101");
		ltOptions.addProperty("user", user);
		ltOptions.addProperty("accessKey", accessKey);
		ltOptions.addProperty("visual", true);
		ltOptions.addProperty("video", true);
		ltOptions.addProperty("network", true);
		capabilities.add("LT:Options", ltOptions);

		playwright = Playwright.create();
		switch (browsername) {
		case "pw-firefox":
			BrowserType firefox = playwright.firefox();
			caps = URLEncoder.encode(capabilities.toString(), "utf-8");
			cdpUrl = "ws://cdp.lambdatest.com/playwright?capabilities=" + caps;
			browser = firefox.connect(cdpUrl);
			break;
		case "pw-chromium":
			BrowserType chromium = playwright.chromium();
			caps = URLEncoder.encode(capabilities.toString(), "utf-8");
			cdpUrl = "ws://cdp.lambdatest.com/playwright?capabilities=" + caps;
			browser = chromium.connect(cdpUrl);
			break;
		}
		page = browser.newPage();
	}

	@Test(priority = 1)
	public void test_scenario1() throws Exception {
		page.navigate("https://www.lambdatest.com/selenium-playground");
		page.click("text=Simple Form Demo");
		softAssert.assertTrue(page.url().contains("simple-form-demo"),
				"The page URL does not contain 'simple-form-demo'");
		String message = "Welcome to LambdaTest";

		page.fill("#user-message", message);

		page.click("//button[@id='showInput']");
		String displayedMessage = page.innerText("#message");
		softAssert.assertEquals(displayedMessage, message,
				"The message displayed does not match with the one that was entered");
		System.out.println(displayedMessage);
		softAssert.assertAll();
	}

	@Test(priority = 2)
	public void test_scenario2() throws Exception {
		page.navigate("https://www.lambdatest.com/selenium-playground");
		page.waitForSelector("text=Drag & Drop Sliders");
		page.click("text=Drag & Drop Sliders");
		Locator slider = page.locator("//input[@value=15]");
		BoundingBox initialBBox = slider.boundingBox();
		// Dragging the slider to 95
		slider.hover();
		page.mouse().move(initialBBox.x + initialBBox.width / 2, initialBBox.y + initialBBox.height / 2);
		// Hover at the center of the slider
		page.mouse().down();
		page.mouse().move(initialBBox.x + 465, initialBBox.y);
		// Adjust to the right
		page.mouse().up();
		Locator outputElement = page.locator("#rangeSuccess");
		String outputText = outputElement.textContent();
		softAssert.assertEquals(outputText, "95", "Slider value does not equal 95");
		softAssert.assertAll();

	}

	@Test(priority = 3)
	public void test_scenario3() throws Exception {
		page.navigate("https://www.lambdatest.com/selenium-playground");
		page.locator("text=Input Form Submit").click();
		Locator submitBtn = page.locator("//button[contains(text(),'Submit')]");
		submitBtn.click();
		//String validationMsg = page.locator("#name").getAttribute("validationMessage");
		String validationMsg = (String) page.locator("#company").evaluate("el => el.validationMessage");
		String expectedErrorMsg = "Please fill out this field.";
		softAssert.assertEquals(validationMsg, expectedErrorMsg, "Error message not as expected");
		page.fill("#name", "Samarjit Kundu");
		page.fill("//input[@placeholder='Email']", "samarjitkundu99@gmail.com");
		page.fill("//input[@placeholder='Password']", "samarjit");
		page.fill("//input[@placeholder='Company']", "Persistent");
		page.fill("#websitename", "www.persistent.com");
		page.selectOption("select[name='country']", new SelectOption().setLabel("United States"));
		page.fill("#inputCity", "New York");
		page.fill("#inputAddress1", "HJ, CH SHJ");
		page.fill("#inputAddress2", "Greater County, New York City");
		page.fill("//input[@placeholder='State']", "New York");
		page.fill("//input[@name='zip']", "123456");
		submitBtn.click();
		softAssert.assertTrue(
				page.locator("text=Thanks for contacting us, we will get back to you shortly.").isVisible(),
				"Success message is not visible");
		softAssert.assertAll();

	}

	@AfterMethod
	public void quitDriver() {
		browser.close();
		playwright.close();

	}


}
