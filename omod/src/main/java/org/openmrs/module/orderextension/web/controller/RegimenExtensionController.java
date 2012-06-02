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
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.DrugOrderComparator;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedDrugOrder;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.module.orderextension.util.DrugConceptHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * The main controller.
 */
@Controller
public class RegimenExtensionController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/** Success form view name */
	private final String SUCCESS_FORM_VIEW = "/module/orderextension/extendedregimen";
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView regimenTab(@RequestParam(required = true, value = "patientId") Integer patientId, ModelMap model) {
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		List<DrugOrder> allDrugOrders = Context.getOrderService().getDrugOrdersByPatient(patient);
		List<DrugOrder> drugOrdersNonContinuous = new ArrayList<DrugOrder>();
		List<DrugOrder> drugOrdersContinuous = new ArrayList<DrugOrder>();
		List<DrugRegimen> cycles = new ArrayList<DrugRegimen>();
		
		for(DrugOrder drugOrder : allDrugOrders)
		{
			if(drugOrder.getDiscontinuedDate() != null || drugOrder.getAutoExpireDate() != null)
			{
				drugOrdersNonContinuous.add(drugOrder);
			}
			else
			{
				drugOrdersContinuous.add(drugOrder);
			}
			
			if (drugOrder instanceof ExtendedDrugOrder) {
				ExtendedDrugOrder edo = (ExtendedDrugOrder)drugOrder;
				if(edo.getGroup() != null && edo.getGroup() instanceof DrugRegimen) {
					DrugRegimen regimen = (DrugRegimen)edo.getGroup();
					if (regimen.isCyclical() && !cycles.contains(regimen))
					{
						cycles.add(regimen);
					}
				}
			}
		}
		
		Collections.sort(drugOrdersContinuous, new DrugOrderComparator());
		
		DrugConceptHelper drugHelper = new DrugConceptHelper();
		
		model.put("drugOrdersNonContinuous", drugOrdersNonContinuous);
		model.put("drugOrdersContinuous", drugOrdersContinuous);
		model.put("cycles", cycles);
		
		model.addAttribute("orderSets", Context.getService(OrderExtensionService.class).getNamedOrderSets(false));
		
		model.addAttribute("drugs", drugHelper.getDistinctSortedDrugs());
		
		model.addAttribute("indications", drugHelper.getIndications());
		
		return new ModelAndView(SUCCESS_FORM_VIEW, "model", model);
	}
}
