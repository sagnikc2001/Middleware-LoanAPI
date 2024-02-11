package com.dhdigital.middleware.customer.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.internal.OracleTypes;

@Component
public class LoanDBConnectorImplDao {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private DataSource dataSource;

	public void insertNewLoanDetails(@Body JsonNode body, Exchange ex) throws Exception {

		JsonNode newLoanApplicationDetailsNode = body.get("NewLoanApplicationDetails");

		Connection conn = null;

		OracleCallableStatement ocStatement = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String strProcedure = "CALL NEW_LOAN_DETAILS(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			ocStatement = (OracleCallableStatement) conn.prepareCall(strProcedure);

			ocStatement.setString(1, newLoanApplicationDetailsNode.path("fullName").asText());
			ocStatement.setString(2, newLoanApplicationDetailsNode.path("location").asText());
			ocStatement.setString(3, newLoanApplicationDetailsNode.path("city").asText());
			ocStatement.setString(4, newLoanApplicationDetailsNode.path("state").asText());
			ocStatement.setString(5, newLoanApplicationDetailsNode.path("pinCode").asText());
			ocStatement.setString(6, newLoanApplicationDetailsNode.path("region").asText());
			ocStatement.setString(7, newLoanApplicationDetailsNode.path("phoneNumber").asText());
			ocStatement.setString(8, newLoanApplicationDetailsNode.path("emailAddress").asText());
			ocStatement.setInt(9, newLoanApplicationDetailsNode.path("annualSalary").asInt());
			ocStatement.setInt(10, newLoanApplicationDetailsNode.path("loanAmount").asInt());
			ocStatement.setInt(11, newLoanApplicationDetailsNode.path("loanTerm").asInt());
			ocStatement.setString(12, newLoanApplicationDetailsNode.path("loanType").asText());
			ocStatement.setInt(13, newLoanApplicationDetailsNode.path("creditScore").asInt());
			ocStatement.setDouble(14, newLoanApplicationDetailsNode.path("interestRate").asDouble());
			ocStatement.setString(15, newLoanApplicationDetailsNode.path("status").asText());

			ocStatement.execute();

		} catch (Exception e) {
			e.printStackTrace();
			ex.getIn().setBody(e.getMessage());
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}
				if (ocStatement != null) {
					ocStatement.close();
				}

				if (null != conn)
					conn.close();

			} catch (SQLException e) {
				e.printStackTrace();
				ex.getIn().setBody(e.getMessage());
			}
		}
	}

	public JsonNode getLoanApplicationStatus(@Body JsonNode body, Exchange ex) throws Exception {

		JsonNode newLoanApplicationDetailsNode = body.get("NewLoanApplicationDetails");

		Connection conn = null;

		OracleCallableStatement ocStatement = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String strProcedure = "CALL GET_LOAN_APPLICATION_STATUS(?,?,?,?,?,?)";

			ocStatement = (OracleCallableStatement) conn.prepareCall(strProcedure);

			ocStatement.setString(1, newLoanApplicationDetailsNode.path("phoneNumber").asText());
			ocStatement.setString(2, newLoanApplicationDetailsNode.path("emailAddress").asText());
			ocStatement.setInt(3, newLoanApplicationDetailsNode.path("loanAmount").asInt());
			ocStatement.setInt(4, newLoanApplicationDetailsNode.path("loanTerm").asInt());
			ocStatement.setString(5, newLoanApplicationDetailsNode.path("loanType").asText());
			ocStatement.registerOutParameter(6, OracleTypes.CURSOR);

			ocStatement.execute();

			// Creating response object
			ObjectNode ogetLoanApplicationStatusObjectNode = JsonNodeFactory.instance.objectNode();
			ObjectNode ogetLoanApplicationStatus = ogetLoanApplicationStatusObjectNode
					.putObject("NewLoanApplicationStatus");

			rs = (ResultSet) ocStatement.getObject(6);

			while (rs.next()) {

				ogetLoanApplicationStatus.put("accountNumber", rs.getInt(1));
				ogetLoanApplicationStatus.put("loanAmount", rs.getInt(11));
				ogetLoanApplicationStatus.put("interestRate", rs.getInt(15));
				ogetLoanApplicationStatus.put("status", rs.getString(16));

			}

			return ogetLoanApplicationStatusObjectNode;

		} catch (Exception e) {
			e.printStackTrace();
			ex.getIn().setBody(e.getMessage());
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}
				if (ocStatement != null) {
					ocStatement.close();
				}

				if (null != conn)
					conn.close();

			} catch (SQLException e) {
				e.printStackTrace();
				ex.getIn().setBody(e.getMessage());
			}
		}
		return null;

	}

	public JsonNode getLoanDetails(@Body JsonNode body, Exchange ex) throws Exception {

		JsonNode oLoanDetailsRequest = body.get("LoanDetailsRequest");

		Connection conn = null;

		OracleCallableStatement ocStatement = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String strProcedure = "CALL GET_LOAN_DETAILS(?,?,?,?,?)";
			
			ocStatement = (OracleCallableStatement) conn.prepareCall(strProcedure);
			
			ocStatement.setString(1, oLoanDetailsRequest.path("fullName").asText());
	        ocStatement.setString(2, oLoanDetailsRequest.path("phoneNumber").asText());
	        ocStatement.setString(3, oLoanDetailsRequest.path("emailAddress").asText());
	        ocStatement.setInt(4, oLoanDetailsRequest.path("accountNumber").asInt());
	        ocStatement.registerOutParameter(5, OracleTypes.CURSOR);
	        
	        ocStatement.execute();
	        
	     // Creating response object
	        ObjectNode ogetLoanDetailsObjectNode = JsonNodeFactory.instance.objectNode();
	        ObjectNode ogetLoanDetails = ogetLoanDetailsObjectNode.putObject("LoanDetails");
	        
	        rs = (ResultSet) ocStatement.getObject(5);
	        
	        while(rs.next()) {
	        	
	        	ogetLoanDetails.put("accountNumber", rs.getInt(1));
	        	ogetLoanDetails.put("fullName", rs.getString(2));
	        	ogetLoanDetails.put("location", rs.getString(3));
	        	ogetLoanDetails.put("city", rs.getString(4));
	        	ogetLoanDetails.put("state", rs.getString(5));
	        	ogetLoanDetails.put("pinCode", rs.getString(6));
	        	ogetLoanDetails.put("region", rs.getString(7));
	        	ogetLoanDetails.put("phoneNumber", rs.getString(8));
	        	ogetLoanDetails.put("emailAddress", rs.getString(9));
	        	ogetLoanDetails.put("annualSalary", rs.getInt(10));
	        	ogetLoanDetails.put("loanAmount", rs.getInt(11));
	        	ogetLoanDetails.put("loanTerm", rs.getInt(12));
	        	ogetLoanDetails.put("loanType", rs.getString(13));
	        	ogetLoanDetails.put("creditScore", rs.getInt(14));
	        	ogetLoanDetails.put("interestRate", rs.getInt(15));
	        	ogetLoanDetails.put("status", rs.getString(16));
	        }
	        
	        return ogetLoanDetailsObjectNode;

		} catch (Exception e) {
			e.printStackTrace();
			ex.getIn().setBody(e.getMessage());
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}
				if (ocStatement != null) {
					ocStatement.close();
				}

				if (null != conn)
					conn.close();

			} catch (SQLException e) {
				e.printStackTrace();
				ex.getIn().setBody(e.getMessage());
			}
		}
		return null;
	}
}
