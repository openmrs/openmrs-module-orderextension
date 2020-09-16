/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.orderextension;

import java.util.List;

import org.junit.Before;
import org.openmrs.DrugOrder;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;

/**
 * Tests for Order Extension
 */
public abstract class BaseOrderExtensionTest extends BaseModuleContextSensitiveTest {

    @Before
    public void setupTestData() throws Exception {
        executeDataSet("org/openmrs/module/orderextension/include/orderSets.xml");
        updateGlobalProperty("orderextension.drugOrderType", "Drug order");
        updateGlobalProperty(OpenmrsConstants.GP_ORDER_NUMBER_GENERATOR_BEAN_ID, "orderEntry.OrderNumberGenerator");
    }

    protected List<DrugOrder> getOrdersForPatient(Patient patient) {
        return getService().getDrugOrders(patient, null, null, null);
    }

    protected void updateGlobalProperty(String propertyName, String propertyValue) {
        GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(propertyName);
        if (gp == null) {
            gp = new GlobalProperty();
            gp.setProperty(propertyName);
        }
        gp.setPropertyValue(propertyValue);
        Context.getAdministrationService().saveGlobalProperty(gp);
    }

    protected OrderExtensionService getService() {
        return Context.getService(OrderExtensionService.class);
    }
}
