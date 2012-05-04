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

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Order;

/**
 * This extends Order and adds GroupableOrder properties,
 * including optional links to an OrderGroup and an Indication
 */
public abstract class GroupableOrder<T extends Order> extends BaseOpenmrsData {

	/**
	 * Default Constructor
	 */
	public GroupableOrder() {
		super();
	}
	
	/**
	 * Primary key id
	 */
	private Integer id;
	
	/**
	 * The underlying Order to extend
	 */
	private T order;

	/**
	 * Provides a means to record the reason for this particular Order
	 */
	private Concept indication;
	
	/**
	 * The associated OrderGroup
	 */
	private OrderGroup group;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the order
	 */
	public T getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(T order) {
		this.order = order;
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
	 * @return the group
	 */
	public OrderGroup getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(OrderGroup group) {
		this.group = group;
	}
}
