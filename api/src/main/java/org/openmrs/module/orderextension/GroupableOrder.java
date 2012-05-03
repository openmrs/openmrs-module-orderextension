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
import org.openmrs.OpenmrsData;

/**
 * Provides an interface Orders that can be associated with Order Groups
 */
public interface GroupableOrder extends OpenmrsData {
	
	/**
	 * @return the reason for this Order
	 */
	public Concept getIndication();
	
	/**
	 * @param indication  the reason for this Order
	 */
	public void setIndication(Concept indication);
	
	/**
	 * @return the group for this Order
	 */
	public OrderGroup getGroup();
	
	/**
	 * @param group the group for this Order
	 */
	public void setGroup(OrderGroup group);
}
