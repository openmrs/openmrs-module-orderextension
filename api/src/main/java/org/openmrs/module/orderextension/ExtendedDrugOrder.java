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

import org.openmrs.Concept;
import org.openmrs.DrugOrder;

/**
 * This extends Order and adds GroupableOrder properties,
 * including optional links to an OrderGroup and an Indication
 */
public class ExtendedDrugOrder extends DrugOrder implements GroupableOrder {

	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor
	 */
	public ExtendedDrugOrder() {
		super();
	}
	
	/**
	 * Provides a means to record the reason for this particular Order
	 */
	private Concept indication;
	
	/**
	 * The associated OrderGroup
	 */
	private OrderGroup group;
	
	/**
	 * Provides the means to specify the route of drug administration (eg. IV, IM, PO)
	 */
	private Concept route;
	
	/**
	 * Provides an additional instructions field
	 */
	private String additionalInstructions;
	
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
	 * @return the route
	 */
	public Concept getRoute() {
		return route;
	}

	/**
	 * @param route the route to set
	 */
	public void setRoute(Concept route) {
		this.route = route;
	}

	/**
	 * @return the additionalInstructions
	 */
	public String getAdditionalInstructions() {
		return additionalInstructions;
	}

	/**
	 * @param additionalInstructions the additionalInstructions to set
	 */
	public void setAdditionalInstructions(String additionalInstructions) {
		this.additionalInstructions = additionalInstructions;
	}
}
