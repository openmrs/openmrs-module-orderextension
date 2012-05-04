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

import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.RegimenExtension;
import org.openmrs.module.orderextension.api.OrderExtensionService;
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
		
		OrderExtensionService service = Context.getService(OrderExtensionService.class);
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		List<RegimenExtension> allDrugOrders = service.getAllRegimens(patient, false);
		
		Calendar futureDate = Calendar.getInstance();
		futureDate.add(Calendar.YEAR, 3);
		
		model.put("allDrugOrders", allDrugOrders);
		model.put("futureDate", futureDate.getTime());
		
	    return new ModelAndView(SUCCESS_FORM_VIEW, "model", model);
	}
}
