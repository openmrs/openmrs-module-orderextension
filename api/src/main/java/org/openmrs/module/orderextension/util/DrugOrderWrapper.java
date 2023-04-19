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

import org.apache.commons.beanutils.BeanComparator;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderGroup;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.ExtendedOrderSetMember;

import java.util.ArrayList;
import java.util.List;

public class DrugOrderWrapper implements Comparable<DrugOrderWrapper> {

	private final DrugOrder drugOrder;
	private final OrderGroup orderGroup;
	private final OrderSet orderSet;
	private final String memberIdentifier;
	private final Integer occurrenceNumberInGroup;
	private final Integer positionInSet;

	public DrugOrderWrapper(DrugOrder o) {
		drugOrder = o;
		orderGroup = OrderEntryUtil.getOrderGroup(o);
		orderSet = HibernateUtil.getRealObjectFromProxy(orderGroup.getOrderSet());
		memberIdentifier = getMemberIdentifier(o.getDrug(), o.getConcept(), o.getOrderReason());
		occurrenceNumberInGroup = calculateOccurrenceNumberInGroup();
		positionInSet = calculatePositionInSet();
	}

	private String getMemberIdentifier(Drug drug, Concept concept, Concept indication) {
		return (drug == null ? "*" : drug.getUuid()) + (concept == null ? "*" : concept.getUuid()) + (indication == null ? "*" : indication.getUuid());
	}

	private int calculateOccurrenceNumberInGroup() {
		int num=0;
		List<DrugOrder> regimen = new ArrayList<>();
		if (orderGroup != null) {
			for (Order o : orderGroup.getOrders()) {
				regimen.add((DrugOrder) o);
			}
			regimen.sort(new BeanComparator<>("effectiveStartDate"));
			for (DrugOrder memberToCheck : regimen) {
				String strToCheck = getMemberIdentifier(memberToCheck.getDrug(), memberToCheck.getConcept(), memberToCheck.getOrderReason());
				if (memberIdentifier.equals(strToCheck)) {
					num++;
				}
				if (memberToCheck.equals(drugOrder)) {
					return num;
				}
			}
		}
		throw new IllegalStateException("Order not found in group");
	}

	public Integer calculatePositionInSet() {
		int positionInSet = 0;
		int numFound = 0;
		if (orderSet != null && orderSet instanceof ExtendedOrderSet) {
			for (OrderSetMember m : orderSet.getOrderSetMembers()) {
				positionInSet++;
				ExtendedOrderSetMember osm = new ExtendedOrderSetMember(m);
				String memberStr = getMemberIdentifier(osm.getDrug(), osm.getConcept(), osm.getIndication());
				if (memberStr.equals(memberIdentifier)) {
					numFound++;
				}
				if (numFound == occurrenceNumberInGroup) {
					return positionInSet;
				}
			}
		}
		return positionInSet;
	}

	@Override
	public int compareTo(DrugOrderWrapper that) {

		// If orders are in the same group, sort based on position in the set
		if (this.getOrderGroup() != null && this.getOrderGroup().equals(that.getOrderGroup())) {
			int ret = this.getPositionInSet().compareTo(that.getPositionInSet());
			if (ret != 0) {
				return ret;
			}
		}

		// If not ordered by position in the set, try to order by start date
		int ret = this.drugOrder.getEffectiveStartDate().compareTo(that.drugOrder.getEffectiveStartDate());
		if (ret != 0) {
			return ret;
		}

		// If this still does not result in a difference, sort by primary key id
		return this.drugOrder.getId().compareTo(that.drugOrder.getId());
	}

	public DrugOrder getDrugOrder() {
		return drugOrder;
	}

	public OrderGroup getOrderGroup() {
		return orderGroup;
	}

	public OrderSet getOrderSet() {
		return orderSet;
	}

	public String getMemberIdentifier() {
		return memberIdentifier;
	}

	public Integer getPositionInSet() {
		return positionInSet;
	}
}
