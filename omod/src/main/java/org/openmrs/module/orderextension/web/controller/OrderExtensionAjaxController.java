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

import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.util.DrugConceptHelper;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 */
@Controller
public class OrderExtensionAjaxController {

	private Log log = LogFactory.getLog(this.getClass());

	@RequestMapping("/module/orderextension/getDrugsByConcept")
	public void getDrugByConcept(@RequestParam(value = "concept", required=true) Integer conceptId, HttpServletResponse response)
	{
		Concept concept = Context.getConceptService().getConcept(conceptId);
		List<Drug> drugs = Context.getConceptService().getDrugsByConcept(concept);

		List<Map<String, String>> drugInfo = new ArrayList<Map<String, String>>();
		for(Drug drug: drugs)
		{
			Map<String, String> info = new HashMap<String, String>();
			info.put("id", drug.getId().toString());
			info.put("name", drug.getName());

			String doseStrength = drug.getStrength();
			info.put("doseStrength", doseStrength);

			String doseFormConceptId = "";
			String doseForm = "";
			if(drug.getDosageForm() != null)
			{
				doseFormConceptId = drug.getDosageForm().getConceptId().toString();
				doseForm = drug.getDosageForm().getDisplayString();
			}
			info.put("doseFormConceptId", doseFormConceptId);
			info.put("doseForm", doseForm);

			String route = OrderEntryUtil.getRoute(drug);
			info.put("route", route);

			drugInfo.add(info);
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
	        mapper.writeValue(response.getWriter(), drugInfo);
        }
        catch (Exception e) {
        	log.error("Error occurred while writing to response: ", e);
        }
	}

	@RequestMapping("/module/orderextension/getClassificationsByIndication")
	public void getClassificationByIndication(@RequestParam(value = "id", required=true) Integer id, HttpServletResponse response)
	{
		Concept indication = Context.getConceptService().getConcept(id);

		List<Map<String, String>> indicationClassifications = new ArrayList<Map<String, String>>();
		for(Concept classification: indication.getSetMembers())
		{
			Map<String, String> info = new HashMap<String, String>();
			info.put("name", classification.getDisplayString());
			info.put("id", classification.getId().toString());
			indicationClassifications.add(info);
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
	        mapper.writeValue(response.getWriter(), indicationClassifications);
        }
        catch (Exception e) {
        	log.error("Error occurred while writing to response: ", e);
        }
	}

	@RequestMapping("/module/orderextension/getDrugOrder")
	public void getDrugOrder(@RequestParam(value = "id", required=true) Integer id, HttpServletResponse response)
	{
		DrugOrder drugOrder = (DrugOrder)Context.getOrderService().getOrder(id);

		Drug drug = drugOrder.getDrug();

		Map<String, String> info = new HashMap<String, String>();

		if(drug != null) {
			info.put("name", drug.getName());
			info.put("concept", Integer.toString(drug.getConcept().getId()));
			info.put("drugId", drug.getId().toString());
			info.put("doseStrength", drug.getStrength());
			info.put("route", OrderEntryUtil.getRoute(drug));
		}
		else if(drugOrder.getConcept() != null) {
			info.put("name", drugOrder.getConcept().getDisplayString());
		}

		String dose = "";
		if(drugOrder.getDose() != null)
		{
			DecimalFormat f = new DecimalFormat("0.#");
			dose = f.format(drugOrder.getDose());
		}
		info.put("dose", dose);
		info.put("doseUnits", drugOrder.getDoseUnits() == null ? "" : Integer.toString(drugOrder.getDoseUnits().getId()));

		String freqDay = OrderEntryUtil.getFrequencyPerDayAsString(drugOrder);
		String freqWeek = OrderEntryUtil.getFrequencyPerWeekAsString(drugOrder);

		info.put("freqDay", freqDay);
		info.put("freqWeek", freqWeek);

		info.put("asNeeded", drugOrder.getAsNeeded().toString());

		info.put("instructions", drugOrder.getInstructions());


		info.put("adminInstructions", drugOrder.getDosingInstructions());

		String ind = "";
		String classification = "";

		Concept indication = drugOrder.getOrderReason();
		if(indication != null)
		{
			DrugConceptHelper drugHelper = new DrugConceptHelper();
			List<Concept> classifications = drugHelper.getIndications();

			for(Concept concept: classifications)
			{
				List<Concept> setMembers = concept.getSetMembers();
				if(setMembers.contains(indication))
				{
					ind = concept.getId().toString();
					classification =  indication.getId().toString();
				}
				else if(indication.equals(concept))
				{
					ind =  indication.getId().toString();
					break;
				}
			}
		}

		info.put("classification", classification);
		info.put("indication", ind);

		info.put("startDate", Context.getDateFormat().format(drugOrder.getEffectiveStartDate()));

		if(drugOrder.getAutoExpireDate() != null)
		{
			info.put("endDate", Context.getDateFormat().format(drugOrder.getAutoExpireDate()));
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
	        mapper.writeValue(response.getWriter(), info);
        }
        catch (Exception e) {
        	log.error("Error occurred while writing to response: ", e);
        }
	}
}
