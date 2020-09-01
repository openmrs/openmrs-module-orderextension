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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;

/**
 * Extends the core OrderSet class by adding explicit fields for cycles
 */
public class ExtendedOrderSet extends OrderSet implements java.io.Serializable {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * If true, indicates that this CyclicalOrderSet is cyclical, and when Ordered, may be repeated
	 */
	private boolean cyclical;
	
	/**
	 * If cyclical, indicates the length of time between the start of one cycle and the start of the next cyle
	 */
	private Integer cycleLengthInDays;
	
	/**
	 * Default Constructor
	 */
	public ExtendedOrderSet() { }

	/**
	 * @return the indication
	 */
	public Concept getIndication() {
		return getCategory();
	}

	/**
	 * @param indication the indication to set
	 */
	public void setIndication(Concept indication) {
		setCategory(indication);
	}

	/**
	 * @return the cyclical
	 */
	public boolean isCyclical() {
		return cyclical;
	}

	/**
	 * @param cyclical the cyclical to set
	 */
	public void setCyclical(boolean cyclical) {
		this.cyclical = cyclical;
	}

	/**
	 * @return the cycleLengthInDays
	 */
	public Integer getCycleLengthInDays() {
		return cycleLengthInDays;
	}

	/**
	 * @param cycleLengthInDays the cycleLengthInDays to set
	 */
	public void setCycleLengthInDays(Integer cycleLengthInDays) {
		this.cycleLengthInDays = cycleLengthInDays;
	}

}
