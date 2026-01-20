package com.orangehrm.actiondriver;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;
	public static final Logger logger = BaseClass.logger;

	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		int explicitwait = Integer.parseInt(BaseClass.getprop().getProperty("explicitwait"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitwait));
		logger.info("Webdriver instance created");
	}

	// method to click an element
	public void clickelement(By by) {
		String elementdescription = getElementDescription(by);

		try {
			elementtobeclickable(by);
			applyBorder(by,"green");
			driver.findElement(by).click();
			ExtentManager.logStep("Click an element" + elementdescription);
			logger.info("Clicked an element " + elementdescription);
		} catch (Exception e) {
			applyBorder(by,"red");
			System.out.println("Unable to click element " + elementdescription + " " + e.getMessage());
			ExtentManager.logfailure(BaseClass.getDriver(), "Unable to click the element ",
					elementdescription + "_unable to click");
			logger.error("unable Clicked an element");
			throw e;
		}
	}

	// method to send text to an input field
	public void entertext(By by, String value) {
		try {
			elementtobevisible(by);
			applyBorder(by,"green");
			driver.findElement(by).clear();
			driver.findElement(by).sendKeys(value);
			logger.info("Text entered at" + getElementDescription(by));

		} catch (Exception e) {
			applyBorder(by,"red");
			logger.error("Unable to enter text " + getElementDescription(by) + " " + e.getMessage());
			ExtentManager.logfailure(BaseClass.getDriver(), "Error entering the element",
					"Error entering the element" + getElementDescription(by));
			throw e;
		}
	}

	// method to get text from an input field
	public String getText(By by) {
		try {
			elementtobevisible(by);
			applyBorder(by,"green");
			return driver.findElement(by).getText();

		} catch (Exception e) {
			applyBorder(by,"red");
			System.out.println("could not get text from " + getElementDescription(by) + e.getMessage());
			logger.error("unable to get text");
			return "";
		}

	}

// check if an element is displayed
	public boolean isDisplayed(By by) {
		try {
			elementtobevisible(by);
			applyBorder(by,"green");
			logger.info("Element is displayed " + getElementDescription(by));
			ExtentManager.logStep("Element is displayed " + getElementDescription(by));
			ExtentManager.logStepwithScreenshot(BaseClass.getDriver(), "Element is displayed",
					getElementDescription(by) + "is the displayed element");
			return driver.findElement(by).isDisplayed();
		} catch (Exception e) {
			applyBorder(by,"red");
			ExtentManager.logfailure(BaseClass.getDriver(), "Error displaying the element",
					"Error displaying the element" + getElementDescription(by));
			logger.error("Error displaying the element " + getElementDescription(by) + " " + e.getMessage());
			return false;
		}

	}

	// Scroll to an element
	public void scrollToElement(By by) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("arguments[0].scrollIntoView(true);", element);
			logger.info("Scrolled to the Element");

		} catch (Exception e) {
			logger.error("Error scrolling to the element" + e.getMessage());
		}
	}

	public void waitForPageToLoad(int timeOutInSec) {
		try {
			wait.withTimeout(Duration.ofSeconds(timeOutInSec)).until(WebDriver -> (JavascriptExecutor) WebDriver)
					.executeScript("return document.readyState").equals("complete");
		} catch (Exception e) {
			logger.error("Error loading page " + e.getMessage());
		}

	}

	// method to compare text
	public boolean compareText(By by, String expectedText) {
		try {
			elementtobevisible(by);
			String actualText = driver.findElement(by).getText();
			if (actualText.equals(expectedText)) {
				applyBorder(by,"green");
				System.out.println("Text are matching " + actualText + " equals " + expectedText);
				ExtentManager.logStep("Text are matching " + actualText + " equals " + expectedText);
				logger.info("compared text match");
				return true;
			} else {
				applyBorder(by,"red");
				logger.error("Text are not matching " + actualText + " not equals " + expectedText);
				ExtentManager.logfailure(BaseClass.getDriver(), "Compare Text",
						"Text are not matching " + actualText + " not equals " + expectedText);
				return false;
			}
		} catch (Exception e) {
			applyBorder(by,"red");
			logger.error("Unable to compare Text at " + getElementDescription(by) + " " + e.getMessage());
			return false;
		}
	}

	// wait for element to be clickable
	private void elementtobeclickable(By by) {

		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("Element is not clickable" + by.toString() + e.getMessage());
		}
	}
	/*
	 * private WebElement elementtobeclickable(By by) { return
	 * wait.until(ExpectedConditions.elementToBeClickable(by)); }
	 */

	// wait for the element to be visible
	private void elementtobevisible(By by) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			logger.error("Element is not visible" + by.toString() + e.getMessage());
		}
	}

	public String getElementDescription(By by) {

		if (driver == null) {
			return "Driver is not initialized";
		}
		if (by == null) {
			return "locator is null";
		}
		try {
			// Find the element using the locator
			WebElement element = driver.findElement(by);

			// get element attributes
			String name = element.getDomAttribute("name");
			String id = element.getDomAttribute("id");
			String clazz = element.getDomAttribute("class");
			String placeholder = element.getDomAttribute("placeholder");
			String text = element.getText();

			if (isNotEmpty(name)) {
				return "Element with name " + name;
			} else if (isNotEmpty(id)) {
				return "Element with id " + id;
			} else if (isNotEmpty(clazz)) {
				return "Element with class " + clazz;
			} else if (isNotEmpty(placeholder)) {
				return "Element with placeholder " + placeholder;
			} else if (isNotEmpty(text)) {
				return "Element with Text " + trunkateString(text, 30);
			} else {
				return "Element located using: " + by.toString();
			}
		} catch (Exception e) {
			logger.error("Unable to get Description " + e.getMessage());
			return "Element located by: " + by.toString();
		}
	}

	// method to check if a string is not null or empty
	private boolean isNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}

	// Truncate long string
	private String trunkateString(String value, int maxLength) {
		if (value == null || value.length() < maxLength) {
			return value;

		}

		return value.substring(0, maxLength) + "...";
	}

	public void applyBorder(By by, String color) {
		try {
			WebElement element = driver.findElement(by);
			String script = "arguments[0].style.border='3px solid " + color + "'";
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(script, element);
			logger.info("Applied border successfully with " + color + " for the element " + getElementDescription(by));
		} catch (Exception e) {
			logger.warn("failed to apply border for " + getElementDescription(by));
			e.printStackTrace();
		}
	}
	 // Method to click using JavaScript
    public void clickUsingJS(By by) {
        try {
            WebElement element = driver.findElement(by);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            applyBorder(by, "green");
            logger.info("Clicked element using JavaScript: " + getElementDescription(by));
        } catch (Exception e) {
            applyBorder(by, "red");
            logger.error("Unable to click using JavaScript", e);
        }
    }
    
}
