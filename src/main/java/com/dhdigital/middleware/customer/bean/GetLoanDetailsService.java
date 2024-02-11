package com.dhdigital.middleware.customer.bean;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dhdigital.middleware.customer.model.AddressInfo;
import com.dhdigital.middleware.customer.model.GetLoanDetailsRequestBackend;
import com.dhdigital.middleware.customer.model.GetLoanDetailsResponse;
import com.dhdigital.middleware.customer.model.GetLoanDetailsResponseBackend;
import com.dhdigital.middleware.customer.model.LoanInfo;
import com.dhdigital.middleware.customer.model.PersonalInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class GetLoanDetailsService {
	
	@Autowired
	ObjectMapper objectMapper;
	
	public JsonNode prepareGetLoanDetailsDBRequest(GetLoanDetailsRequestBackend oGetLoanDetailsRequestBackend, Exchange ex) throws Exception{
		
		ObjectNode oLoanDetailsRequestObjectNode = JsonNodeFactory.instance.objectNode();
		ObjectNode oLoanDetailsRequest = oLoanDetailsRequestObjectNode.putObject("LoanDetailsRequest");
		
		oLoanDetailsRequest.put("fullName", oGetLoanDetailsRequestBackend.getGetLoanDetailsRequest().getCustomerInfo().getFullname());
		oLoanDetailsRequest.put("phoneNumber", oGetLoanDetailsRequestBackend.getGetLoanDetailsRequest().getCustomerInfo().getPhoneNumber());
		oLoanDetailsRequest.put("emailAddress", oGetLoanDetailsRequestBackend.getGetLoanDetailsRequest().getCustomerInfo().getEmailAddress());
		oLoanDetailsRequest.put("accountNumber", oGetLoanDetailsRequestBackend.getGetLoanDetailsRequest().getLoanAccountInfo().getAccountNumber());
		
		
		return oLoanDetailsRequestObjectNode;
	}
	
	public GetLoanDetailsResponseBackend prepareGetLoanDetailsResponse(@Body JsonNode body, Exchange ex) throws Exception{
		
		JsonNode loanDetails = body.get("LoanDetails");
		
		GetLoanDetailsResponseBackend oGetLoanDetailsResponseBackend = new GetLoanDetailsResponseBackend();
		GetLoanDetailsResponse oGetLoanDetailsResponse = new GetLoanDetailsResponse();
		PersonalInfo oPersonalInfo = new PersonalInfo();
		AddressInfo oAddressInfo = new AddressInfo();
		LoanInfo oLoanInfo = new LoanInfo();
		
		oPersonalInfo.setFullName(loanDetails.path("fullName").asText());
		oPersonalInfo.setPhoneNumber(loanDetails.path("phoneNumber").asText());
		oPersonalInfo.setEmailAddress(loanDetails.path("emailAddress").asText());
		oPersonalInfo.setAnnualSalary(loanDetails.path("annualSalary").asInt());
		oAddressInfo.setLocation(loanDetails.path("location").asText());
		oAddressInfo.setCity(loanDetails.path("city").asText());
		oAddressInfo.setState(loanDetails.path("state").asText());
		oAddressInfo.setPinCode(loanDetails.path("pinCode").asText());
		oAddressInfo.setRegion(loanDetails.path("region").asText());
		oLoanInfo.setAccountNumber(loanDetails.path("accountNumber").asInt());
		oLoanInfo.setStatus(loanDetails.path("status").asText());
		oLoanInfo.setInterestRate(loanDetails.path("interestRate").asInt());
		oLoanInfo.setLoanAmount(loanDetails.path("loanAmount").asInt());
		oLoanInfo.setLoanTerm(loanDetails.path("loanTerm").asInt());
		oLoanInfo.setLoanType(loanDetails.path("loanType").asText());
		oLoanInfo.setCreditScore(loanDetails.path("creditScore").asInt());
		
		oPersonalInfo.setAddressInfo(oAddressInfo);
		oGetLoanDetailsResponse.setPersonalInfo(oPersonalInfo);
		oGetLoanDetailsResponse.setLoanInfo(oLoanInfo);
		oGetLoanDetailsResponseBackend.setGetLoanDetailsResponse(oGetLoanDetailsResponse);
		
		return oGetLoanDetailsResponseBackend;
	}

}
