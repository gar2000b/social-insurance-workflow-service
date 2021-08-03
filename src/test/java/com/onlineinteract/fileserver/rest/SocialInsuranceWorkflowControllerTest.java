package com.onlineinteract.fileserver.rest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SocialInsuranceWorkflowControllerTest {

	private static final String CUSTOMER_JSON = "{\"Forename\":\"Alex\",\"DOB\":\"22/04/76\",\"Address2\":\"Ingles\",\"SIN\":\"123-456-789\",\"CustomerId\":\"1\",\"Address1\":\"142 Potter Way\",\"City\":\"Surrey\",\"Surname\":\"Beaton\",\"Postcode\":\"SU12 3HG\",\"AccountNumber\":\"12345678\"}";
	private static SocialInsuranceWorkflowController socialInsuranceWorkflowController;
	private static ObjectMapper mapper;

	@BeforeClass
	public static void setup() {
		socialInsuranceWorkflowController = new SocialInsuranceWorkflowController();
		mapper = new ObjectMapper();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void logCustomerTest() throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> customer = mapper.readValue(CUSTOMER_JSON, Map.class);
		Integer result = socialInsuranceWorkflowController.logCustomer(customer);
		assertEquals(Integer.valueOf(1), result);
	}

	@Test
	public void logCustomerFailTest() throws JsonParseException, JsonMappingException, IOException {
		Map<String, String> customer = null;
		Integer result = socialInsuranceWorkflowController.logCustomer(customer);
		assertEquals(Integer.valueOf(-1), result);
	}
	
	@Test
	public void logFraudCheckTest() {
		boolean result = socialInsuranceWorkflowController.logFraudCheck("Customer checks out OK", 1);
		assertEquals(true, result);
	}
	
	@Test
	public void logFraudCheckFailTest() {
		boolean result = socialInsuranceWorkflowController.logFraudCheck("Customer noes not check out OK", 1);
		assertEquals(false, result);
	}
	
	@Test
	public void logVerificationTest() {
		Integer result = socialInsuranceWorkflowController.logVerification("SIN Verified for customer: 1");
		assertEquals(Integer.valueOf(1), result);
	}
	
	@Test
	public void logVerificationFailTest() {
		Integer result = socialInsuranceWorkflowController.logVerification("SIN Verified for customer 1");
		assertEquals(Integer.valueOf(-1), result);
	}

}
