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
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.module.orderextension.util.DrugOrderTemplate;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.orderextension.util.OrderFrequencyEditor;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Primary Controller for administering orders
 */
@Controller
public class OrderExtensionOrderController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	private OrderExtensionService getOrderExtensionService() {
		return Context.getService(OrderExtensionService.class);
	}

	/**
	 * Registers any needed property editors
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder)    {
		binder.registerCustomEditor(OrderFrequency.class, new OrderFrequencyEditor());
	}

	/**
	 * Shows the page to list order sets
	 */
	@RequestMapping(value = "/module/orderextension/orderList")
	public void listOrders(ModelMap model, @RequestParam(value = "patientId") Integer patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		model.addAttribute("patient", patient);

		List<DrugRegimen> regimens = getOrderExtensionService().getDrugRegimens(patient);
		model.addAttribute("regimens", regimens);

		List<DrugOrder> drugOrders = OrderEntryUtil.getDrugOrdersByPatient(patient);
		for (DrugRegimen r : regimens) {
			drugOrders.removeAll(r.getMembers());
		}
		model.addAttribute("drugOrders", drugOrders);
		
		model.addAttribute("orderSets", getOrderExtensionService().getNamedOrderSets(false));
	}
	
	/**
	 * adds a new orderSet
	 */
	@RequestMapping(value = "/module/orderextension/addOrdersFromSet")
	public String addOrderSet(WebRequest request,
							  @RequestParam(value = "patientId") Patient patient,
	                          @RequestParam(value = "orderSet") Integer orderSetId,
	                          @RequestParam(value = "startDateSet") Date startDateSet,
	                          @RequestParam(value = "numCycles", required = false) Integer numCycles,
	                          @RequestParam(value = "returnPage") String returnPage) {

		try {
			ExtendedOrderSet orderSet = getOrderExtensionService().getOrderSet(orderSetId);
			getOrderExtensionService().addOrdersForPatient(patient, orderSet, startDateSet, numCycles);
		}
		catch (Exception e) {
			log.error("Unable to add orders from order set:", e);
			updateSessionMessage(request, e);
		}
		return "redirect:" + returnPage;
	}
	
	/**
	 * adds a new order
	 */
	@RequestMapping(value = "/module/orderextension/addDrugOrder")
	public String addOrder(WebRequest request,
						   @RequestParam(value = "patientId") Patient patient,
	                       @RequestParam(value = "drug") Drug drug,
	                       @RequestParam(value = "dose") Double dose,
	                       @RequestParam(value = "doseUnits") Concept doseUnits,
						   @RequestParam(value = "frequency") OrderFrequency frequency,
	                       @RequestParam(value = "startDateDrug") Date startDateDrug,
	                       @RequestParam(value = "duration", required = false) Integer duration,
	                       @RequestParam(value = "asNeeded", required = false) String asNeeded,
						   @RequestParam(value = "route") Concept route,
	                       @RequestParam(value = "classification", required = false) Concept classification,
	                       @RequestParam(value = "indication", required = false) Concept indication,
	                       @RequestParam(value = "instructions", required = false) String instructions,
	                       @RequestParam(value = "adminInstructions", required = false) String adminInstructions,
	                       @RequestParam(value = "returnPage") String returnPage) {

		try {
			Concept orderReason = (classification != null ? classification : indication);
			DrugOrderTemplate drugOrderTemplate = new DrugOrderTemplate(
					patient, drug, dose, doseUnits, frequency, startDateDrug, duration, (asNeeded != null), route,
					orderReason, instructions, adminInstructions, null
			);
			getOrderExtensionService().extendedSaveDrugOrder(drugOrderTemplate, false);
		}
		catch (Exception e) {
			log.error("Unable to save drug order:", e);
			updateSessionMessage(request, e);
		}
		
		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/addDrugOrderToGroup")
	public String addDrugOrderToGroup(WebRequest request,
									  @RequestParam(value = "patientId")  Patient patient,
	                                  @RequestParam(value = "groupId") Integer groupId,
	                                  @RequestParam(value = "drug") Drug drug,
	                                  @RequestParam(value = "dose") Double dose,
									  @RequestParam(value = "doseUnits") Concept doseUnits,
	                                  @RequestParam(value = "frequency") OrderFrequency frequency,
	                                  @RequestParam(value = "addCycleStartDate") Date startDateDrug,
	                                  @RequestParam(value = "duration", required = false) Integer duration,
	                                  @RequestParam(value = "asNeeded", required = false) String asNeeded,
									  @RequestParam(value = "route") Concept route,
	                                  @RequestParam(value = "classification", required = false) Concept classification,
	                                  @RequestParam(value = "indication", required = false) Concept indication,
	                                  @RequestParam(value = "instructions", required = false) String instructions,
	                                  @RequestParam(value = "adminInstructions", required = false) String adminInstructions,
	                                  @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                                  @RequestParam(value = "returnPage") String returnPage) {

		try {
			DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(groupId);
			Concept orderReason = (classification != null ? classification : indication);

			DrugOrderTemplate drugOrderTemplate = new DrugOrderTemplate(
					patient, drug, dose, doseUnits, frequency, startDateDrug, duration, (asNeeded != null), route,
					orderReason, instructions, adminInstructions, regimen
			);
			boolean includeCycles = (repeatCycle != null);

			getOrderExtensionService().extendedSaveDrugOrder(drugOrderTemplate, includeCycles);
		}
		catch (Exception e) {
			log.error("Unable to add drug order to regimen:", e);
			updateSessionMessage(request, e);
		}
		
		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/changeStartDateOfGroup")
	public String changeStartDateOfGroup(WebRequest request,
	                                     @RequestParam(value = "patientId") Patient patient,
	                                     @RequestParam(value = "groupId") Integer groupId,
	                                     @RequestParam(value = "changeDate") Date changeDate,
	                                     @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                                     @RequestParam(value = "returnPage") String returnPage) {

		try {
			DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(groupId);
			boolean includeCycles = (repeatCycle != null);
			getOrderExtensionService().changeStartDateOfGroup(patient, regimen, changeDate, includeCycles);
		}
		catch (Exception e) {
			log.error("Unable to change the start date of the group:", e);
			updateSessionMessage(request, e);
		}
		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/changeStartDateOfPartGroup")
	public String changeStartDateOfPartGroup(WebRequest request,
	                                         @RequestParam(value = "patientId") Patient patient,
	                                         @RequestParam(value = "groupId") Integer groupId,
	                                         @RequestParam(value = "changePartDate") Date changeDate,
	                                         @RequestParam(value = "cycleDay") String cycleDayString,
	                                         @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                                         @RequestParam(value = "repeatPartCycles", required = false) String repeatPartCycles,
	                                         @RequestParam(value = "repeatThisCycle", required = false) String repeatThisCycle,
	                                         @RequestParam(value = "returnPage") String returnPage) {

		try {
			DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(groupId);
			Integer cycleDay = Integer.parseInt(cycleDayString);
			boolean includeCycles = (repeatCycle != null);
			boolean includePartCycles = (repeatPartCycles != null);
			boolean includeThisCycle = (repeatThisCycle != null);

			getOrderExtensionService().changeStartDateOfPartGroup(
					patient, regimen, changeDate, cycleDay, includeCycles, includePartCycles, includeThisCycle
			);
		}
		catch (Exception e) {
			log.error("Unable to change the start date of the group:", e);
			updateSessionMessage(request, e);
		}

		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/editDrug")
	public String editDrug(WebRequest request,
						   @RequestParam(value = "patientId") Patient patient,
						   @RequestParam(value = "orderId") Integer orderId,
	                       @RequestParam(value = "drug") Drug drug,
	                       @RequestParam(value = "dose") Double dose,
						   @RequestParam(value = "doseUnits") Concept doseUnits,
	                       @RequestParam(value = "frequency") OrderFrequency frequency,
	                       @RequestParam(value = "editStartDate") Date startDateDrug,
	                       @RequestParam(value = "editDuration", required = false) Integer duration,
	                       @RequestParam(value = "asNeeded", required = false) String asNeededStr,
						   @RequestParam(value = "route", required = false) Concept route,
	                       @RequestParam(value = "classification", required = false) Concept classification,
	                       @RequestParam(value = "indication", required = false) Concept indication,
	                       @RequestParam(value = "instructions", required = false) String instructions,
	                       @RequestParam(value = "adminInstructions", required = false) String adminInstructions,
	                       @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                       @RequestParam(value = "drugChangeReason", required = false) String changeReason,
						   @RequestParam(value = "returnPage") String returnPage
	                       ) {

		try {
			DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrder(orderId);
			boolean asNeeded = (asNeededStr != null);
			changeReason = StringUtils.isEmpty(changeReason) ? "Edit Drug Order" : changeReason;
			Concept orderReason = (classification != null ? classification : indication);
			boolean includeCycles = (repeatCycle != null);
			DrugRegimen regimen = (DrugRegimen)OrderEntryUtil.getOrderGroup(drugOrder);

			DrugOrderTemplate orderTemplate = new DrugOrderTemplate(
					patient, drug, dose, doseUnits, frequency, startDateDrug, duration, asNeeded,
					route,  orderReason, instructions, adminInstructions, regimen
			);

			getOrderExtensionService().changeDrugOrder(drugOrder, orderTemplate, changeReason, includeCycles);
		}
		catch (Exception e) {
			log.error("Unable to Edit Drug Order:", e);
			updateSessionMessage(request, e);
		}
		return "redirect:" + returnPage;
	}

	@RequestMapping(value = "/module/orderextension/stopDrug")
	public String stopDrug(WebRequest request,
			               @RequestParam(value = "patientId") Patient patient,
						   @RequestParam(value = "orderId") Integer orderId,
	                       @RequestParam(value = "drugStopDate") Date stopDate,
	                       @RequestParam(value = "drugStopReason") Concept reasonForStopping,
	                       @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                       @RequestParam(value = "returnPage") String returnPage) {

		try {
			DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrder(orderId);
			boolean includeCycles = (repeatCycle != null);
			getOrderExtensionService().stopDrugOrder(patient, drugOrder, stopDate, reasonForStopping, includeCycles);
		}
		catch (Exception e) {
			log.error("Unable to discontinue drug order:", e);
			updateSessionMessage(request, e);
		}
		
		return "redirect:" + returnPage;
	}

	@RequestMapping(value = "/module/orderextension/stopAllDrugsInGroup")
	public String stopAllDrugsInGroup(WebRequest request,
			@RequestParam(value = "patientId") Patient patient,
			@RequestParam(value = "groupId") Integer groupId,
			@RequestParam(value = "drugStopAllDate") Date stopDate,
			@RequestParam(value = "drugStopAllReason") Concept reasonForStopping,
			@RequestParam(value = "repeatCycles", required = false) String repeatCycle,
			@RequestParam(value = "returnPage") String returnPage) {

		try {
			DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(groupId);
			boolean includeCycles = (repeatCycle != null);
			getOrderExtensionService().stopDrugOrdersInGroup(patient, regimen, stopDate, reasonForStopping, includeCycles);
		}
		catch (Exception e) {
			log.error("Unable to delete all orders in the order group", e);
			updateSessionMessage(request, e);
		}

		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/deleteDrug")
	public String deleteDrug(WebRequest request,
							 @RequestParam(value = "patientId") Patient patient,
							 @RequestParam(value = "orderId") Integer orderId,
	                         @RequestParam(value = "deleteReason") String voidReason,
	                         @RequestParam(value = "deleteReasonDescription", required = false) String voidReasonDescription,
	                         @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                         @RequestParam(value = "returnPage") String returnPage) {

		try {
			DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrder(orderId);
			String reason = voidReason + (StringUtils.hasText(voidReasonDescription) ? " " + voidReasonDescription : "");
			boolean includeCycles = (repeatCycle != null);
			getOrderExtensionService().voidDrugOrder(patient, drugOrder, reason, includeCycles);
		}
		catch (Exception e) {
			log.error("Unable to delete Drug Order:", e);
			updateSessionMessage(request, e);
		}
		
		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/deleteAllDrugsInGroup")
	public String deleteAllDrugsInGroup(WebRequest request,
									    @RequestParam(value = "patientId") Patient patient,
									    @RequestParam(value = "groupId") Integer groupId,
	                                    @RequestParam(value = "deleteReason") String voidReason,
	       	                         	@RequestParam(value = "deleteAllReasonDescription", required = false) String voidReasonDescription,
	       	                         	@RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                                    @RequestParam(value = "returnPage") String returnPage) {
		
		try {
			DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(groupId);
			String reason = voidReason + (StringUtils.hasText(voidReasonDescription) ? " " + voidReasonDescription : "");
			boolean includeCycles = (repeatCycle != null);
			getOrderExtensionService().voidDrugOrdersInGroup(patient, regimen, reason, includeCycles);
		}
		catch (Exception e) {
			log.error("Unable to delete drug order group:", e);
			updateSessionMessage(request, e);
		}
		
		return "redirect:" + returnPage;
	}

	private void updateSessionMessage(WebRequest request, Exception e) {
		String msg = Context.getMessageSourceService().getMessage("error.general", null, Context.getLocale());
		msg += ": " + e.getMessage();
		request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, msg, WebRequest.SCOPE_SESSION);
	}
}
