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
import java.util.List;

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
