package com.onlineinteract.fileserver.rest;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@EnableAutoConfiguration
public class SocialInsuranceWorkflowController {

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "text/plain", value = "/verifySin")
	@ResponseBody
	public ResponseEntity<String> verifySin(@RequestBody Map<String, String> customer, @RequestHeader HttpHeaders incomingHeaders) {
		System.out.println("\nVerifying SIN with customer ID: " + customer.get("CustomerId"));

		/**
		 * Fetch Customer
		 */
		String customerServiceUrl = "http://customer:9082/fetchCustomer/" + customer.get("CustomerId");
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.addAll(incomingHeaders);
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> response = restTemplate.getForEntity(customerServiceUrl, String.class, headers);
		String customerJson = response.getBody();
		System.out.println("Response from Customer Service: " + customerJson);
		if (response.getStatusCode() != HttpStatus.OK)
			return new ResponseEntity<>("Sorry, there was a problem fetching customer", response.getStatusCode());

		/**
		 * Fraud Check
		 */
		String fraudCheckServiceUrl = "http://fraud-check:9083/fraudCheck";
		
		System.out.println("Headers: ");
	    Iterator it = headers.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	    }
	    
		HttpEntity<String> request = new HttpEntity<String>(customerJson, headers);
		response = restTemplate.postForEntity(fraudCheckServiceUrl, request, String.class);
		System.out.println("Response from Fraud Check Service: " + response.getBody());
		if (response.getStatusCode() != HttpStatus.OK)
			return new ResponseEntity<>("Sorry, there was a problem with fraud check", response.getStatusCode());

		/**
		 * Social Insurance Verification
		 */
		ObjectMapper mapper = new ObjectMapper();
		try {
			customer = mapper.readValue(customerJson, Map.class);
		} catch (IOException e) {
			return new ResponseEntity<>("Sorry, there was a problem converting customer json to map",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
