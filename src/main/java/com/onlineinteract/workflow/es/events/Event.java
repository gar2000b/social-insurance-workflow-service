package com.onlineinteract.workflow.es.events;

import java.util.Date;

public abstract class Event {

	private String id;
	private String type;
	private final Date created = new Date();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
