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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.ExtendedDrugOrder;
import org.openmrs.module.orderextension.OrderGroup;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Primary Controller for administering orders
 */
@Controller
public class OrderExtensionOrderListController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Shows the page to list order sets
	 */
	@RequestMapping(value = "/module/orderextension/orderList")
	public void listOrders(ModelMap model, @RequestParam(value="patientId", required=true) Integer patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		model.addAttribute("patient", patient);
		
		List<Order> orders = Context.getOrderService().getOrdersByPatient(patient);
		List<ExtendedDrugOrder> extendedOrders = Context.getService(OrderExtensionService.class).getExtendedOrders(patient, ExtendedDrugOrder.class);
		for (ExtendedDrugOrder eo : extendedOrders) {
			orders.remove(eo.getOrder());
		}
		for (Order o : orders) {
			if (o instanceof DrugOrder) {
				extendedOrders.add(new ExtendedDrugOrder((DrugOrder)o));
			}
		}
		model.addAttribute("extendedOrders", extendedOrders);
		
		Map<OrderGroup, List<ExtendedDrugOrder>> ordersByGroup = new LinkedHashMap<OrderGroup, List<ExtendedDrugOrder>>();
		List<ExtendedDrugOrder> ungroupedOrders = new ArrayList<ExtendedDrugOrder>();
		for (ExtendedDrugOrder eo : extendedOrders) {
			OrderGroup g = eo.getGroup();
			if (g == null) {
				ungroupedOrders.add(eo);
			}
			else {
				List<ExtendedDrugOrder> l = ordersByGroup.get(g);
				if (l == null) {
					l = new ArrayList<ExtendedDrugOrder>();
					ordersByGroup.put(g, l);
				}
				l.add(eo);
			}
		}
		ordersByGroup.put(null, ungroupedOrders);
		model.addAttribute("ordersByGroup", ordersByGroup);
	}
}
