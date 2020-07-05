package com.onlineinteract.workflow.es.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.onlineinteract.workflow.es.events.CustomerCreatedEvent;
import com.onlineinteract.workflow.es.events.CustomerUpdatedEvent;
import com.onlineinteract.workflow.es.events.Event;

@Repository
public class EventStoreRepository {

	private Map<String, List<Event>> store = new HashMap<>();

	public void addEvent(Event event) {
		String id = null;
		if (event instanceof CustomerCreatedEvent)
			id = ((CustomerCreatedEvent) event).getCustomer().getId();
		if (event instanceof CustomerUpdatedEvent)
			id = ((CustomerUpdatedEvent) event).getCustomer().getId();

		List<Event> events = store.get(id);
		if (events == null) {
			events = new ArrayList<Event>();
			events.add(event);
			store.put(id, events);
		} else {
			events.add(event);
		}
	}

	public List<Event> getEvents(String id) {
		return store.get(id);
	}

	public Set<String> getEventIds() {
		return store.keySet();
	}

	public int getEventCount() {
		return store.size();
	}
}
