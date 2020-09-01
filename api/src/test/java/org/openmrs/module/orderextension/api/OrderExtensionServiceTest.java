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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for Order Extension
 */
public class OrderExtensionServiceTest extends BaseModuleContextSensitiveTest {

    @Before
    public void setupTestData() throws Exception {
        executeDataSet("org/openmrs/module/orderextension/include/orderSets.xml");
    }

	@Test
	public void shouldSaveAnOrderSet() throws Exception {
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

        OrderSet orderSet1 = getService().getOrderSet(orderSet.getId());
        Assert.assertEquals(orderSet.getName(), orderSet1.getName());
        Assert.assertEquals(orderSet.getDescription(), orderSet1.getDescription());
        Assert.assertEquals(orderSet.getCategory(), orderSet1.getCategory());
        Assert.assertEquals(ExtendedOrderSet.class, orderSet1.getClass());

        ExtendedOrderSet extendedOrderSet1 = (ExtendedOrderSet) orderSet1;
        Assert.assertEquals(orderSet.isCyclical(), extendedOrderSet1.isCyclical());
        Assert.assertEquals(orderSet.getCycleLengthInDays(), extendedOrderSet1.getCycleLengthInDays());
        Assert.assertEquals(1, orderSet1.getOrderSetMembers().size());

        OrderSetMember member1 = orderSet1.getOrderSetMembers().get(0);
        Assert.assertEquals(member.getConcept(), member1.getConcept());
        Assert.assertEquals(member.getOrderTemplateType(), member1.getOrderTemplateType());
        Assert.assertEquals(member.getOrderTemplate(), member1.getOrderTemplate());
	}

    private OrderExtensionService getService() {
        return Context.getService(OrderExtensionService.class);
    }
}
