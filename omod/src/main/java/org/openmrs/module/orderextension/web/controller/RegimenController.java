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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.DrugClassificationHelper;
import org.openmrs.module.orderextension.RegimenExtension;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.springframework.stereotype.Controller;

/**
 * The main controller.
 */
@Controller
public class RegimenController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public final String CURRENT_MODE = "current";
	public final String COMPLETED_MODE = "completed";
	public final String FUTURE_MODE = "future";
	public final String HISTORY_MODE = "history";

	
	protected void populateModel(Integer patientId, String mode, Map<String, Object> model) {
		
		OrderExtensionService service = Context.getService(OrderExtensionService.class);
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		List<RegimenExtension> regimens = null;
		
		if(mode.equals(CURRENT_MODE))
		{
			regimens = service.getCurrentRegimens(patient, null); 
		}
		else if(mode.equals(COMPLETED_MODE))
		{
			Calendar fiveYearsAgo = Calendar.getInstance();
			fiveYearsAgo.add(Calendar.YEAR, -5);
			
			regimens = service.getCompletedRegimens(patient, null, fiveYearsAgo.getTime());
		}
		else if(mode.equals(FUTURE_MODE))
		{
			regimens = service.getFutureRegimens(patient, null);
		}
		else if(mode.equals(HISTORY_MODE))
		{
			Calendar fiveYearsAgo = Calendar.getInstance();
			fiveYearsAgo.add(Calendar.YEAR, -5);
			fiveYearsAgo.add(Calendar.DAY_OF_YEAR, -1);
			regimens = service.getCompletedRegimens(patient, fiveYearsAgo.getTime());
		}
		
		DrugClassificationHelper helper = new DrugClassificationHelper(regimens);
		model.put("classifications", helper);
	}
}
