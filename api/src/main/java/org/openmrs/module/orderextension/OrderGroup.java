/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.orderextension;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.OpenmrsObject;

/**
 * This represents a group of orders. Generally, orders may be placed within groups to assist with
 * subsequent management or reporting of the orders (e.g., a drug regimen of three drugs may be
 * placed within an order group).
 */
public class OrderGroup extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The primary key id for this OrderGroup when saved
	 */
	private Integer id;
	
	/**
	 * Optional link to the OrderSet that the user chose to construct this OrderGroup
	 */
	private OrderSet orderSet;
	
	/**
	 * Optional cycleNumber.  If this OrderGroup references a cyclical OrderSet, this indicates which cycle it is
	 */
	private Integer cycleNumber;
	
	/**
	 * Any Orders contained within this OrderGroup
	 */
	private Set<GroupableOrder<?>> members;
	
	/**
	 * @return the earliest start date of all of the Orders in the OrderGroup
	 */
	public Date getEarliestStartDate() {
		Date d = null;
		for (GroupableOrder<?> o : members) {
			Date memberStartDate = o.getOrder().getStartDate();
			if (d == null || d.after(memberStartDate)) {
				d = memberStartDate;
			}
		}
		return d;
	}

	/**
	 * @see OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * @see OpenmrsObject#setId(Integer)
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the orderSet
	 */
	public OrderSet getOrderSet() {
		return orderSet;
	}

	/**
	 * @param orderSet the orderSet to set
	 */
	public void setOrderSet(OrderSet orderSet) {
		this.orderSet = orderSet;
	}
	
	/**
	 * @return the cycleNumber
	 */
	public Integer getCycleNumber() {
		return cycleNumber;
	}

	/**
	 * @param cycleNumber the cycleNumber to set
	 */
	public void setCycleNumber(Integer cycleNumber) {
		this.cycleNumber = cycleNumber;
	}

	/**
	 * @param member the member to add to the group
	 */
	public void addMember(GroupableOrder<?> member) {
		getMembers().add(member);
	}

	/**
	 * @return the members
	 */
	public Set<GroupableOrder<?>> getMembers() {
		if (members == null) {
			members = new HashSet<GroupableOrder<?>>();
		}
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(Set<GroupableOrder<?>> members) {
		this.members = members;
	}
}
