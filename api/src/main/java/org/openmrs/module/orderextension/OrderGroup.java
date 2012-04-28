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

import org.openmrs.BaseOpenmrsData;
import org.openmrs.OpenmrsObject;

/**
 * This represents a group of orders. Generally, orders may be placed within groups to assist with
 * subsequent management or reporting of the orders (e.g., a drug regimen of three drugs may be
 * placed within an order group).
 */
public class OrderGroup extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The primary key id for this OrderGroup when saved
	 */
	private Integer id;
	
	// TBD: Fill in the rest of the details...

	/**
	 * @see OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * @see OpenmrsObject#setId(Integer)
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	


}
