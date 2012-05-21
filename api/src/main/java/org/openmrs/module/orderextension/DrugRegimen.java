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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;

/**
 * This represents a particular type of OrderGroup which contains one or more
 * DrugOrders and a reference to an OrderSet to make up a DrugRegimen
 */
public class DrugRegimen extends OrderGroup implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	public DrugRegimen() {
		super();
	}
	
	/**
	 * Contains the individual DrugOrders within this Regimen
	 */
	private List<ExtendedDrugOrder> members;

	/**
	 * If this regimen is one of many in a cycle, this provides
	 * a means for representing the cycle number
	 */
	private Integer cycleNumber;
	
	//********** INSTANCE METHODS ***************
	
	/**
	 * returns a name for this regimen
	 */
	public String getName() {
		if (getOrderSet() != null && getOrderSet().getName() != null) {
			return getOrderSet().getName();
		}
		return "";
	}
	
	/**
	 * @return whether or not this Regimen is cyclical.  Delegates to the underlying OrderSet
	 */
	public boolean isCyclical() {
		return getOrderSet() != null && getOrderSet().isCyclical();
	}
	
	/**
	 * @return the length in days between the start of one cycle and the start of the next cycle, if applicable
	 */
	public Integer getCycleLengthInDays() {
		if (getOrderSet() != null) {
			return getOrderSet().getCycleLengthInDays();
		}
		return null;
	}
	
	/**
	 * @return a Map from indication Concept to Set<ExtendedDrugOrder> for the drug orders with this indication
	 * these are returned in order of startDate ascending
	 */
	public Map<Concept, List<ExtendedDrugOrder>> getOrdersByIndication() {
		Map<Concept, List<ExtendedDrugOrder>> m = new HashMap<Concept, List<ExtendedDrugOrder>>();
		List<ExtendedDrugOrder> allOrders = new ArrayList<ExtendedDrugOrder>(getMembers());
		Collections.sort(allOrders, new DrugOrderComparator());
		for (ExtendedDrugOrder d : allOrders) {
			List<ExtendedDrugOrder> l = m.get(d.getIndication());
			if (l == null) {
				l = new ArrayList<ExtendedDrugOrder>();
				m.put(d.getIndication(), l);
			}
			l.add(d);
		}
		return m;
	}
	
	/**
	 * @return the startDate for the Drug Order that has the earliest start date among all members
	 */
	public Date getFirstDrugOrderStartDate() {
		Date d = null;
		for (ExtendedDrugOrder drugOrder : getMembers()) {
			if (d == null || d.after(drugOrder.getStartDate())) {
				d = drugOrder.getStartDate();
			}
		}
		return d;
	}
	
	/**
	 * @return the discontinueDate or autoExpireDate ending latest, or null if there are open ended orders
	 */
	public Date getLastDrugOrderEndDate() {
		Date d = null;
		for (ExtendedDrugOrder drugOrder : getMembers()) {
			Date endDate = drugOrder.getDiscontinuedDate();
			if (endDate == null) {
				endDate = drugOrder.getAutoExpireDate();
			}
			if (endDate == null) {
				return null;
			}
			if (d == null || d.before(endDate)) {
				d = endDate;
			}
		}
		return d;
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	//********** PROPERTY ACCESS ***************

	/**
	 * @return the members
	 */
	public List<ExtendedDrugOrder> getMembers() {
		if (members == null) {
			members = new ArrayList<ExtendedDrugOrder>();
		}
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<ExtendedDrugOrder> members) {
		this.members = members;
	}
	
	/**
	 * @param member the member to add
	 */
	public void addMember(ExtendedDrugOrder member) {
		getMembers().add(member);
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
}
