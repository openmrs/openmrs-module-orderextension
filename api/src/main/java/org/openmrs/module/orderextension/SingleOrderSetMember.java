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
import org.openmrs.OrderType;

/**
 * This represents a single pre-defined Order with a particular OrderType
 * There are 3 complementary properties, which can work together or separately as needed.
 * If concept is specified, this represents the default Concept for the Order
 * If both conceptSet and conceptClass are null, then no further Concept selection would be allowed
 * If either conceptSet or conceptClass are specified, these will provide the range of Concepts
 * that are allowed for this particular Order, with the concept property providing the default value
 */
public abstract class SingleOrderSetMember extends OrderSetMember implements java.io.Serializable {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	public SingleOrderSetMember() {}
	
	/**
	 * @return the OrderType for the Order that this member represents
	 */
	public abstract OrderType getOrderType();
	
	/**
	 * Provides a means for specifying a specific order-able Concept that should be ordered
	 */
	private Concept concept;
	
	/**
	 * If specified, this indicates the reason for this particular order set member
	 */
	private Concept indication;
	
	/**
	 * If specified, provides a means to specify instructions on the Order
	 */
	private String instructions;

	/**
	 * @return the concept
	 */
	public Concept getConcept() {
		return concept;
	}

	/**
	 * @param concept the concept to set
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
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
	 * @return the instructions
	 */
	public String getInstructions() {
		return instructions;
	}

	/**
	 * @param instructions the instructions to set
	 */
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
}
