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
package org.openmrs.module.orderextension.web.controller;

import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.module.orderextension.util.OrderEntryUtil;

import java.util.List;
import java.util.Map;

public class WebUtils {

	public static final String ALL_DRUG_ORDER_WRAPPERS = "allDrugOrders";

	public static List<DrugOrder> getDrugOrdersByPatient(Patient patient, Map<String, Object> model) {
		List<DrugOrder> l = (List<DrugOrder>) model.get(ALL_DRUG_ORDER_WRAPPERS);
		if (l == null) {
			l = OrderEntryUtil.getDrugOrdersByPatient(patient);
			model.put(ALL_DRUG_ORDER_WRAPPERS, l);
		}
		return l;
	}
}
