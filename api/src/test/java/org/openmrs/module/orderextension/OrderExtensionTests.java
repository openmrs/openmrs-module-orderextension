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

import java.util.Date;

import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.OrderContext;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for Order Extension
 */
public class OrderExtensionTests extends BaseModuleContextSensitiveTest {

	@Test
	public void shouldSuccessfullyMergePatientsWithExtendedDrugOrders() throws Exception {

		Patient p1 = Context.getPatientService().getPatient(2);
		Patient p2 = Context.getPatientService().getPatient(6);

		{
			Encounter encounter = new Encounter();
			encounter.setPatient(p1);
			encounter.setEncounterDatetime(new Date());
			encounter.setEncounterType(Context.getEncounterService().getAllEncounterTypes().get(0));
			encounter.setLocation(Context.getLocationService().getLocation(1));
			Context.getEncounterService().saveEncounter(encounter);

			DrugOrder drugOrder = new DrugOrder();
			drugOrder.setOrderType(Context.getOrderService().getOrderType(1));
			drugOrder.setPatient(p1);
			drugOrder.setEncounter(encounter);
			drugOrder.setConcept(Context.getConceptService().getConcept(88));
			drugOrder.setDrug(Context.getConceptService().getDrug(3));
			drugOrder.setCareSetting(Context.getOrderService().getCareSettingByName("INPATIENT"));
			drugOrder.setOrderer(Context.getProviderService().getProvider(1));
			drugOrder.setDose(100d);
			drugOrder.setDoseUnits(Context.getConceptService().getConcept(50));
			drugOrder.setRoute(Context.getOrderService().getDrugRoutes().get(0));
			drugOrder.setFrequency(Context.getOrderService().getOrderFrequency(1));
			drugOrder.setDosingInstructions("Take 2 and call me in the morning");
			drugOrder.setDateActivated(new Date());
			Context.getOrderService().saveOrder(drugOrder, new OrderContext());
		}

		Context.getPatientService().mergePatients(p1, p2);
	}
}
