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
import java.util.HashSet;
import java.util.Set;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
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
	 * Optional indication for this overall OrderGroup
	 */
	private Concept indication;
	
	/**
	 * If this OrderGroup is nested, this links to the parent
	 */
	private OrderGroup parentOrderGroup;
	
	/**
	 * Any OrderGroups that this OrderGroup contains
	 */
	private Set<OrderGroup> nestedOrderGroups;
	
	/**
	 * Any Orders contained within this OrderGroup
	 */
	private Set<ExtendedOrder> members;

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
	 * @return the indication
	 */
	public Concept getIndication() {
		return indication;
	}

	/**
	 * @param indication the indication to set
	 */
	public void setIndication(Concept indication) {
		this.indication = indication;
	}

	/**
	 * @return the parentOrderGroup
	 */
	public OrderGroup getParentOrderGroup() {
		return parentOrderGroup;
	}

	/**
	 * @param parentOrderGroup the parentOrderGroup to set
	 */
	public void setParentOrderGroup(OrderGroup parentOrderGroup) {
		this.parentOrderGroup = parentOrderGroup;
	}

	/**
	 * @return the nestedOrderGroups
	 */
	public Set<OrderGroup> getNestedOrderGroups() {
		if (nestedOrderGroups == null) {
			nestedOrderGroups = new HashSet<OrderGroup>();
		}
		return nestedOrderGroups;
	}

	/**
	 * @param nestedOrderGroups the nestedOrderGroups to set
	 */
	public void setNestedOrderGroups(Set<OrderGroup> nestedOrderGroups) {
		this.nestedOrderGroups = nestedOrderGroups;
	}

	/**
	 * @return the members
	 */
	public Set<ExtendedOrder> getMembers() {
		if (members == null) {
			members = new HashSet<ExtendedOrder>();
		}
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(Set<ExtendedOrder> members) {
		this.members = members;
	}
}
