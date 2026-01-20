package com.orangehrm.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;

public class TestListeners implements ITestListener, IAnnotationTransformer {

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		// TODO Auto-generated method stub
		annotation.setRetryAnalyzer(RetryAnalyzer.class);
	}

	// Triggered when the suite starts
	@Override
	public void onStart(ITestContext context) {
		ExtentManager.getReporter();
	}

	// Triggered when test Starts
	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.startTest(testName);
		ExtentManager.logStep("Test started for "+testName);
	}

	//Triggered when test passes
	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logStepwithScreenshot(BaseClass.getDriver(), "Test passed succssfully", "Test Ended" + testName+"✓-Test passed" );
	}

	//Triggered when test fails
	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String failureMessage= result.getThrowable().getMessage();
		ExtentManager.logStep(failureMessage);
		ExtentManager.logfailure(BaseClass.getDriver(), "Test failed", "Test Ended" + testName+"x-Test failed" );
	}

	
	//triggered when a test is skipped
	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logSkipstep("Test skipped "+testName);
	}

	//triggered at the end of each test
	@Override
	public void onFinish(ITestContext context) {
		ExtentManager.endTest();
	}

}
