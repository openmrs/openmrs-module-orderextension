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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderGroup;
import org.openmrs.module.orderextension.util.OrderExtensionUtil;

/**
 * This represents a particular type of OrderGroup which contains one or more
 * DrugOrders and a reference to an ExtendedOrderSet to make up a DrugRegimen
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
	 * @return whether or not this Regimen is cyclical.  Delegates to the underlying ExtendedOrderSet
	 */
	public boolean isCyclical() {
		return OrderExtensionUtil.isCyclical(getOrderSet());
	}

	public List<DrugOrder> getMembers() {
		List<DrugOrder> ret = new ArrayList<DrugOrder>();
		for (Order o : OrderExtensionUtil.getOrdersInGroup(this)) {
			ret.add((DrugOrder)o);
		}
		return ret;
	}
	
	/**
	 * @return the length in days between the start of one cycle and the start of the next cycle, if applicable
	 */
	public Integer getCycleLengthInDays() {
		return OrderExtensionUtil.getCycleLengthInDays(getOrderSet());
	}
	
	/**
	 * @return the startDate for the Drug Order that has the earliest start date among all members
	 */
	public Date getFirstDrugOrderStartDate() {
		return OrderExtensionUtil.getFirstDrugOrderStartDate(this);
	}
	
	/**
	 * @return the discontinueDate or autoExpireDate ending latest, or null if there are open ended orders
	 */
	public Date getLastDrugOrderEndDate() {
		return OrderExtensionUtil.getLastDrugOrderEndDate(this);
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		String s = getName();
		if (StringUtils.isNotBlank(s)) {
			return s;
		}
		return super.toString();
	}

	//********** PROPERTY ACCESS ***************

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
