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
import java.util.Set;

import org.openmrs.DrugOrder;

/**
 * Sorts Drug Orders by date and then by name
 */
public class DrugOrderComparator implements Comparator<DrugOrder> {

	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(DrugOrder r1, DrugOrder r2) {

		if (r1.getDrug() != null && r1.getDrug().equals(r2.getDrug())) {
			if (r1.getStartDate().compareTo(r2.getStartDate()) < 0) {
				return -1;
			} else if (r1.getStartDate().compareTo(r2.getStartDate()) > 0) {
				return 1;
			}

		} else {
			if (r1.getStartDate().compareTo(r2.getStartDate()) < 0) {
				return -10;
			} else if (r1.getStartDate().compareTo(r2.getStartDate()) > 0) {
				return 10;
			} else if (r1.getDrug() != null && r2.getDrug() != null) {

				ExtendedDrugOrder r11 = (ExtendedDrugOrder) r1;
				OrderSetMember osm1 = null;
				if (r11.getGroup() != null) {
					Set<OrderSetMember> orderSets1 = r11.getGroup().getOrderSet().getMembers();
					for (OrderSetMember member : orderSets1) {
						DrugOrderSetMember dosm = (DrugOrderSetMember) member;
						if (dosm.getDrug().getDrugId() == r1.getDrug()
								.getDrugId()) {
							osm1 = member;
						}
					}
				}
				ExtendedDrugOrder r22 = (ExtendedDrugOrder) r2;
				OrderSetMember osm2 = null;
				if (r22.getGroup() != null) {
					Set<OrderSetMember> orderSets2 = r22.getGroup().getOrderSet().getMembers();
					for (OrderSetMember member2 : orderSets2) {
						DrugOrderSetMember dosm = (DrugOrderSetMember) member2;
						if (dosm.getDrug().getDrugId() == r2.getDrug()
								.getDrugId()) {
							osm2 = member2;
						}
					}
				}
				if (osm1 != null && osm2 != null && osm1.getId() < osm2.getId()) {
					return -20;
				} else {
					return 20;
				}
				// return
				// r1.getDrug().getName().compareTo(r2.getDrug().getName());
			}
		}

		return 0;
	}
}
