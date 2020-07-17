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

import org.openmrs.Order;

/**
 * Adds the ability to Group an Order
 */
public class ExtendedOrder extends Order {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Provides a means for Grouping the Order
	 */
	private ExtendedOrderGroup group;

	/**
	 * Default Constructor
	 */
	public ExtendedOrder() {
		super();
	}

	/**
	 * @return the group
	 */
	public ExtendedOrderGroup getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(ExtendedOrderGroup group) {
		this.group = group;
	}
}
