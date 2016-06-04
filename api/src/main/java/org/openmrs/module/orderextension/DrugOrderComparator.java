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

import org.openmrs.DrugOrder;

import java.util.Comparator;

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

        // If the order is an extended drug order, attempt to compare based on the sortWeight of the order set member associated with it
        if (r1 instanceof ExtendedDrugOrder && r2 instanceof ExtendedDrugOrder) {
            ret = compareExtendedDrugOrders((ExtendedDrugOrder)r1, (ExtendedDrugOrder)r2);
        }

        // If this does not result in a difference, sort first by start date
        if (ret == 0) {
            ret = r1.getStartDate().compareTo(r2.getStartDate());
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
    public int compareExtendedDrugOrders(ExtendedDrugOrder e1, ExtendedDrugOrder e2) {
        OrderSet os1 = (e1.getGroup() != null ? e1.getGroup().getOrderSet() : null);
        OrderSet os2 = (e2.getGroup() != null ? e2.getGroup().getOrderSet() : null);
        if (os1 != null && os2 != null && os1.equals(os2)) {
            int weight1 = -1;
            int weight2 = -1;
            for (OrderSetMember osm : os1.getMembers()) {
                if (osm instanceof DrugOrderSetMember) {
                    DrugOrderSetMember m = (DrugOrderSetMember) osm;
                    if (extendedDrugOrderMatchesOrderSetMember(e1, m)) {
                        weight1 = m.getSortWeight() == null ? -1 : m.getSortWeight();
                    }
                    if (extendedDrugOrderMatchesOrderSetMember(e2, m)) {
                        weight2 = m.getSortWeight() == null ? -1 : m.getSortWeight();
                    }
                }
            }
            if (weight1 >= 0 && weight2 >= 0) {
                return weight1 - weight2;
            }
            else if (weight1 >= 0) {
                return -1;
            }
            else if (weight2 >= 0) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * @return true if an extended drug order matches up with a drug order set member, based on drug and indication
     */
    public boolean extendedDrugOrderMatchesOrderSetMember(ExtendedDrugOrder edo, DrugOrderSetMember m) {
        return nullSafeEquals(edo.getDrug(), m.getDrug()) && nullSafeEquals(edo.getIndication(), m.getIndication());
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
