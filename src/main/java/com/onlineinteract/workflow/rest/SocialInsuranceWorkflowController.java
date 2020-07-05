package com.onlineinteract.workflow.rest;

import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.onlineinteract.workflow.es.domain.Customer;
import com.onlineinteract.workflow.es.repository.EventStoreRepository;
import com.onlineinteract.workflow.es.utility.CustomerUtility;
import com.onlineinteract.workflow.utility.JsonParser;

@Controller
@EnableAutoConfiguration
public class SocialInsuranceWorkflowController {

	@Autowired
	EventStoreRepository eventStoreRepository;

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "text/plain", value = "/verifySin")
	@ResponseBody
	public ResponseEntity<String> verifySin(@RequestBody Map<String, String> customer,
			@RequestHeader HttpHeaders incomingHeaders) {
		System.out.println("\nVerifying SIN with customer ID: " + customer.get("CustomerId"));

		/**
		 * Fetch Customer
		 */
		Customer customerState = CustomerUtility.recreateCustomerState(eventStoreRepository,
				customer.get("CustomerId"));
		String customerJson = JsonParser.toJson(customerState);
		System.out.println("customer state recreated from events: " + customerJson);

		/**
		 * Fraud Check
		 */
		String fraudCheckServiceUrl = "http://fraud-check:9083/fraudCheck";

		System.out.println("Headers: ");
		Iterator it = incomingHeaders.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
		}

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.addAll(incomingHeaders);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<String>(customerJson, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(fraudCheckServiceUrl, request, String.class);
		System.out.println("Response from Fraud Check Service: " + response.getBody());
		if (response.getStatusCode() != HttpStatus.OK)
			return new ResponseEntity<>("Sorry, there was a problem with fraud check", response.getStatusCode());

		/**
		 * Social Insurance Verification
		 */
		customer.put("sin", customerState.getSin());
		String socialInsuranceVerificationUrl = "http://social-insurance-verification:9085/verifySin";
		HttpEntity<Map<String, String>> sinVerificationRequest = new HttpEntity<Map<String, String>>(customer, headers);
		response = restTemplate.postForEntity(socialInsuranceVerificationUrl, sinVerificationRequest, String.class);
		System.out.println("Response from Social Insurance Verification Service: " + response.getBody());
		if (response.getStatusCode() != HttpStatus.OK)
			return new ResponseEntity<>("Sorry, there was a problem with Social Insurance Verification",
					response.getStatusCode());

		return new ResponseEntity<>("SIN Verified for customer: " + customer.get("CustomerId"), HttpStatus.OK);
	}
}
