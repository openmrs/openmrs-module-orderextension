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


/**
 * Provides the capability to combine Order Sets together, and to
 * re-use common order set components across multiple Order Sets
 */
public class NestedOrderSetMember extends OrderSetMember {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	private NestedOrderSetMember() {}
	
	/**
	 * Provides a means to re-use an Order Set within multiple Order Sets
	 */
	private OrderSet nestedOrderSet;

	/**
	 * @return the nestedOrderSet
	 */
	public OrderSet getNestedOrderSet() {
		return nestedOrderSet;
	}

	/**
	 * @param nestedOrderSet the nestedOrderSet to set
	 */
	public void setNestedOrderSet(OrderSet nestedOrderSet) {
		this.nestedOrderSet = nestedOrderSet;
	}
}
