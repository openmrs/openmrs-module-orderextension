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
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
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
		
		DrugOrder drugOrder = setUpDrugOrder(patientId, drugId, dose, frequencyDay, frequencyWeek, startDateDrug, stopDateDrug, asNeeded, classification, indication, instructions, adminInstructions);
	
		Context.getOrderService().saveOrder(drugOrder);
	
		return "redirect:"+returnPage;
	}
	
	private DrugOrder setUpDrugOrder(Integer patientId, Integer drugId, Double dose, String frequencyDay, String frequencyWeek, Date startDateDrug, Date stopDateDrug, String asNeeded, Integer classification, Integer indication, String instructions, String adminInstructions)
	{
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		DrugOrder drugOrder = new ExtendedDrugOrder();
		drugOrder.setPatient(patient);
		
		drugOrder = updateDrugOrder(drugOrder, drugId, dose, frequencyDay, frequencyWeek, startDateDrug, stopDateDrug, asNeeded, classification, indication, instructions, adminInstructions);
		
		return drugOrder;
	}
	
	private DrugOrder updateDrugOrder(DrugOrder drugOrder, Integer drugId, Double dose, String frequencyDay, String frequencyWeek, Date startDateDrug, Date stopDateDrug, String asNeeded, Integer classification, Integer indication, String instructions, String adminInstructions)
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
		if(asNeeded != null)
		{
			drugOrder.setPrn(true);
		}
		else
		{
			drugOrder.setPrn(false);
		}
		
		if(drugOrder instanceof ExtendedDrugOrder)
		{
			ExtendedDrugOrder eDrugOrder = (ExtendedDrugOrder)drugOrder;
			if(classification != null)
			{
				eDrugOrder.setIndication(Context.getConceptService().getConcept(classification));
			}
			else if(indication != null)
			{
				eDrugOrder.setIndication(Context.getConceptService().getConcept(indication));
			}
			else {
				eDrugOrder.setIndication(null);
			}
			
			eDrugOrder.setAdministrationInstructions(adminInstructions);
			
		}
		
		drugOrder.setInstructions(instructions);
		
		if(drugOrder.isDiscontinuedRightNow())
		{
			drugOrder.setDiscontinuedDate(stopDateDrug);
		}
		else{
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
								 @RequestParam(value="repeatCycles", required=false) String repeatCycle,
								 @RequestParam(value="returnPage", required=true) String returnPage) {
		
		DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(groupId);
		
		if(repeatCycle != null)
		{
			Patient patient = Context.getPatientService().getPatient(patientId);
			List<DrugRegimen> futureOrders = Context.getService(OrderExtensionService.class).getFutureDrugRegimensOfSameOrderSet(patient, regimen, regimen.getFirstDrugOrderStartDate());
			
			for(DrugRegimen drugRegimen: futureOrders)
			{	
				Date startDate = adjustDate(drugRegimen.getFirstDrugOrderStartDate(), regimen.getFirstDrugOrderStartDate(), startDateDrug);
				
				Date stopDate = null;
				if(stopDateDrug != null)
				{
					stopDate = adjustDate(drugRegimen.getFirstDrugOrderStartDate(), regimen.getFirstDrugOrderStartDate(), stopDateDrug);
				}
				
				ExtendedDrugOrder drugOrder = (ExtendedDrugOrder)setUpDrugOrder(patientId, drugId, dose, frequencyDay, frequencyWeek, startDate, stopDate, asNeeded, classification, indication, instructions, adminInstructions);
				drugRegimen.addMember(drugOrder);
				
				Context.getService(OrderExtensionService.class).saveOrderGroup(drugRegimen);
			}
		}
		
		ExtendedDrugOrder drugOrder = (ExtendedDrugOrder)setUpDrugOrder(patientId, drugId, dose, frequencyDay, frequencyWeek, startDateDrug, stopDateDrug, asNeeded, classification, indication, instructions, adminInstructions);
		regimen.addMember(drugOrder);
		
		Context.getService(OrderExtensionService.class).saveOrderGroup(regimen);
	
		return "redirect:"+returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/changeStartDateOfGroup")
	public String changeStartDateOfGroup(ModelMap model, 
								 @RequestParam(value="patientId", required=true) Integer patientId,
								 @RequestParam(value="groupId", required=true) Integer groupId,
								 @RequestParam(value="changeDate", required=true) Date changeDate,
								 @RequestParam(value="repeatCycles", required=false) String repeatCycle,
								 @RequestParam(value="returnPage", required=true) String returnPage) {
		
		DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(groupId);
		
		if(repeatCycle != null)
		{
			Patient patient = Context.getPatientService().getPatient(patientId);
			List<DrugRegimen> futureOrders = Context.getService(OrderExtensionService.class).getFutureDrugRegimensOfSameOrderSet(patient, regimen, regimen.getFirstDrugOrderStartDate());
			
			for(DrugRegimen drugRegimen: futureOrders)
			{	
				Date startDate = adjustDate(drugRegimen.getFirstDrugOrderStartDate(), regimen.getFirstDrugOrderStartDate(), changeDate);
				
				for(ExtendedDrugOrder order: drugRegimen.getMembers())
				{ 
					if(order.getAutoExpireDate() != null)
					{
						order.setAutoExpireDate(adjustDate(order.getAutoExpireDate(), regimen.getFirstDrugOrderStartDate(), changeDate));
					}
					
					order.setStartDate(adjustDate(order.getStartDate(), regimen.getFirstDrugOrderStartDate(), changeDate));
					Context.getOrderService().saveOrder(order);
				}
			}
		}
		
		Date sDate = regimen.getFirstDrugOrderStartDate();
		for(ExtendedDrugOrder order: regimen.getMembers())
		{ 
			if(order.getAutoExpireDate() != null)
			{
				order.setAutoExpireDate(adjustDate(order.getAutoExpireDate(), sDate, changeDate));
			}
			
			order.setStartDate(adjustDate(order.getStartDate(), sDate, changeDate));
			Context.getOrderService().saveOrder(order);
		}
		
	
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
								 @RequestParam(value="repeatCycles", required=false) String repeatCycle,
								 @RequestParam(value="returnPage", required=true) String returnPage,
								 @RequestParam(value="patientId", required=true) Integer patientId) {
		
 		DrugOrder o = Context.getOrderService().getOrder(orderId, DrugOrder.class);
		
		if(o instanceof ExtendedDrugOrder)
		{
			ExtendedDrugOrder drugOrder = (ExtendedDrugOrder)o;
		
			if(repeatCycle != null)
			{
				DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(drugOrder.getGroup().getId());
				Patient patient = Context.getPatientService().getPatient(patientId);
				List<ExtendedDrugOrder> futureOrders = Context.getService(OrderExtensionService.class).getFutureDrugOrdersOfSameOrderSet(patient, regimen.getOrderSet(), regimen.getFirstDrugOrderStartDate());
				
				for(ExtendedDrugOrder order: futureOrders)
				{
					if((order.getDrug() != null && drugOrder.getDrug() != null && order.getDrug().equals(drugOrder.getDrug())) || (order.getConcept() != null && drugOrder.getConcept() != null && order.getConcept().equals(drugOrder.getConcept())))
					{
						//assuming that the same drug won't appear twice in the same indication within a cycle and that you would want to change the dose on one 
						if((order.getIndication() == null && drugOrder.getIndication() == null) || (order.getIndication() != null && drugOrder.getIndication() != null && drugOrder.getIndication().equals(order.getIndication())))
						{
							Date startDate = order.getStartDate();
							Date endDate = order.getAutoExpireDate();
							if(drugOrder.getStartDate().getTime() != startDateDrug.getTime())
							{
								startDate = adjustDate(startDate, drugOrder.getStartDate(), startDateDrug);
							}
							if(drugOrder.getAutoExpireDate() == null && stopDateDrug != null)
							{
								endDate = stopDateDrug;
							}
							else if(drugOrder.getAutoExpireDate() != null && stopDateDrug == null)
							{
								endDate = null;
							}
							else if(drugOrder.getAutoExpireDate() != null && stopDateDrug != null && drugOrder.getAutoExpireDate().getTime() != stopDateDrug.getTime())
							{
								endDate = adjustDate(endDate, drugOrder.getAutoExpireDate(), stopDateDrug);
							}
							
							DrugOrder orderDrug = updateDrugOrder(order, drugId, dose, frequencyDay, frequencyWeek, startDate, endDate, asNeeded, classification, indication, instructions, adminInstructions);
							Context.getOrderService().saveOrder(orderDrug);
						}
					}
				}
			}
		}
		
		o = updateDrugOrder(o, drugId, dose, frequencyDay, frequencyWeek, startDateDrug, stopDateDrug, asNeeded, classification, indication, instructions, adminInstructions);
		
		Context.getOrderService().saveOrder(o);
	
		return "redirect:"+returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/stopDrug")
	public String stopDrug(ModelMap model, 
								 @RequestParam(value="orderId", required=true) Integer orderId,
								 @RequestParam(value="drugStopDate", required=true) Date stopDate,
								 @RequestParam(value="drugStopReason", required=true) Integer stopReason,
								 @RequestParam(value="repeatCycles", required=false) String repeatCycle,
								 @RequestParam(value="returnPage", required=true) String returnPage,
								 @RequestParam(value="patientId", required=true) Integer patientId) {
		
		Concept stopConcept = Context.getConceptService().getConcept(stopReason);
		
		Order o = Context.getOrderService().getOrder(orderId, DrugOrder.class);
		
		if(o instanceof ExtendedDrugOrder)
		{
			ExtendedDrugOrder drugOrder = (ExtendedDrugOrder)o;
		
			if(repeatCycle != null)
			{
				DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(drugOrder.getGroup().getId());
				Patient patient = Context.getPatientService().getPatient(patientId);
				List<ExtendedDrugOrder> futureOrders = Context.getService(OrderExtensionService.class).getFutureDrugOrdersOfSameOrderSet(patient, regimen.getOrderSet(), regimen.getFirstDrugOrderStartDate());
				
				for(ExtendedDrugOrder order: futureOrders)
				{
					if(order.getDrug() != null && drugOrder.getDrug() != null)
					{
						if(order.getDrug().equals(drugOrder.getDrug()))
						{
							Context.getOrderService().voidOrder(order, stopConcept.getDisplayString());
						}
					}
					else if(order.getConcept() != null && drugOrder.getConcept() != null)
					{
						if(order.getConcept().equals(drugOrder.getConcept()))
						{
							Context.getOrderService().voidOrder(order, stopConcept.getDisplayString());
						}
					}
				}
			}
		}
		
		Context.getOrderService().discontinueOrder(o, stopConcept, stopDate);
		
		return "redirect:"+returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/deleteDrug")
	public String deleteDrug(ModelMap model, 
								 @RequestParam(value="orderId", required=true) Integer orderId,
								 @RequestParam(value="deleteReason", required=true) String voidReason,
								 @RequestParam(value="repeatCycles", required=false) String repeatCycle,
								 @RequestParam(value="returnPage", required=true) String returnPage,
								 @RequestParam(value="patientId", required=true) Integer patientId) {
		
		DrugOrder o = Context.getOrderService().getOrder(orderId, DrugOrder.class);
		
		if(o instanceof ExtendedDrugOrder)
		{
			ExtendedDrugOrder drugOrder = (ExtendedDrugOrder)o;
			
			if(repeatCycle != null)
			{
				DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(drugOrder.getGroup().getId());
				Patient patient = Context.getPatientService().getPatient(patientId);
				List<ExtendedDrugOrder> futureOrders = Context.getService(OrderExtensionService.class).getFutureDrugOrdersOfSameOrderSet(patient, regimen.getOrderSet(), regimen.getFirstDrugOrderStartDate());
				
				for(ExtendedDrugOrder order: futureOrders)
				{
					if(order.getDrug() != null && drugOrder.getDrug() != null)
					{
						if(order.getDrug().equals(drugOrder.getDrug()))
						{
							Context.getOrderService().voidOrder(order, voidReason);
						}
					}
					else if(order.getConcept() != null && drugOrder.getConcept() != null)
					{
						if(order.getConcept().equals(drugOrder.getConcept()))
						{
							Context.getOrderService().voidOrder(order, voidReason);
						}
					}
				}
			}
		}
		
		Context.getOrderService().voidOrder(o, voidReason);
		
		return "redirect:"+returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/deleteAllDrugsInGroup")
	public String deleteAllDrugsInGroup(ModelMap model, 
								 @RequestParam(value="groupId", required=true) Integer groupId,
								 @RequestParam(value="deleteReason", required=true) String voidReason,
								 @RequestParam(value="repeatCycles", required=false) String repeatCycle,
								 @RequestParam(value="returnPage", required=true) String returnPage,
								 @RequestParam(value="patientId", required=true) Integer patientId) {
		
		DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(groupId);
		
		if(repeatCycle != null)
		{
			Patient patient = Context.getPatientService().getPatient(patientId);
			List<ExtendedDrugOrder> futureOrders = Context.getService(OrderExtensionService.class).getFutureDrugOrdersOfSameOrderSet(patient, regimen.getOrderSet(), regimen.getLastDrugOrderEndDate());
			
			for(ExtendedDrugOrder order: futureOrders)
			{
				Context.getOrderService().voidOrder(order, voidReason);
			}
		}
		
		for(ExtendedDrugOrder order: regimen.getMembers())
		{
			Context.getOrderService().voidOrder(order, voidReason);
		}
		
		return "redirect:"+returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/stopAllDrugsInGroup")
	public String stopAllDrugsInGroup(ModelMap model, 
								 @RequestParam(value="groupId", required=true) Integer groupId,
								 @RequestParam(value="drugStopAllDate", required=true) Date stopDate,
								 @RequestParam(value="drugStopAllReason", required=true) Integer stopReason,
								 @RequestParam(value="repeatCycles", required=false) String repeatCycle,
								 @RequestParam(value="returnPage", required=true) String returnPage,
								 @RequestParam(value="patientId", required=true) Integer patientId) {
		
		DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(groupId);
		
		Concept stopConcept = Context.getConceptService().getConcept(stopReason);
		
		if(repeatCycle != null)
		{
			Patient patient = Context.getPatientService().getPatient(patientId);
			List<ExtendedDrugOrder> futureOrders = Context.getService(OrderExtensionService.class).getFutureDrugOrdersOfSameOrderSet(patient, regimen.getOrderSet(), regimen.getLastDrugOrderEndDate());
			
			for(ExtendedDrugOrder order: futureOrders)
			{
				Context.getOrderService().voidOrder(order, stopConcept.getDisplayString());
			}
		}
		
		for(ExtendedDrugOrder order: regimen.getMembers())
		{
			Context.getOrderService().discontinueOrder(order, stopConcept, stopDate);
		}
		
		return "redirect:"+returnPage;
	}
	
	private Date adjustDate(Date dateToAdjust, Date startDateComparison, Date endDateComparison)
	{
		long milis2 = startDateComparison.getTime();
		long milis1 = endDateComparison.getTime();
		
		long diff = milis1 - milis2;
		
		long diffDays = diff / (24 * 60 * 60 * 1000);
	
		Calendar adjusted = Calendar.getInstance();
		adjusted.setTime(dateToAdjust);
		adjusted.add(Calendar.DAY_OF_YEAR, (int)diffDays);
		return adjusted.getTime();
	}
}
