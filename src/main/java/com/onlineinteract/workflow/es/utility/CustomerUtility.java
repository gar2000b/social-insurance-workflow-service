package com.onlineinteract.workflow.es.utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.onlineinteract.workflow.es.domain.Customer;
import com.onlineinteract.workflow.es.events.CustomerCreatedEvent;
import com.onlineinteract.workflow.es.events.CustomerUpdatedEvent;
import com.onlineinteract.workflow.es.events.Event;
import com.onlineinteract.workflow.es.repository.EventStoreRepository;

public class CustomerUtility {

	public static Customer recreateCustomerState(EventStoreRepository eventStoreRepository, String customerId) {
		List<Event> events = eventStoreRepository.getEvents(customerId);

		/**
		 * Note: we can use the following pattern where we simply take the latest event,
		 * as our implementation is using super course-grained events (where we are
		 * basically storing a snapshot of the whole customer state each update for demo
		 * purposes - possibly beyond, cause disk space is cheap right?).
		 * 
		 * ^ this is the tricky part for some - how granular should our events be? At
		 * the end of the day, both will accomplish the same job of capturing all
		 * temporal data changes but:
		 * 
		 * a) possibly more disk space is needed - but this probably isn't that big a
		 * deal.
		 * 
		 * b) Would require a fair bit of processing elsewhere to figure out what the
		 * actual updates were if we were interested in those during analytics/audits.
		 * 
		 * It's absolutely a trade-off.
		 */
		Event event = events.get(events.size() - 1);
		if (event instanceof CustomerCreatedEvent)
			return ((CustomerCreatedEvent) event).getCustomer();
		if (event instanceof CustomerUpdatedEvent)
			return ((CustomerUpdatedEvent) event).getCustomer();

		/**
		 * Note: use the following pattern if you are re-constructing your domain model
		 * from more fine-grained events.
		 */

//		Customer customer = null;
//		for (Event event : events) {
//			if (event instanceof CustomerCreatedEvent) {
//				CustomerCreatedEvent e = (CustomerCreatedEvent) event;
//				customer = e.getCustomer();
//			}
//			// ... more reconstruction for assembling the asset and reference added/removed events (if they existed) }
//		}

		return null;
	}

	public static Map<String, Customer> getAllCustomers(EventStoreRepository eventStoreRepository) {
		Map<String, Customer> customers = new HashMap<>();
		Set<String> eventIds = eventStoreRepository.getEventIds();
		for (String customerId : eventIds) {
			Customer customer = recreateCustomerState(eventStoreRepository, customerId);
			customers.put(customerId, customer);
		}
		return customers;
	}

	public static int getCustomerCount(EventStoreRepository eventStoreRepository) {
		return eventStoreRepository.getEventCount();
	}
}
