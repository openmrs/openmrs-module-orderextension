/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.orderextension.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.DrugOrderComparator;
import org.openmrs.module.orderextension.DrugOrderSetMember;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedDrugOrder;
import org.openmrs.module.orderextension.OrderSet;
import org.openmrs.module.orderextension.OrderSetMember;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Tests for Order Extension
 */
public class DrugOrderComparatorTest extends BaseModuleContextSensitiveTest {

    @Before
    public void setupTestData() throws Exception {
        executeDataSet("org/openmrs/module/orderextension/include/orderSets.xml");
    }

	@Test
	public void shouldSuccessfullySortExtendedDrugOrdersBasedOnOrderSetMembership() throws Exception {

        // First create a couple of order groups based the test order set
        Patient p = Context.getPatientService().getPatient(2);
        OrderSet orderSet = getService().getOrderSet(21);
        Context.getService(OrderExtensionService.class).addOrdersForPatient(p, orderSet, new Date(), 3);

        // Ensure that three order groups were in fact created
        List<DrugRegimen> regimens = getService().getOrderGroups(p, DrugRegimen.class);
        Assert.assertEquals(3, regimens.size());

        // Determine the expected order of the order set members
        Map<Integer, OrderSetMember> m = new TreeMap<Integer, OrderSetMember>();
        for (OrderSetMember member : orderSet.getMembers()) {
            m.put(member.getSortWeight(), member);
        }
        List<OrderSetMember> membersOrderedBySortWeight = new ArrayList<OrderSetMember>(m.values());

        // For each of the three order groups created, run this test
        for (DrugRegimen regimen : regimens) {

            // Ensure that the right number of drug orders was created, based on the order set
            List<DrugOrder> orderList = new ArrayList<DrugOrder>(regimen.getMembers());
            Assert.assertEquals(membersOrderedBySortWeight.size(), orderList.size());

            // Sort the drug orders, using the DrugOrderComparator
            Collections.sort(orderList, new DrugOrderComparator());

            // Test that the drug order at the given index is based on the order set member at the same index, based on drug, indication, route
            for (int i=0; i<orderList.size(); i++) {
                ExtendedDrugOrder order = (ExtendedDrugOrder)orderList.get(i);
                DrugOrderSetMember member = (DrugOrderSetMember)membersOrderedBySortWeight.get(i);
                Assert.assertEquals(member.getDrug(), order.getDrug());
                Assert.assertEquals(member.getIndication(), order.getIndication());
                Assert.assertEquals(member.getRoute(), order.getRoute());
            }
        }
	}

    private OrderExtensionService getService() {
        return Context.getService(OrderExtensionService.class);
    }
}