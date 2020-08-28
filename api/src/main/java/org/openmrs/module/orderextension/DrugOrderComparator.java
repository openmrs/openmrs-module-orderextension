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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderGroup;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.orderextension.util.OrderEntryUtil;

/**
 * Sorts Drug Orders based on order set sort weight, then by date and then by primary key id
 */
public class DrugOrderComparator implements Comparator<DrugOrder> {

    /**
     * @see Comparator#compare(Object, Object)
     */
    @Override
    public int compare(DrugOrder r1, DrugOrder r2) {
        int ret = 0;

        // First sort based on order in a a group
        ret = compareOrderInGroup(r1, r2);

        // If this does not result in a difference, sort first by start date
        if (ret == 0) {
            ret = r1.getEffectiveStartDate().compareTo(r2.getEffectiveStartDate());
        }

        // If this still does not result in a difference, sort by primary key id
        if (ret == 0) {
            ret = r1.getId().compareTo(r2.getId());
        }
        return ret;
     }

    /**
     * @return compare results of two extended drug orders, based on the sort weight of their associated order set members in the order set
     */
    public int compareOrderInGroup(DrugOrder e1, DrugOrder e2) {
        // Ensure orders are in the same group
        if (e1.getOrderGroup() == null || e2.getOrderGroup() == null || !e1.getOrderGroup().equals(e2.getOrderGroup())) {
            return 0;
        }
        Integer weight1 = getPositionInSet(e1);
        Integer weight2 = getPositionInSet(e2);
        if (weight1 == null || weight2 == null) {
            return 0;
        }
        return weight1.compareTo(weight2);
    }

    /**
     * @return true if an extended drug order matches up with a drug order set member, based on drug and indication
     * TODO: IF WE NEED TO DISTINGUISH MULTIPLE IN A GROUP, THAT SHOULD BE ADDED IN
     */
    public Integer getPositionInSet(DrugOrder o) {
        int positionInSet = 0;
        OrderGroup orderGroup = OrderEntryUtil.getOrderGroup(o);
        String strToCheck = getMemberIdentifier(o.getDrug(), o.getConcept(), o.getOrderReason());
        int occursInGroup = getOccuranceNumberInGroup(o);
        OrderSet orderSet = HibernateUtil.getRealObjectFromProxy(orderGroup.getOrderSet());
        int numFound = 0;
        if (orderSet != null && orderSet instanceof ExtendedOrderSet) {
            for (OrderSetMember m : orderSet.getOrderSetMembers()) {
                positionInSet++;
                ExtendedOrderSetMember osm = new ExtendedOrderSetMember(m);
                String memberStr = getMemberIdentifier(osm.getDrug(), osm.getConcept(), osm.getIndication());
                if (memberStr.equals(strToCheck)) {
                    numFound++;
                }
                if (numFound == occursInGroup) {
                    return positionInSet;
                }
            }
        }
        return positionInSet;
    }

    protected String getMemberIdentifier(Drug drug, Concept concept, Concept indication) {
        StringBuilder sb = new StringBuilder();
        sb.append(drug == null ? "*" : drug.getUuid());
        sb.append(concept == null ? "*" : concept.getUuid());
        sb.append(indication == null ? "*" : indication.getUuid());
        return drug + "." + concept + "." + indication;
    }

    protected int getOccuranceNumberInGroup(DrugOrder drugOrder) {
        int num=0;
        List<DrugOrder> regimen = new ArrayList<DrugOrder>();
        for (Order o : drugOrder.getOrderGroup().getOrders()) {
            regimen.add((DrugOrder)o);
        }
        Collections.sort(regimen, new BeanComparator("effectiveStartDate"));
        String strId = getMemberIdentifier(drugOrder.getDrug(), drugOrder.getConcept(), drugOrder.getOrderReason());
        for (int i=0; i<regimen.size(); i++) {
            DrugOrder memberToCheck = regimen.get(i);
            String strToCheck = getMemberIdentifier(memberToCheck.getDrug(), memberToCheck.getConcept(), memberToCheck.getOrderReason());
            if (strId.equals(strToCheck)) {
                num++;
            }
            if (memberToCheck.equals(drugOrder)) {
                return num;
            }
        }
        throw new IllegalStateException("Order not found in group");
    }
}
