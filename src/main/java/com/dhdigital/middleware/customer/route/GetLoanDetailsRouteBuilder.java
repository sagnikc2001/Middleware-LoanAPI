package com.dhdigital.middleware.customer.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import com.dhdigital.middleware.customer.model.GetLoanDetailsRequestBackend;
import com.dhdigital.middleware.customer.model.GetLoanDetailsResponseBackend;

@Component
public class GetLoanDetailsRouteBuilder extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		restConfiguration()  // Initializing REST Configuration.
		.bindingMode(RestBindingMode.json);
		
		rest("/api/loan")
		.post("v1/GetLoanDetails") // http://localhost:8080/api/loan/v1/GetLoanDetails
		.type(GetLoanDetailsRequestBackend.class)
		.outType(GetLoanDetailsResponseBackend.class)
		.consumes("application/json")
		.produces("application/json")
		.to("direct:GetLoanDetails");
		
		
		onException(Exception.class) 
		.log("inside exception")
		.to("bean:utils?method=onException(${exchange},\"GetLoanDetailsResponse\",\"MW\")")
		.log("Exception"+"${exception}").handled(true)
		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));
		
		from("direct:GetLoanDetails")
		.to("bean:getLoanDetailsService?method=prepareGetLoanDetailsDBRequest") // Converting pojo request to DAO request
		.log("Request DAO Body - ${body}")
		.to("bean:loanDBConnectorImplDao?method=getLoanDetails") // Fetching details from DB and returning as DAO response
		.log("Response DAO Body - ${body}")
		.to("bean:getLoanDetailsService?method=prepareGetLoanDetailsResponse"); // Converting DAO response to pojo response
		
		
//		from("direct:GettLoanDetails")
//		.to("bean:getLoanDetailsService?method=prepareGetLoanDetailsDBRequest")
//		.log("Request DAO Body - ${body}")
//		.choice()
//		.when().jsonpath("$.LoanDetailsRequest[?(@.fullName == null || @.fullName == '' || @.phoneNumber == null || @.phoneNumber == '' || @.emailAddress == null || @.emailAddress == '' || @.accountNumber == null || @.accountNumber == 0)]")
//			.to("bean:utils?method=prepareFaultNodeStr(\"GetLoanDetailsResponse\",\"INCORRECTVALUE\",\"Enter All Fields\",\"\",\"\",\"validationsCust\",${exchange})")
//		.otherwise()	
//			.to("bean:loanDBConnectorImplDao?method=getLoanDetails")
//			.log("Response DAO Body - ${body}")
//			.choice()
//			.when().jsonpath("$.LoanDetails[?(@.accountNumber != null && @.accountNumber != 0 && @.fullName != null && @.fullName != '' && @.location != null && @.location != '' && @.city != null && @.city != '' && @.state != null && @.state != '' && @.pinCode != null && @.pinCode != '' && @.region != null && @.region != '' && @.phoneNumber != null && @.phoneNumber != '' && @.emailAddress != null && @.emailAddress != '' && @.annualSalary != null && @.annualSalary != 0 && @.loanAmount != null && @.loanAmount != 0 && @.loanTerm != null && @.loanTerm != 0 && @.loanType != null && @.loanType != '' && @.creditScore != null && @.creditScore != 0 && @.interestRate != null && @.interestRate != 0 && @.status != null && @.status != '')]")
//				.to("bean:getLoanDetailsService?method=prepareGetLoanDetailsResponse")
//			.otherwise()
//				.to("bean:utils?method=prepareFaultNodeStr(\"GetLoanDetailsResponse\",\"RECORDNOTFOUND\",\"\",\"\",\"\",\"sysOrAppWithoutBkndError\",${exchange})");
	}

}
