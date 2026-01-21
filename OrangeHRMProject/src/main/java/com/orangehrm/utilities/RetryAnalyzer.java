package com.orangehrm.utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

	private int retrycount=0;
	private static final int maxRetryCount =0;
	
	@Override
	public boolean retry(ITestResult result) {
		if(retrycount<maxRetryCount) {
			retrycount++;
			System.out.println("Retrying test: " + result.getName() + 
                    " Attempt: " + retrycount);
			return true;
		}
		return false;
	}
}
