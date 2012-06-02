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

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedDrugOrder;
import org.openmrs.module.orderextension.OrderSet;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Primary Controller for administering orders
 */
@Controller
public class OrderExtensionOrderController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Shows the page to list order sets
	 */
	@RequestMapping(value = "/module/orderextension/orderList")
	public void listOrders(ModelMap model, @RequestParam(value="patientId", required=true) Integer patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		model.addAttribute("patient", patient);
		
		List<DrugRegimen> regimens = Context.getService(OrderExtensionService.class).getOrderGroups(patient, DrugRegimen.class);
		model.addAttribute("regimens", regimens);
		
		List<DrugOrder> drugOrders = Context.getOrderService().getDrugOrdersByPatient(patient);
		for (DrugRegimen r : regimens) {
			drugOrders.removeAll(r.getMembers());
		}
		model.addAttribute("drugOrders", drugOrders);
		
		model.addAttribute("orderSets", Context.getService(OrderExtensionService.class).getNamedOrderSets(false));
	}
	
	/**
	 * adds a new orderSet
	 */
	@RequestMapping(value = "/module/orderextension/addOrderSet")
	public String addOrderSet(ModelMap model, 
								 @RequestParam(value="patientId", required=true) Integer patientId,
								 @RequestParam(value="orderSet", required=true) Integer orderSetId,
								 @RequestParam(value="startDateSet", required=true) Date startDateSet,
								 @RequestParam(value="numCycles", required=false) Integer numCycles,
								 @RequestParam(value="returnPage", required=true) String returnPage) {
		
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		OrderSet orderSet = Context.getService(OrderExtensionService.class).getOrderSet(orderSetId);
		Context.getService(OrderExtensionService.class).addOrdersForPatient(patient, orderSet, startDateSet, numCycles);
		
		return "redirect:"+returnPage;
	}
	
	/**
	 * adds a new order
	 */
	@RequestMapping(value = "/module/orderextension/addDrugOrder")
	public String addOrder(ModelMap model, 
								 @RequestParam(value="patientId", required=true) Integer patientId,
								 @RequestParam(value="drug", required=true) Integer drugId,
								 @RequestParam(value="dose", required=true) Double dose,
								 @RequestParam(value="frequencyDay", required=false) String frequencyDay,
								 @RequestParam(value="frequencyWeek", required=false) String frequencyWeek,
								 @RequestParam(value="startDateDrug", required=true) Date startDateDrug,
								 @RequestParam(value="stopDateDrug", required=false) Date stopDateDrug,
								 @RequestParam(value="asNeeded", required=false) String asNeeded,
								 @RequestParam(value="classification", required=false) Integer classification,
								 @RequestParam(value="indication", required=false) Integer indication,
								 @RequestParam(value="instructions", required=false) String instructions,
								 @RequestParam(value="adminInstructions", required=false) String adminInstructions,
								 @RequestParam(value="returnPage", required=true) String returnPage) {
		
		ExtendedDrugOrder drugOrder = setUpDrugOrder(patientId, drugId, dose, frequencyDay, frequencyWeek, startDateDrug, stopDateDrug, asNeeded, classification, indication, instructions, adminInstructions);
	
		Context.getService(OrderExtensionService.class).saveExtendedDrugOrder(drugOrder);
	
		return "redirect:"+returnPage;
	}
	
	private ExtendedDrugOrder setUpDrugOrder(Integer patientId, Integer drugId, Double dose, String frequencyDay, String frequencyWeek, Date startDateDrug, Date stopDateDrug, String asNeeded, Integer classification, Integer indication, String instructions, String adminInstructions)
	{
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		ExtendedDrugOrder drugOrder = new ExtendedDrugOrder();
		drugOrder.setPatient(patient);
		
		drugOrder = updateDrugOrder(drugOrder, drugId, dose, frequencyDay, frequencyWeek, startDateDrug, stopDateDrug, asNeeded, classification, indication, instructions, adminInstructions);
		
		return drugOrder;
	}
	
	private ExtendedDrugOrder updateDrugOrder(ExtendedDrugOrder drugOrder, Integer drugId, Double dose, String frequencyDay, String frequencyWeek, Date startDateDrug, Date stopDateDrug, String asNeeded, Integer classification, Integer indication, String instructions, String adminInstructions)
	{
		Drug drug = Context.getConceptService().getDrug(drugId);
		drugOrder.setDrug(drug);
		drugOrder.setConcept(drug.getConcept());
		drugOrder.setDose(dose);
		
		String frequency = "";
		if(frequencyDay != null && frequencyDay.length() > 0)
		{
			frequency = frequencyDay;
		}
		if(frequencyWeek != null && frequencyWeek.length() > 0)
		{
			if(frequency.length() > 0)
			{
				frequency = frequency + " x ";
			}
			
			frequency = frequency + frequencyWeek;
		}
		drugOrder.setFrequency(frequency);
		drugOrder.setStartDate(startDateDrug);
		if(asNeeded != null && asNeeded.equals("true"))
		{
			drugOrder.setPrn(true);
		}
		if(classification != null)
		{
			drugOrder.setIndication(Context.getConceptService().getConcept(classification));
		}
		else if(indication != null)
		{
			drugOrder.setIndication(Context.getConceptService().getConcept(indication));
		}
		if(instructions != null && instructions.length() > 0)
		{
			drugOrder.setInstructions(instructions);
		}
		if(adminInstructions != null && adminInstructions.length() > 0)
		{
			drugOrder.setAdministrationInstructions(adminInstructions);
		}
		
		if(stopDateDrug != null)
		{
			drugOrder.setAutoExpireDate(stopDateDrug);
		}
		
		OrderType orderType = Context.getOrderService().getOrderType(OpenmrsConstants.ORDERTYPE_DRUG);
		drugOrder.setOrderType(orderType);
		
		return drugOrder;
	}
	
	@RequestMapping(value = "/module/orderextension/addDrugOrderToGroup")
	public String addDrugOrderToGroup(ModelMap model, 
								 @RequestParam(value="patientId", required=true) Integer patientId,
								 @RequestParam(value="groupId", required=true) Integer groupId,
								 @RequestParam(value="drug", required=true) Integer drugId,
								 @RequestParam(value="dose", required=true) Double dose,
								 @RequestParam(value="frequencyDay", required=false) String frequencyDay,
								 @RequestParam(value="frequencyWeek", required=false) String frequencyWeek,
								 @RequestParam(value="startDate", required=true) Date startDateDrug,
								 @RequestParam(value="stopDate", required=false) Date stopDateDrug,
								 @RequestParam(value="asNeeded", required=false) String asNeeded,
								 @RequestParam(value="classification", required=false) Integer classification,
								 @RequestParam(value="indication", required=false) Integer indication,
								 @RequestParam(value="instructions", required=false) String instructions,
								 @RequestParam(value="adminInstructions", required=false) String adminInstructions,
								 @RequestParam(value="returnPage", required=true) String returnPage) {
		
		DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(groupId);
		
		ExtendedDrugOrder drugOrder = setUpDrugOrder(patientId, drugId, dose, frequencyDay, frequencyWeek, startDateDrug, stopDateDrug, asNeeded, classification, indication, instructions, adminInstructions);
		regimen.addMember(drugOrder);
		
		Context.getService(OrderExtensionService.class).saveOrderGroup(regimen);
	
		return "redirect:"+returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/editDrug")
	public String editDrug(ModelMap model, 
								 @RequestParam(value="orderId", required=true) Integer orderId,
								 @RequestParam(value="drug", required=true) Integer drugId,
								 @RequestParam(value="dose", required=true) Double dose,
								 @RequestParam(value="frequencyDay", required=false) String frequencyDay,
								 @RequestParam(value="frequencyWeek", required=false) String frequencyWeek,
								 @RequestParam(value="editStartDate", required=true) Date startDateDrug,
								 @RequestParam(value="editStopDate", required=false) Date stopDateDrug,
								 @RequestParam(value="asNeeded", required=false) String asNeeded,
								 @RequestParam(value="classification", required=false) Integer classification,
								 @RequestParam(value="indication", required=false) Integer indication,
								 @RequestParam(value="instructions", required=false) String instructions,
								 @RequestParam(value="adminInstructions", required=false) String adminInstructions,
								 @RequestParam(value="returnPage", required=true) String returnPage) {
		
		ExtendedDrugOrder drugOrder = Context.getService(OrderExtensionService.class).getExtendedDrugOrder(orderId);
		
		drugOrder = updateDrugOrder(drugOrder, drugId, dose, frequencyDay, frequencyWeek, startDateDrug, stopDateDrug, asNeeded, classification, indication, instructions, adminInstructions);
		
		Context.getService(OrderExtensionService.class).saveExtendedDrugOrder(drugOrder);
	
		return "redirect:"+returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/stopDrug")
	public String stopDrug(ModelMap model, 
								 @RequestParam(value="orderId", required=true) Integer orderId,
								 @RequestParam(value="drugStopDate", required=false) Date stopDate,
								 @RequestParam(value="drugStopReason", required=false) Integer stopReason,
								 @RequestParam(value="returnPage", required=true) String returnPage) {
		
		ExtendedDrugOrder drugOrder = Context.getService(OrderExtensionService.class).getExtendedDrugOrder(orderId);
		
		Context.getOrderService().discontinueOrder(drugOrder, Context.getConceptService().getConcept(stopReason), stopDate);
		
		return "redirect:"+returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/deleteDrug")
	public String deleteDrug(ModelMap model, 
								 @RequestParam(value="orderId", required=true) Integer orderId,
								 @RequestParam(value="deleteReason", required=false) String voidReason,
								 @RequestParam(value="returnPage", required=true) String returnPage) {
		
		ExtendedDrugOrder drugOrder = Context.getService(OrderExtensionService.class).getExtendedDrugOrder(orderId);
		
		Context.getOrderService().voidOrder(drugOrder, voidReason);
		
		return "redirect:"+returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/deleteAllDrugsInGroup")
	public String deleteAllDrugsInGroup(ModelMap model, 
								 @RequestParam(value="groupId", required=true) Integer groupId,
								 @RequestParam(value="deleteReason", required=false) String voidReason,
								 @RequestParam(value="returnPage", required=true) String returnPage) {
		
		DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(groupId);
		
		for(ExtendedDrugOrder order: regimen.getMembers())
		{
			Context.getOrderService().voidOrder(order, voidReason);
		}
		
		return "redirect:"+returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/stopAllDrugsInGroup")
	public String stopAllDrugsInGroup(ModelMap model, 
								 @RequestParam(value="groupId", required=true) Integer groupId,
								 @RequestParam(value="drugStopDate", required=false) Date stopDate,
								 @RequestParam(value="drugStopReason", required=false) Integer stopReason,
								 @RequestParam(value="returnPage", required=true) String returnPage) {
		
		DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(groupId);
		
		Concept stopConcept = Context.getConceptService().getConcept(stopReason);
		
		for(ExtendedDrugOrder order: regimen.getMembers())
		{
			Context.getOrderService().discontinueOrder(order, stopConcept, stopDate);
		}
		
		return "redirect:"+returnPage;
	}
}
