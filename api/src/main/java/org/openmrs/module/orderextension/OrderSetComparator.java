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

import java.util.Comparator;

import org.openmrs.OrderSet;

/**
 * Compares two Order Sets based on indication, then name
 */
public class OrderSetComparator implements Comparator<OrderSet> {

	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(OrderSet r1, OrderSet r2) {
        String s1 = (r1.getCategory() == null ? "ZZZZZZ" : r1.getCategory().getDisplayString()) + " - " + r1.getName();
		String s2 = (r2.getCategory() == null ? "ZZZZZZ" : r2.getCategory().getDisplayString()) + " - " + r2.getName();
		return s1.compareTo(s2);
     }
}
