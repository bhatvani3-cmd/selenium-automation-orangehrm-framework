package com.orangehrm.test;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class LoginPageTest extends BaseClass{
	
	private HomePage homepage;
	private LoginPage loginpage;
	
	@BeforeMethod
	public void setuppage() {
		loginpage= new LoginPage(getDriver());
		homepage= new HomePage(getDriver());
	}
	
	@Test(dataProvider = "validLogindata", dataProviderClass = DataProviders.class)
	public void verifyvalidlogin(String username, String password) {
		//ExtentManager.startTest("Valid login test 0");
		System.out.println("Running test on Thread"+Thread.currentThread().getId());
		ExtentManager.logStep("Entering username and password");
		//String username = prop.getProperty("username");
		//String password = prop.getProperty("password");
		loginpage.userLogin(username, password);
		Assert.assertTrue(homepage.isAdminVisible(), "Admin tab should be visible after successful login");
		ExtentManager.logStep("Validation successful");
		staticWait(2);
		//homepage.logoutOperation();
	}
	
	@Test(dataProvider = "validLogindata", dataProviderClass = DataProviders.class)
	public void verifyvalidlogin1(String username, String password) {
		//ExtentManager.startTest("Valid login test 1");
		System.out.println("Running test on Thread"+Thread.currentThread().getId());
		ExtentManager.logStep("Entering username and password");
		//String username = prop.getProperty("username");
		//String password = prop.getProperty("password");
		loginpage.userLogin(username, password);
		Assert.assertTrue(homepage.isAdminVisible(), "Admin tab should be visible after successful login");
		//ExtentManager.logStep("Validation successful");
		staticWait(2);
		//homepage.logoutOperation();
	}
	
}
