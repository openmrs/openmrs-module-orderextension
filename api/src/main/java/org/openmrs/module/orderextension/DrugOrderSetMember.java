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
import org.openmrs.Drug;
import org.openmrs.OrderType;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;

/**
 * This extends the capabilities of the SingleOrderSetMember to provide additional means 
 * for specifying a pre-defined DrugOrder
 */
public class DrugOrderSetMember extends SingleOrderSetMember {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	public DrugOrderSetMember() {}
	
	/**
	 * @see SingleOrderSetMember#getOrderType()
	 */
	@Override
	public OrderType getOrderType() {
		return Context.getOrderService().getOrderType(OpenmrsConstants.ORDERTYPE_DRUG);
	}
	
	/**
	 * This works similarly to the Concept property, in that it will specify
	 * a default value within a List of options if used in conjunction with either
	 * the concept, conceptSet, or conceptClass properties, and will specify a 
	 * fixed, non-change-able value if these other properties are null
	 */
	private Drug drug;

	/**
	 * Provides the ability to specify a default dose
	 */
	private Double dose;
	
	/**
	 * Used in conjunction with the dose property, provides the ability to specify default dose units
	 */
	private String units;
	
	/**
	 * Used to specify the route (eg. PO, IV, etc)
	 */
	private Concept route;
	
	/**
	 * Provides the ability to specify a default frequency (eg. "1/day x 7 days/week")
	 */
	private String frequency;
	
	/**
	 * Also referred to as PRN, if true indicates that the order is "as needed"
	 */
	private boolean asNeeded;
	
	/**
	 * Provides the ability to specify additional instructions on the Drug Order
	 */
	private String additionalInstructions;

	/**
	 * @return the drug
	 */
	public Drug getDrug() {
		return drug;
	}

	/**
	 * @param drug the drug to set
	 */
	public void setDrug(Drug drug) {
		this.drug = drug;
		if (drug != null && drug.getConcept() != null) {
			setConcept(drug.getConcept());
		}
	}

	/**
	 * @return the dose
	 */
	public Double getDose() {
		return dose;
	}

	/**
	 * @param dose the dose to set
	 */
	public void setDose(Double dose) {
		this.dose = dose;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(String units) {
		this.units = units;
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
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the asNeeded
	 */
	public boolean isAsNeeded() {
		return asNeeded;
	}

	/**
	 * @param asNeeded the asNeeded to set
	 */
	public void setAsNeeded(boolean asNeeded) {
		this.asNeeded = asNeeded;
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
