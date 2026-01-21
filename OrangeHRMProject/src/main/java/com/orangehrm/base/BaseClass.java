package com.orangehrm.base;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {

	protected static Properties prop;
	// protected static WebDriver driver;
	// protected static ActionDriver actionDriver;

	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();
	public static final Logger logger = LoggerManager.getLoggerManager(BaseClass.class);
	
	protected static ThreadLocal<SoftAssert> softAssert=ThreadLocal.withInitial(SoftAssert::new);
	
	//getter method for soft Assert
	public SoftAssert getSoftAssert() {
		return softAssert.get();
	}
	
	@BeforeSuite
	public void loadconfig() throws IOException {
		// Load Config file
		prop = new Properties();
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"/src/main/resources/config.properties");
		prop.load(fis);
		logger.info("config.properties file loaded");

		// start the extentreport
		//ExtentManager.getReporter();--implemented in listeners
	}

	@BeforeMethod
	public void setup() throws IOException {
		System.out.println("Setting up webdriver for the test case " + this.getClass().getSimpleName());
		launchbrowser();
		congifurebrowser();
		staticWait(2);
		logger.info("Webdriver initialized and Browser maximized");
		logger.warn("This is a warn message");
		logger.fatal("This is a fatal message");
		logger.error("This is a error message");
		logger.trace("This is a trace message");
		logger.debug("This is a debug message");
		/*
		 * if(actionDriver == null) { actionDriver =new ActionDriver(driver);
		 * logger.info("Intitialize the actiondriver instance"); }
		 */
		// Initialize actiondriver for this thread
		actionDriver.set(new ActionDriver(getDriver()));
		logger.info("Action driver initialized for " + Thread.currentThread().getId() + " | Driver hash: "
				+ getDriver().hashCode());
	}

	private void launchbrowser() {
		String Browser = prop.getProperty("browser");
		if (Browser.equalsIgnoreCase("chrome")) {
			ChromeOptions options= new ChromeOptions();
			//options.addArguments("--headless=new");
			//options.addArguments("--window-size=1920,1080");
			options.addArguments("--start-maximized");
			options.addArguments("--diable-gpu");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--disable-notifications");
			// driver = new ChromeDriver();
			driver.set(new ChromeDriver(options));
			ExtentManager.registerDriver(getDriver());
			logger.info("Chrome instance created");
			
		} 
		else if (Browser.equalsIgnoreCase("firefox")) {
			
			FirefoxOptions options=new FirefoxOptions();
			options.addArguments("--headless");
			options.addArguments("--disable-gpu"); // Disable GPU rendering (useful for headless mode)
			options.addArguments("--width=1920"); // Set browser width
			options.addArguments("--height=1080"); // Set browser height
			options.addArguments("--disable-notifications"); // Disable browser notifications
			options.addArguments("--no-sandbox"); // Needed for CI/CD environments
			options.addArguments("--disable-dev-shm-usage");
			// driver = new FirefoxDriver();
			driver.set(new FirefoxDriver(options));
			ExtentManager.registerDriver(getDriver());
			logger.info("Firefox instance created");
			
		} 
		else if (Browser.equalsIgnoreCase("edge")) {
			
			EdgeOptions options = new EdgeOptions();
			options.addArguments("--headless");
			options.addArguments("--window-size=1920,1080"); // Set window size
			options.addArguments("--disable-notifications"); // Disable pop-up notifications
			options.addArguments("--no-sandbox"); // Needed for CI/CD
			options.addArguments("--disable-dev-shm-usage");
			// driver = new EdgeDriver();
			driver.set(new EdgeDriver(options));
			logger.info("Edge instance created");
			ExtentManager.registerDriver(getDriver());

		} else {
			throw new IllegalArgumentException("The browser type does not match");
		}
	}

	private void congifurebrowser() {
		// Wait
		int implicitwait = Integer.parseInt(prop.getProperty("ImplicitWait"));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitwait));
		// Maximize the window
		//Dimension screen = getscreenSizeSafe();
		//getDriver().manage().window().setSize(screen);

		getDriver().manage().window().maximize();
		try {
			// Navigate to url
			String url = prop.getProperty("url");
			getDriver().get(url);
		} catch (Exception e) {
			System.out.println("The Webpage load fail" + e.getMessage());
			e.printStackTrace();
		}
	}

	// Driver getter method
	public static WebDriver getDriver() {
		if (driver.get() == null) {
			System.out.println("Driver instance is not initialized");
			throw new IllegalStateException("Driver instance is not initialized");
		}
		return driver.get();
	}

	// actiondriver getter method
	public static ActionDriver getactionDriver() {
		if (actionDriver.get() == null) {
			System.out.println("actionDriver instance is not initialized");
			throw new IllegalStateException("actionDriver instance is not initialized");
		}
		return actionDriver.get();
	}

	// getter method for prop
	public static Properties getprop() {
		return prop;
	}

	// Static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

	@AfterMethod
	public void teardown() {
		WebDriver currentDriver = getDriver();
		if (currentDriver != null) {
			try {
				currentDriver.quit();
				logger.info("Driver quit successfully");
			} catch (Exception e) {
				logger.error("Could not quit driver: " + e.getMessage());
			} finally {
				driver.remove();
				actionDriver.remove();
			}
		}
		//ExtentManager.endTest(); -- implemented through listeners
	}
	
	private static Dimension getscreenSizeSafe() {
		
		try {
			Toolkit toolkit= Toolkit.getDefaultToolkit();
			java.awt.Dimension d=toolkit.getScreenSize();
			return new Dimension(d.width,d.height);
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Dimension(1920,1080);
		}
		
		
	}

}