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
 * Implementation of GroupableOrder that wraps a DrugOrder add adds new properties
 */
public class ExtendedDrugOrder extends GroupableOrder<DrugOrder> {

	/**
	 * Default Constructor
	 */
	public ExtendedDrugOrder() {
		super();
		setOrder(new DrugOrder());
	}
	
	/**
	 * Constructor for wrapping a DrugOrder in an ExtendedDrugOrder
	 */
	public ExtendedDrugOrder(DrugOrder order) {
		this();
		setOrder(order);
	}
	
	/**
	 * Provides the means to specify the route of drug administration (eg. IV, IM, PO)
	 */
	private Concept route;
	
	/**
	 * Provides an additional instructions field
	 */
	private String additionalInstructions;

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
