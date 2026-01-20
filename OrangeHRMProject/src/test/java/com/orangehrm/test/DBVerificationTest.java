package com.orangehrm.test;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DBconnection;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;

public class DBVerificationTest extends BaseClass {
	
	private HomePage homepage;
	private LoginPage loginpage;
	
	SoftAssert softAssert = getSoftAssert();
	
	@BeforeMethod
	public void setuppages() {
		loginpage= new LoginPage(getDriver());
		homepage= new HomePage(getDriver());
	}

	@Test(dataProvider = "empDBVerification", dataProviderClass = DataProviders.class)
	public void verifyEmpNamefromDB(String empID, String empName) {
		ExtentManager.logStep("Logging in with admin credentials");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");
		loginpage.userLogin(username, password);
		ExtentManager.logStep("Click on PIM tab");
		homepage.clickonPIMtab();
		ExtentManager.logStep("Search for employee");
		homepage.searchEmployee(empName);
		staticWait(2);
		
		ExtentManager.logStep("get the employee name from the database");
		Map<String, String> empDetails= DBconnection.getEmployeeDetails(empID);
		
		String firstName=empDetails.get("firstName");
		String middleName=empDetails.get("middleName");
		String lastName=empDetails.get("lastName");
		
		String firstandMiddleName= (firstName+" "+middleName).trim();
		
		ExtentManager.logStep("Verify employee first and middle name");
		softAssert.assertTrue(homepage.verifyEmpFirstandmiddleName(firstandMiddleName), "First and middle name are not matching");
		
		ExtentManager.logStep("Verify employee last name");
		softAssert.assertTrue(homepage.verifyEmpLastName(lastName), "Last name is not matching");
		
		ExtentManager.logStep("DB verification complete");
		
		softAssert.assertAll();
	}
}
