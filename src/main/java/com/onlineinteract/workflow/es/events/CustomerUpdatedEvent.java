package com.onlineinteract.workflow.es.events;

import com.onlineinteract.workflow.es.domain.Customer;

public class CustomerUpdatedEvent extends Event {
	private Customer customer;

	public CustomerUpdatedEvent() {
	}

	public CustomerUpdatedEvent(Customer customer) {
		this.customer = customer;
		this.setType("CustomerUpdatedEvent");
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerUpdatedEvent other = (CustomerUpdatedEvent) obj;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		return true;
	}
}
