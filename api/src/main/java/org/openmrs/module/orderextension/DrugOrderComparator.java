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
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;

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
        OrderSet os1 = (e1.getOrderGroup() != null ? e1.getOrderGroup().getOrderSet() : null);
        OrderSet os2 = (e2.getOrderGroup() != null ? e2.getOrderGroup().getOrderSet() : null);
        if (os1 != null && os2 != null && os1.equals(os2)) {
            Integer weight1 = -1;
            Integer weight2 = -1;
            for (OrderSetMember osm : os1.getOrderSetMembers()) {
                if ("orderextension".equals(osm.getOrderTemplateType())) {
                    ExtendedOrderSetMember m = new ExtendedOrderSetMember(osm);
                    if (extendedDrugOrderMatchesOrderSetMember(e1, m)) {
                        weight1 = m.getSortWeight();
                    }
                    if (extendedDrugOrderMatchesOrderSetMember(e2, m)) {
                        weight2 = m.getSortWeight();
                    }
                }
            }
            return weight1.compareTo(weight2);
        }
        return 0;
    }

    /**
     * @return true if an extended drug order matches up with a drug order set member, based on drug and indication
     * If a particular drug/indication pair occur several times in an order set on different dates, account for this
     */
    public boolean extendedDrugOrderMatchesOrderSetMember(DrugOrder edo, ExtendedOrderSetMember m) {
        if (nullSafeEquals(edo.getDrug(), m.getDrug())) {
            if (nullSafeEquals(edo.getOrderReason(), m.getIndication())) {
                int memberOccuranceNum = getOccuranceNumberInSet(m);
                int orderOccuranceNum = getOccuranceNumberInGroup(edo);
                return memberOccuranceNum == orderOccuranceNum;
            }
        }
        return false;
    }

    protected int getOccuranceNumberInSet(ExtendedOrderSetMember m) {
        int num = 0;
        for (int i=0; i<m.getOrderSet().getOrderSetMembers().size(); i++) {
            OrderSetMember osm = m.getOrderSet().getOrderSetMembers().get(i);
            ExtendedOrderSetMember memberToCheck = new ExtendedOrderSetMember(osm);
            if (nullSafeEquals(memberToCheck.getDrug(), m.getDrug()) && nullSafeEquals(memberToCheck.getIndication(), m.getIndication())) {
                num++;
            }
            if (memberToCheck.equals(m)) {
                return num;
            }
        }
        throw new IllegalStateException("Member not found in set");
    }

    protected int getOccuranceNumberInGroup(DrugOrder edo) {
        int num=0;
        List<DrugOrder> regimen = new ArrayList<DrugOrder>();
        for (Order o : edo.getOrderGroup().getOrders()) {
            regimen.add((DrugOrder)o);
        }
        Collections.sort(regimen, new BeanComparator("effectiveStartDate"));
        for (int i=0; i<regimen.size(); i++) {
            DrugOrder memberToCheck = regimen.get(i);
            if (nullSafeEquals(memberToCheck.getDrug(), edo.getDrug()) && nullSafeEquals(memberToCheck.getOrderReason(), edo.getOrderReason())) {
                num++;
            }
            if (memberToCheck.equals(edo)) {
                return num;
            }
        }
        throw new IllegalStateException("Order not found in group");
    }

    /**
     * @return true if both arguments are null or are equal to each other, false otherwise
     */
    private boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        if (o1 != null) {
            return o1.equals(o2);
        }
        return false;
    }
}
