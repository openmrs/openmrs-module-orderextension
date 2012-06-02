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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.DrugOrder;
import org.openmrs.module.orderextension.DrugClassificationHelper;
import org.openmrs.module.orderextension.util.DrugConceptHelper;
import org.openmrs.web.controller.PortletController;
import org.springframework.stereotype.Controller;

/**
 * The main controller.
 */
@Controller
public class OrderExtensionPortletController extends PortletController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public final String CURRENT_MODE = "current";
	public final String COMPLETED_MODE = "completed";
	public final String FUTURE_MODE = "future";
	public final String HISTORY_MODE = "history";
	
	/**
	 * @see PortletController#populateModel(HttpServletRequest, Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {

		List<DrugOrder> allOrders = (List<DrugOrder>)model.get("patientDrugOrders");
		String mode = (String) model.get("mode");
		
		List<DrugOrder> orders = new ArrayList<DrugOrder>();
		
		for (DrugOrder o : allOrders) {
			if (o.isCurrent()) {
				if (CURRENT_MODE.equals(mode)) {
					orders.add(o);
				}
			}
			else if (o.isFuture()) {
				if (FUTURE_MODE.equals(mode)) {
					orders.add(o);
				}
			}
			else {
				Calendar fiveYearsAgo = Calendar.getInstance();
				fiveYearsAgo.add(Calendar.YEAR, -5);
				Date historyDate = fiveYearsAgo.getTime();
				if (COMPLETED_MODE.equals(mode)) {
					if (o.getStartDate().compareTo(historyDate) >= 0) {
						orders.add(o);
					}
				}
				else if (HISTORY_MODE.equals(mode)) {
					if (o.getStartDate().compareTo(historyDate) < 0) {
						orders.add(o);
					}
				}
			}
		}

		DrugClassificationHelper helper = new DrugClassificationHelper(orders);
		DrugConceptHelper drugHelper = new DrugConceptHelper();
		
		model.put("classifications", helper);
		
		model.put("drugs", drugHelper.getDistinctSortedDrugs());
		
		model.put("indications", drugHelper.getIndications());
	}
}
