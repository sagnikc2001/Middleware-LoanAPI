package com.dhdigital.middleware.customer.bean;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alahli.middleware.utility.Utils.Utils;
import com.dhdigital.middleware.customer.model.LoanAccountDetails;
import com.dhdigital.middleware.customer.model.NewLoanApplicationRequest;
import com.dhdigital.middleware.customer.model.NewLoanApplicationRequestBackend;
import com.dhdigital.middleware.customer.model.NewLoanApplicationResponse;
import com.dhdigital.middleware.customer.model.NewLoanApplicationResponseBackend;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class NewLoanApplicationService {

	@Autowired
	Utils utils;
	
	@Autowired
	ObjectMapper objectMapper;

	public NewLoanApplicationRequest oNewLoanApplicationRequest;

	int interestRate = 0;
	String status = null;

	public NewLoanApplicationRequest setNewLoanApplicationRequestBackendIn(
			NewLoanApplicationRequestBackend oNewLoanApplicationRequestBackend, Exchange ex) throws Exception {

		oNewLoanApplicationRequest = oNewLoanApplicationRequestBackend.getNewLoanApplicationRequest();

		return oNewLoanApplicationRequest;
	}

	public void getLoanEligibilityAndInterestRate() throws Exception {

		int baseInterestRate = 15;
		int creditScore = oNewLoanApplicationRequest.getLoanDetails().getCreditScore();
		int creditScorePenalty = 1;
		int annualSalary = oNewLoanApplicationRequest.getPersonalDetails().getApplicant().getAnnualSalary();
		int minimumSalary = 300000;
		int loanAmount = oNewLoanApplicationRequest.getLoanDetails().getLoanAmount();
		int maximumLoanAmount = 1000000;
		String loanType = oNewLoanApplicationRequest.getLoanDetails().getLoanType();

		// Calculate interest rate
		interestRate = calculateInterestRate(baseInterestRate, creditScorePenalty, creditScore, loanType);

		// Check whether to grant the loan
		boolean grantLoan = shouldGrantLoan(annualSalary, minimumSalary, loanAmount, creditScore, maximumLoanAmount);
		System.out.println("Grant Loan = " + grantLoan);

		status = (grantLoan) ? "Approved" : "Rejected";
	}

	private static int calculateInterestRate(int baseInterestRate, int creditScorePenalty, int creditScore,
			String loanType) {
		int loanTypeFactor = getLoanTypeFactor(loanType);
		return baseInterestRate - creditScorePenalty * (creditScore / 100) + loanTypeFactor;
	}

	private static boolean shouldGrantLoan(int annualSalary, int minimumSalary, int loanAmount, int creditScore,
			int maximumLoanAmount) {
		return (annualSalary >= minimumSalary) && (loanAmount <= maximumLoanAmount) && (creditScore > 250);
	}

	private static int getLoanTypeFactor(String loanType) {

		switch (loanType.toLowerCase()) {
		case "home":
			return -3; // Lowest interest rate for Home Loans
		case "car":
			return -2; // Moderate interest rate for Car Loans
		case "personal":
			return 0; // Higher interest rate for Personal Loans
		default:
			return 3; // Highest Interest rates for unknown loan types
		}
	}

	public JsonNode prepareNewLoanDetailsDBRequest() throws Exception {

		ObjectNode oNewLoanDetailsObjectNode = JsonNodeFactory.instance.objectNode();
		ObjectNode oNewLoanDetails = oNewLoanDetailsObjectNode.putObject("NewLoanApplicationDetails");

		oNewLoanDetails.put("fullName", oNewLoanApplicationRequest.getPersonalDetails().getApplicant().getFullName());
		oNewLoanDetails.put("location",
				oNewLoanApplicationRequest.getPersonalDetails().getApplicant().getAddress().getLocation());
		oNewLoanDetails.put("city",
				oNewLoanApplicationRequest.getPersonalDetails().getApplicant().getAddress().getCity());
		oNewLoanDetails.put("state",
				oNewLoanApplicationRequest.getPersonalDetails().getApplicant().getAddress().getState());
		oNewLoanDetails.put("pinCode",
				oNewLoanApplicationRequest.getPersonalDetails().getApplicant().getAddress().getPinCode());
		oNewLoanDetails.put("region",
				oNewLoanApplicationRequest.getPersonalDetails().getApplicant().getAddress().getRegion());
		oNewLoanDetails.put("phoneNumber",
				oNewLoanApplicationRequest.getPersonalDetails().getApplicant().getPhoneNumber());
		oNewLoanDetails.put("emailAddress",
				oNewLoanApplicationRequest.getPersonalDetails().getApplicant().getEmailAddress());
		oNewLoanDetails.put("annualSalary",
				oNewLoanApplicationRequest.getPersonalDetails().getApplicant().getAnnualSalary());
		oNewLoanDetails.put("loanAmount", oNewLoanApplicationRequest.getLoanDetails().getLoanAmount());
		oNewLoanDetails.put("loanTerm", oNewLoanApplicationRequest.getLoanDetails().getLoanTerm());
		oNewLoanDetails.put("loanType", oNewLoanApplicationRequest.getLoanDetails().getLoanType());
		oNewLoanDetails.put("creditScore", oNewLoanApplicationRequest.getLoanDetails().getCreditScore());
		oNewLoanDetails.put("interestRate", interestRate);
		oNewLoanDetails.put("status", status);

		return oNewLoanDetailsObjectNode;
	}
	
	public NewLoanApplicationResponseBackend prepareNewLoanDetailsDBResponse(@Body JsonNode body, Exchange ex) throws Exception{
		
		JsonNode newLoanApplicationStatusNode = body.get("NewLoanApplicationStatus");
		
		NewLoanApplicationResponseBackend oNewLoanApplicationResponseBackend = new NewLoanApplicationResponseBackend();
		
		NewLoanApplicationResponse oNewLoanApplicationResponse = new NewLoanApplicationResponse();
		
		LoanAccountDetails oLoanAccountDetails = new LoanAccountDetails();
		
		
		oNewLoanApplicationResponse.setStatus(newLoanApplicationStatusNode.path("status").asText());
		oLoanAccountDetails.setAccountNumber(newLoanApplicationStatusNode.path("accountNumber").asInt());
		oLoanAccountDetails.setInterestRate(newLoanApplicationStatusNode.path("interestRate").asInt());
		oLoanAccountDetails.setLoanAmount(newLoanApplicationStatusNode.path("loanAmount").asInt());
		
		oNewLoanApplicationResponseBackend.setNewLoanApplicationResponse(oNewLoanApplicationResponse);
		oNewLoanApplicationResponse.setLoanAccountDetails(oLoanAccountDetails);
		
		return oNewLoanApplicationResponseBackend;
		
	}

}
