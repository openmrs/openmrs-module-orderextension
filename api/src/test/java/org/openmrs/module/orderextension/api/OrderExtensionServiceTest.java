/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.orderextension.api;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.BaseOrderExtensionTest;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.util.OrderExtensionUtil;

/**
 * Tests for Order Extension
 */
public class OrderExtensionServiceTest extends BaseOrderExtensionTest {

	@Test
	public void shouldSaveAnOrderSet() {
        ExtendedOrderSet orderSet = new ExtendedOrderSet();
        orderSet.setName("Test Order Set");
        orderSet.setDescription("This is a test");
        orderSet.setOperator(OrderSet.Operator.ANY);
        orderSet.setIndication(Context.getConceptService().getConcept(7098));
        orderSet.setCyclical(true);
        orderSet.setCycleLengthInDays(30);

        OrderSetMember member = new OrderSetMember();
        member.setOrderType(Context.getOrderService().getOrderType(1));
        member.setConcept(Context.getConceptService().getConcept(491));
        member.setOrderTemplateType("org.openmrs.module.orderextension");
        member.setOrderTemplate("{ \"drug\": 223 }");
        orderSet.addOrderSetMember(member);

        getService().saveOrderSet(orderSet);

        ExtendedOrderSet orderSet1 = getService().getOrderSet(orderSet.getId());
        Assert.assertEquals(orderSet.getName(), orderSet1.getName());
        Assert.assertEquals(orderSet.getDescription(), orderSet1.getDescription());
        Assert.assertEquals(orderSet.getCategory(), orderSet1.getCategory());
        Assert.assertEquals(ExtendedOrderSet.class, orderSet1.getClass());

        Assert.assertEquals(orderSet.isCyclical(), orderSet1.isCyclical());
        Assert.assertEquals(orderSet.getCycleLengthInDays(), orderSet1.getCycleLengthInDays());
        Assert.assertEquals(1, orderSet1.getOrderSetMembers().size());

        OrderSetMember member1 = orderSet1.getOrderSetMembers().get(0);
        Assert.assertEquals(member.getConcept(), member1.getConcept());
        Assert.assertEquals(member.getOrderTemplateType(), member1.getOrderTemplateType());
        Assert.assertEquals(member.getOrderTemplate(), member1.getOrderTemplate());
	}

    @Test
    public void shouldAddOrdersFromAnOrderSet() {
        Patient patient = Context.getPatientService().getPatient(2);
        ExtendedOrderSet orderSet = getService().getOrderSet(50);
        int numCycles = 3;

        List<DrugOrder> startingOrders = getOrdersForPatient(patient);
        getService().addOrdersForPatient(patient, orderSet, new Date(), numCycles);
        int expectedNumberOfOrders = orderSet.getOrderSetMembers().size() * numCycles;
        List<DrugOrder> endingOrders = getOrdersForPatient(patient);
        endingOrders.removeAll(startingOrders);
        Assert.assertEquals(expectedNumberOfOrders, endingOrders.size());
    }

    @Test
    public void shouldChangeStartDateOfAllCyclesToEarlierDate() {
        Patient patient = Context.getPatientService().getPatient(2);
        ExtendedOrderSet orderSet = getService().getOrderSet(50);

        // First, add 3 cycles, starting in 1 week
        int numCycles = 3;
        Date today = new Date();
        Date oneWeekFromNow = OrderExtensionUtil.adjustDate(today, 7);
        List<DrugRegimen> regimens = getService().addOrdersForPatient(patient, orderSet, oneWeekFromNow, numCycles);
        Assert.assertEquals(numCycles, regimens.size());

        // Next, change the start date to today for the current cycle, and apply to all future cycles
        getService().changeStartDateOfGroup(patient, regimens.get(0), today, true);
    }

    @Test
    public void shouldAddDrugOrderToGroup() {
        Patient patient = Context.getPatientService().getPatient(2);
        ExtendedOrderSet orderSet = getService().getOrderSet(50);

        // First, add 3 cycles, starting in 1 week
        int numCycles = 3;
        Date today = new Date();
        Date oneWeekFromNow = OrderExtensionUtil.adjustDate(today, 7);
        List<DrugRegimen> regimens = getService().addOrdersForPatient(patient, orderSet, oneWeekFromNow, numCycles);
        Assert.assertEquals(numCycles, regimens.size());

        // Next, change the start date to today for the current cycle, and apply to all future cycles
        getService().changeStartDateOfGroup(patient, regimens.get(0), today, true);
    }
}
