package com.dhdigital.middleware.customer.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import com.dhdigital.middleware.customer.model.NewLoanApplicationRequestBackend;
import com.dhdigital.middleware.customer.model.NewLoanApplicationResponseBackend;


@Component
public class NewLoanApplicationRouteBuilder extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		restConfiguration()  // Initializing REST Configuration.
		.bindingMode(RestBindingMode.json);
	
		rest("/api/loan")
		.post("v1/NewLoanApplication") // http://localhost:8080/api/loan/v1/NewLoanApplication
		.type(NewLoanApplicationRequestBackend.class)
		.outType(NewLoanApplicationResponseBackend.class)
		.consumes("application/json")
		.produces("application/json")
		.to("direct:newLoanApplication");
		
		
		onException(Exception.class) 
		.log("inside exception")
		.to("bean:utils?method=onException(${exchange},\"NewLoanApplicationResponse\",\"MW\")")
		.log("Exception"+"${exception}").handled(true)
		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));
		
		
		
		from("direct:newLoanApplication")
		.to("bean:newLoanApplicationService?method=setNewLoanApplicationRequestBackendIn") // Saving request locally
		.to("bean:newLoanApplicationService?method=getLoanEligibilityAndInterestRate") // Evaluating whether loan can be sanctioned and interest rate according to request details
		.to("bean:newLoanApplicationService?method=prepareNewLoanDetailsDBRequest") // Converting pojo request to DAO request
		.log("Request DAO Body - ${body}")
		.to("bean:loanDBConnectorImplDao?method=insertNewLoanDetails") // Storing Loan application details in DB
		.to("bean:loanDBConnectorImplDao?method=getLoanApplicationStatus") // Getting Account Number and Loan status from DB
		.log("Response DAO Body - ${body}")
		.to("bean:newLoanApplicationService?method=prepareNewLoanDetailsDBResponse"); // Converting DAO response to pojo response
		
		
		
		
//		from("direct:newLoanApplication")
//		.to("bean:newLoanApplicationService?method=setNewLoanApplicationRequestBackendIn")
//		.to("bean:newLoanApplicationService?method=getLoanEligibilityAndInterestRate")
//		.to("bean:newLoanApplicationService?method=prepareNewLoanDetailsDBRequest")
//		.log("Request DAO Body - ${body}")
//		.choice()
//		.when().jsonpath("$.NewLoanApplicationDetails[?(@.fullName == null || @.fullName == '' || @.location == null || @.location == '' || @.city == null || @.city == '' || @.state == null || @.state == '' || @.pinCode == null || @.pinCode == '' || @.region == null || @.region == '' || @.phoneNumber == null || @.phoneNumber == '' || @.emailAddress == null || @.emailAddress == '' || @.annualSalary == null || @.annualSalary == 0 || @.loanAmount == null || @.loanAmount == 0 || @.loanTerm == null || @.loanTerm == 0 || @.loanType == null || @.loanType == '' || @.creditScore == null || @.creditScore == 0 || @.interestRate == null || @.interestRate == 0 || @.status == null || @.status == '')]")
//			.to("bean:utils?method=prepareFaultNodeStr(\"NewLoanApplicationResponse\",\"INCORRECTVALUE\",\"Enter All Fields\",\"\",\"\",\"validationsCust\",${exchange})")
//		.otherwise()
//			.to("bean:loanDBConnectorImplDao?method=insertNewLoanDetails")
//			.to("bean:loanDBConnectorImplDao?method=getLoanApplicationStatus")
//			.log("Response DAO Body - ${body}")
//			.choice()
//			.when().jsonpath("$.NewLoanApplicationStatus[?(@.accountNumber != null && @.accountNumber != '' &&  @.loanAmount != null &&  @.loanAmount != 0 && @.interestRate != null && @.interestRate != 0 && @.status != null && @.status != '')]")
//				.to("bean:newLoanApplicationService?method=prepareNewLoanDetailsDBResponse")
//			.otherwise()
//				.to("bean:utils?method=prepareFaultNodeStr(\"NewLoanApplicationResponse\",\"RECORDNOTFOUND\",\"\",\"\",\"\",\"sysOrAppWithoutBkndError\",${exchange})");
	}

}
