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
package org.openmrs.module.orderextension.util;

import java.util.Calendar;
import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.module.orderextension.ExtendedDrugOrder;

/**
 * Defines any utility methods
 */
public class OrderExtensionUtil  {

	/**
	 * @return the passed object formatted as a String, optionally given the passed format
	 */
	public static String format(Object o, String format) {
		if (o != null) {
			if (o instanceof DrugOrder) {
				DrugOrder drugOrder = (DrugOrder)o;
				ExtendedDrugOrder edo = null;
				if (drugOrder instanceof ExtendedDrugOrder) {
					edo = (ExtendedDrugOrder)drugOrder;
				}
				
				if ("route".equals(format)) {
					if (edo != null && edo.getRoute() != null) {
						return format(edo.getRoute(), null);
					}
					if (drugOrder.getDrug() != null) {
						return format(drugOrder.getDrug().getRoute(), null);
					}
					return "";
				}
				
				if ("administrationInstructions".equals(format)) {
					if (edo != null) {
						return edo.getAdministrationInstructions();
					}
					return "";
				}
				
				if (drugOrder.getDrug() != null) {
					return format(drugOrder.getDrug(), null);
				}
				else {
					return format(drugOrder.getConcept(), null);
				}
			}
			else if (o instanceof Concept) {
				Concept c = (Concept)o;
				return c.getDisplayString();
			}
			else if (o instanceof OpenmrsMetadata) {
				OpenmrsMetadata m = (OpenmrsMetadata)o;
				return m.getName();
			}
			
			return o.toString();
		}
		return "";
	}
	
	/**
	 * @return a Date that is the number of days passed in relative to the date passed in
	 */
	public static Date incrementDate(Date d, Integer increment) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, increment);
		return c.getTime();
	}
}
