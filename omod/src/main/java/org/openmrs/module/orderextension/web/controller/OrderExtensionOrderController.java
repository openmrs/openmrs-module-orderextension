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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.orderextension.util.OrderExtensionUtil;
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
	public void listOrders(ModelMap model, @RequestParam(value = "patientId", required = true) Integer patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		model.addAttribute("patient", patient);

		List<DrugRegimen> regimens = getOrderExtensionService().getDrugRegimens(patient);
		model.addAttribute("regimens", regimens);

		List<DrugOrder> drugOrders = OrderEntryUtil.getDrugOrdersByPatient(patient);
		for (DrugRegimen r : regimens) {
			drugOrders.removeAll(r.getOrders());
		}
		model.addAttribute("drugOrders", drugOrders);
		
		model.addAttribute("orderSets", getOrderExtensionService().getNamedOrderSets(false));
	}
	
	/**
	 * adds a new orderSet
	 */
	@RequestMapping(value = "/module/orderextension/addOrdersFromSet")
	public String addOrderSet(ModelMap model, @RequestParam(value = "patientId", required = true) Integer patientId,
	                          @RequestParam(value = "orderSet", required = true) Integer orderSetId,
	                          @RequestParam(value = "startDateSet", required = true) Date startDateSet,
	                          @RequestParam(value = "numCycles", required = false) Integer numCycles,
	                          @RequestParam(value = "returnPage", required = true) String returnPage) {

		Patient patient = Context.getPatientService().getPatient(patientId);
		ExtendedOrderSet orderSet = getOrderExtensionService().getOrderSet(orderSetId);
		getOrderExtensionService().addOrdersForPatient(patient, orderSet, startDateSet, numCycles);
		return "redirect:" + returnPage;
	}
	
	/**
	 * adds a new order
	 */
	@RequestMapping(value = "/module/orderextension/addDrugOrder")
	public String addOrder(ModelMap model, @RequestParam(value = "patientId", required = true) Integer patientId,
	                       @RequestParam(value = "drug", required = true) Drug drug,
	                       @RequestParam(value = "dose", required = true) Double dose,
	                       @RequestParam(value = "doseUnits", required = true) Concept doseUnits,
						   @RequestParam(value = "frequency", required = true) OrderFrequency frequency,
	                       @RequestParam(value = "startDateDrug", required = true) Date startDateDrug,
	                       @RequestParam(value = "duration", required = false) Integer duration,
	                       @RequestParam(value = "asNeeded", required = false) String asNeeded,
						   @RequestParam(value = "route", required = true) Concept route,
	                       @RequestParam(value = "classification", required = false) Integer classification,
	                       @RequestParam(value = "indication", required = false) Integer indication,
	                       @RequestParam(value = "instructions", required = false) String instructions,
	                       @RequestParam(value = "adminInstructions", required = false) String adminInstructions,
	                       @RequestParam(value = "returnPage", required = true) String returnPage) {
		
		DrugOrder drugOrder = setUpDrugOrder(patientId, drug, dose, doseUnits, frequency, startDateDrug,
		    duration, asNeeded, route, classification, indication, instructions, adminInstructions);

		getOrderExtensionService().extendedSaveDrugOrder(drugOrder);
		
		return "redirect:" + returnPage;
	}
	
	private DrugOrder setUpDrugOrder(Integer patientId, Drug drug, Double dose, Concept doseUnits,
									 OrderFrequency frequency,Date startDateDrug, Integer duration, String asNeeded,
	                                 Concept route, Integer classification, Integer indication, String instructions,
	                                 String adminInstructions) {

		DrugOrder drugOrder = new DrugOrder();
		drugOrder.setPatient(Context.getPatientService().getPatient(patientId));
		
		drugOrder = updateDrugOrder(
				drugOrder, drug, dose, doseUnits, frequency, startDateDrug, duration,
		        asNeeded, route, classification, indication, instructions, adminInstructions
		);
		
		return drugOrder;
	}

	private DrugOrder updateDrugOrder(DrugOrder drugOrder, Drug drug, Double dose, Concept doseUnits,
			OrderFrequency frequency, Date startDateDrug, Integer duration, String asNeeded,
			Concept route, Integer classification, Integer indication, String instructions,
			String adminInstructions) {

		if (drugOrder.getOrderType() == null) {
			drugOrder.setOrderType(OrderEntryUtil.getDrugOrderType());
		}
		drugOrder.setDrug(drug);
		drugOrder.setConcept(drug.getConcept());
		drugOrder.setDose(dose);
		drugOrder.setDoseUnits(doseUnits);
		drugOrder.setFrequency(frequency);
		OrderEntryUtil.setStartDate(drugOrder, startDateDrug);
		OrderEntryUtil.setEndDate(drugOrder, duration);
		drugOrder.setAsNeeded((asNeeded != null));
		drugOrder.setRoute(route);

		if (classification != null) {
			drugOrder.setOrderReason(Context.getConceptService().getConcept(classification));
		}
		else if (indication != null) {
			drugOrder.setOrderReason(Context.getConceptService().getConcept(indication));
		}
		else {
			drugOrder.setOrderReason(null);
		}

		drugOrder.setDosingInstructions(adminInstructions);
		drugOrder.setInstructions(instructions);

		return drugOrder;
	}
	
	@RequestMapping(value = "/module/orderextension/addDrugOrderToGroup")
	public String addDrugOrderToGroup(ModelMap model, @RequestParam(value = "patientId", required = true) Integer patientId,
	                                  @RequestParam(value = "groupId", required = true) Integer groupId,
	                                  @RequestParam(value = "drug", required = true) Drug drug,
	                                  @RequestParam(value = "dose", required = true) Double dose,
									  @RequestParam(value = "doseUits", required = true) Concept doseUnits,
	                                  @RequestParam(value = "frequency", required = true) OrderFrequency frequency,
	                                  @RequestParam(value = "addCycleStartDate", required = true) Date startDateDrug,
	                                  @RequestParam(value = "duration", required = false) Integer duration,
	                                  @RequestParam(value = "asNeeded", required = false) String asNeeded,
									  @RequestParam(value = "route", required = true) Concept route,
	                                  @RequestParam(value = "classification", required = false) Integer classification,
	                                  @RequestParam(value = "indication", required = false) Integer indication,
	                                  @RequestParam(value = "instructions", required = false) String instructions,
	                                  @RequestParam(value = "adminInstructions", required = false) String adminInstructions,
	                                  @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                                  @RequestParam(value = "returnPage", required = true) String returnPage) {
		
		DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(groupId);
		
		if (repeatCycle != null) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			List<DrugRegimen> futureOrders = getOrderExtensionService().getFutureDrugRegimensOfSameOrderSet(patient, regimen, regimen.getFirstDrugOrderStartDate());
			
			for (DrugRegimen drugRegimen : futureOrders) {
				Date firstStartDate = drugRegimen.getFirstDrugOrderStartDate();
				Date startDate = adjustDate(firstStartDate, daysDiff(regimen.getFirstDrugOrderStartDate(), startDateDrug));
				DrugOrder drugOrder = setUpDrugOrder(patientId, drug, dose, doseUnits, frequency,
				    startDate, duration, asNeeded, route, classification, indication, instructions,
				    adminInstructions);
				drugRegimen.addOrder(drugOrder);
				
				getOrderExtensionService().saveDrugRegimen(drugRegimen);
			}
		}

		DrugOrder drugOrder = setUpDrugOrder(patientId, drug, dose, doseUnits, frequency,
		    startDateDrug, duration, asNeeded, route, classification, indication, instructions,
		    adminInstructions);
		regimen.addOrder(drugOrder);
		
		getOrderExtensionService().saveDrugRegimen(regimen);
		
		return "redirect:" + returnPage;
	}

	protected DrugOrder cloneAndVoidPrevious(DrugOrder orderToVoid, String reason) {
		DrugOrder newOrder = orderToVoid.cloneForRevision();
		newOrder.setAction(Order.Action.NEW);
		newOrder.setPreviousOrder(null);
		Context.getOrderService().voidOrder(orderToVoid, reason);
		return newOrder;
	}

	protected void updateOrderStartAndEndDates(DrugOrder oldOrder, int daysDiff, String reason) {
		if (daysDiff != 0) {
			DrugOrder newOrder = cloneAndVoidPrevious(oldOrder, reason);
			if (newOrder.getAutoExpireDate() != null) {
				newOrder.setAutoExpireDate(adjustDate(newOrder.getAutoExpireDate(), daysDiff));
			}
			OrderEntryUtil.setStartDate(newOrder, adjustDate(newOrder.getEffectiveStartDate(), daysDiff));
			getOrderExtensionService().extendedSaveDrugOrder(newOrder);
		}
	}
	
	@RequestMapping(value = "/module/orderextension/changeStartDateOfGroup")
	public String changeStartDateOfGroup(ModelMap model,
	                                     @RequestParam(value = "patientId", required = true) Integer patientId,
	                                     @RequestParam(value = "groupId", required = true) Integer groupId,
	                                     @RequestParam(value = "changeDate", required = true) Date changeDate,
	                                     @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                                     @RequestParam(value = "returnPage", required = true) String returnPage) {
		
		DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(groupId);
		Date firstDate = regimen.getFirstDrugOrderStartDate();
		int daysDiff = daysDiff(firstDate, changeDate);

		if (daysDiff != 0) {
			if (repeatCycle != null) {
				Patient patient = Context.getPatientService().getPatient(patientId);
				List<DrugRegimen> futureOrders = getOrderExtensionService().getFutureDrugRegimensOfSameOrderSet(
						patient, regimen, firstDate
				);
				for (DrugRegimen drugRegimen : futureOrders) {
					for (DrugOrder order : drugRegimen.getMembers()) {
						updateOrderStartAndEndDates(order, daysDiff, "Changing start date of order group");
					}
				}
			}
			for (DrugOrder order : regimen.getMembers()) {
				updateOrderStartAndEndDates(order, daysDiff, "Changing start date of order group");
			}
		}
		
		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/changeStartDateOfPartGroup")
	public String changeStartDateOfPartGroup(ModelMap model,
	                                         @RequestParam(value = "patientId", required = true) Integer patientId,
	                                         @RequestParam(value = "groupId", required = true) Integer groupId,
	                                         @RequestParam(value = "changePartDate", required = true) Date changeDate,
	                                         @RequestParam(value = "cycleDay", required = true) String cycleDayString,
	                                         @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                                         @RequestParam(value = "repeatPartCycles", required = false) String repeatPartCycles,
	                                         @RequestParam(value = "repeatThisCycle", required = false) String repeatThisCycle,
	                                         @RequestParam(value = "returnPage", required = true) String returnPage) {
		
		DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(groupId);
		
		Integer cycleDay = Integer.parseInt(cycleDayString);
		
		Date startDate = getCycleDate(regimen.getFirstDrugOrderStartDate(), cycleDay);
		int days = daysDiff(startDate, changeDate);
		
		if (repeatCycle != null || repeatPartCycles != null) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			List<DrugRegimen> futureOrders = getOrderExtensionService()
			        .getFutureDrugRegimensOfSameOrderSet(patient, regimen, regimen.getFirstDrugOrderStartDate());
			
			for (DrugRegimen drugRegimen : futureOrders) {
				for (DrugOrder order : drugRegimen.getMembers()) {
					if (repeatCycle == null) {
						if (getCycleDay(drugRegimen.getFirstDrugOrderStartDate(), order.getEffectiveStartDate()) == cycleDay) {
							updateOrderStartAndEndDates(order, days, "Changing start date of part of order group");
						}
					}
					else {
						updateOrderStartAndEndDates(order, days, "Changing start date of part of order group");
					}
				}
			}
		}
		
		for (DrugOrder order : regimen.getMembers()) {
			if (repeatThisCycle != null || repeatPartCycles != null) {
				if (getCycleDay(regimen.getFirstDrugOrderStartDate(), order.getEffectiveStartDate()) >= cycleDay) {
					updateOrderStartAndEndDates(order, days, "Changing start date of part of order group");
				}
			}
			else {
				if (getCycleDay(regimen.getFirstDrugOrderStartDate(), order.getEffectiveStartDate()) == cycleDay) {
					updateOrderStartAndEndDates(order, days, "Changing start date of part of order group");
				}
			}
		}
		
		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/editDrug")
	public String editDrug(ModelMap model, WebRequest request,
						   @RequestParam(value = "orderId", required = true) Integer orderId,
	                       @RequestParam(value = "drug", required = true) Drug drug,
	                       @RequestParam(value = "dose", required = true) Double dose,
						   @RequestParam(value = "doseUnits", required = true) Concept doseUnits,
	                       @RequestParam(value = "frequency", required = true) OrderFrequency frequency,
	                       @RequestParam(value = "editStartDate", required = true) Date startDateDrug,
	                       @RequestParam(value = "editDuration", required = false) Integer duration,
	                       @RequestParam(value = "asNeeded", required = false) String asNeeded,
						   @RequestParam(value = "route", required = false) Concept route,
	                       @RequestParam(value = "classification", required = false) Integer classification,
	                       @RequestParam(value = "indication", required = false) Integer indication,
	                       @RequestParam(value = "instructions", required = false) String instructions,
	                       @RequestParam(value = "adminInstructions", required = false) String adminInstructions,
	                       @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                       @RequestParam(value = "returnPage", required = true) String returnPage,
	                       @RequestParam(value = "drugChangeReason", required = false) String changeReason,
	                       @RequestParam(value = "patientId", required = true) Integer patientId) {

		try {
			DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrder(orderId);
			DrugRegimen regimen = (DrugRegimen) drugOrder.getOrderGroup();

			if (StringUtils.isEmpty(changeReason)) {
				changeReason = "Edit Drug Order";
			}

			if (repeatCycle != null) {
				Patient patient = Context.getPatientService().getPatient(patientId);
				List<DrugOrder> futureOrders = getOrderExtensionService().getFutureDrugOrdersOfSameOrderSet(
						patient, regimen.getOrderSet(), regimen.getFirstDrugOrderStartDate()
				);
				for (DrugOrder order : futureOrders) {
					if (OrderExtensionUtil.orderablesMatch(order, drugOrder)) {
						//assuming that the same drug won't appear twice in the same indication within a cycle and that you would want to change the dose on one
						if (OrderExtensionUtil.reasonsMatch(order, drugOrder)) {
							Date startDate = order.getEffectiveStartDate();
							if (drugOrder.getEffectiveStartDate().getTime() != startDateDrug.getTime()) {
								startDate = adjustDate(startDate, daysDiff(drugOrder.getEffectiveStartDate(), startDateDrug));
							}
							DrugOrder newOrder = cloneAndVoidPrevious(order, changeReason);
							updateDrugOrder(
									newOrder, drug, dose, doseUnits, frequency, startDate, duration,
									asNeeded, route, classification, indication, instructions, adminInstructions
							);

							getOrderExtensionService().extendedSaveDrugOrder(newOrder);
						}
					}
				}
			}

			DrugOrder newOrder = cloneAndVoidPrevious(drugOrder, changeReason);
			newOrder = updateDrugOrder(
					newOrder, drug, dose, doseUnits, frequency, startDateDrug, duration, asNeeded,
					route, classification, indication, instructions, adminInstructions
			);
			getOrderExtensionService().extendedSaveDrugOrder(newOrder);
		}
		catch (Exception e) {
			log.error("Unable to Edit Drug Order:", e);
			updateSessionMessage(request, e);
		}
		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/stopDrug")
	public String stopDrug(ModelMap model, @RequestParam(value = "orderId", required = true) Integer orderId,
	                       @RequestParam(value = "drugStopDate", required = true) Date stopDate,
	                       @RequestParam(value = "drugStopReason", required = true) Integer stopReason,
	                       @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                       @RequestParam(value = "returnPage", required = true) String returnPage,
	                       @RequestParam(value = "patientId", required = true) Integer patientId) {
		
		Concept stopConcept = Context.getConceptService().getConcept(stopReason);
		
		DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrder(orderId);
		if (repeatCycle != null) {
			DrugRegimen regimen = (DrugRegimen) drugOrder.getOrderGroup();
			Patient patient = Context.getPatientService().getPatient(patientId);
			List<DrugOrder> futureOrders = getOrderExtensionService()
			        .getFutureDrugOrdersOfSameOrderSet(patient, regimen.getOrderSet(),
			            regimen.getFirstDrugOrderStartDate());

			for (DrugOrder order : futureOrders) {
				if (order.getDrug() != null && drugOrder.getDrug() != null) {
					if (order.getDrug().equals(drugOrder.getDrug())) {
						Context.getOrderService().voidOrder(order, stopConcept.getDisplayString());
					}
				} else if (order.getConcept() != null && drugOrder.getConcept() != null) {
					if (order.getConcept().equals(drugOrder.getConcept())) {
						Context.getOrderService().voidOrder(order, stopConcept.getDisplayString());
					}
				}
			}
		}

		Date discontinueDate = stopDate;
		Date now = new Date();
		if (OrderExtensionUtil.sameDate(discontinueDate, now)) {
			discontinueDate = new Date();
		}
		else if (OrderExtensionUtil.sameDate(drugOrder.getEffectiveStartDate(), discontinueDate)) {
			discontinueDate = OrderExtensionUtil.adjustDateToEndOfDay(stopDate);
		}

		OrderEntryUtil.getOrderExtensionService().discontinueOrder(drugOrder, stopConcept, discontinueDate);
		
		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/deleteDrug")
	public String deleteDrug(ModelMap model, @RequestParam(value = "orderId", required = true) Integer orderId,
	                         @RequestParam(value = "deleteReason", required = true) String voidReason,
	                         @RequestParam(value = "deleteReasonDescription", required = false) String voidReasonDescription,
	                         @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                         @RequestParam(value = "returnPage", required = true) String returnPage,
	                         @RequestParam(value = "patientId", required = true) Integer patientId) {
		
		StringBuilder voidReasonAndDescription=new StringBuilder();
		voidReasonAndDescription.append(voidReason);
		//if(!voidReasonDescription.equals("") || voidReasonDescription!=null || voidReasonDescription.length()>0){
		if(voidReasonDescription.trim().length()>0){
			voidReasonAndDescription.append(" ");
			voidReasonAndDescription.append(voidReasonDescription);
		}
		
		Order o = Context.getOrderService().getOrder(orderId);
		DrugOrder drugOrder = (DrugOrder)o;
			
		if (repeatCycle != null) {
			DrugRegimen regimen = (DrugRegimen)drugOrder.getOrderGroup();
			Patient patient = Context.getPatientService().getPatient(patientId);
			List<DrugOrder> futureOrders = getOrderExtensionService()
			        .getFutureDrugOrdersOfSameOrderSet(patient, regimen.getOrderSet(),
			            regimen.getFirstDrugOrderStartDate());

			for (DrugOrder order : futureOrders) {
				if (order.getDrug() != null && drugOrder.getDrug() != null) {
					if (order.getDrug().equals(drugOrder.getDrug())) {
						Context.getOrderService().voidOrder(order, voidReasonAndDescription.toString());
					}
				} else if (order.getConcept() != null && drugOrder.getConcept() != null) {
					if (order.getConcept().equals(drugOrder.getConcept())) {
						Context.getOrderService().voidOrder(order, voidReasonAndDescription.toString());
					}
				}
			}
		}
		
		Context.getOrderService().voidOrder(o, voidReasonAndDescription.toString());
		
		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/deleteAllDrugsInGroup")
	public String deleteAllDrugsInGroup(ModelMap model, @RequestParam(value = "groupId", required = true) Integer groupId,
	                                    @RequestParam(value = "deleteReason", required = true) String voidReason,
	       	                         	@RequestParam(value = "deleteAllReasonDescription", required = false) String voidReasonDescription,
	       	                         	@RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                                    @RequestParam(value = "returnPage", required = true) String returnPage,
	                                    @RequestParam(value = "patientId", required = true) Integer patientId) {
		
		
		StringBuilder voidReasonAndDescription=new StringBuilder();
		voidReasonAndDescription.append(voidReason);
		//if(!voidReasonDescription.equals("") || voidReasonDescription!=null || voidReasonDescription.length()>0){
		if(voidReasonDescription.trim().length()>0){
			voidReasonAndDescription.append(" ");
			voidReasonAndDescription.append(voidReasonDescription);
		}
		
		
		
		DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(groupId);
		
		if (repeatCycle != null) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			List<DrugOrder> futureOrders = getOrderExtensionService()
			        .getFutureDrugOrdersOfSameOrderSet(patient, regimen.getOrderSet(), regimen.getLastDrugOrderEndDate());
			
			for (DrugOrder order : futureOrders) {
				Context.getOrderService().voidOrder(order, voidReasonAndDescription.toString());
			}
		}
		
		for (DrugOrder order : regimen.getMembers()) {
			Context.getOrderService().voidOrder(order, voidReasonAndDescription.toString());
		}
		
		return "redirect:" + returnPage;
	}
	
	@RequestMapping(value = "/module/orderextension/stopAllDrugsInGroup")
	public String stopAllDrugsInGroup(ModelMap model, @RequestParam(value = "groupId", required = true) Integer groupId,
	                                  @RequestParam(value = "drugStopAllDate", required = true) Date stopDate,
	                                  @RequestParam(value = "drugStopAllReason", required = true) Integer stopReason,
	                                  @RequestParam(value = "repeatCycles", required = false) String repeatCycle,
	                                  @RequestParam(value = "returnPage", required = true) String returnPage,
	                                  @RequestParam(value = "patientId", required = true) Integer patientId) {
		
		DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(groupId);
		
		Concept stopConcept = Context.getConceptService().getConcept(stopReason);
		
		if (repeatCycle != null) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			List<DrugOrder> futureOrders = getOrderExtensionService()
			        .getFutureDrugOrdersOfSameOrderSet(patient, regimen.getOrderSet(), regimen.getLastDrugOrderEndDate());
			
			for (DrugOrder order : futureOrders) {
				Context.getOrderService().voidOrder(order, stopConcept.getDisplayString());
			}
		}
		
		for (DrugOrder order : regimen.getMembers()) {
			if (OrderEntryUtil.isCurrent(order)) {
				OrderEntryUtil.getOrderExtensionService().discontinueOrder(order, stopConcept, OrderExtensionUtil.adjustDateToEndOfDay(stopDate));
			} else if (OrderEntryUtil.isFuture(order)) {
				Context.getOrderService().voidOrder(order, stopConcept.getDisplayString());
			}
		}
		
		return "redirect:" + returnPage;
	}

	private int daysDiff(Date startDateComparison, Date endDateComparison) {
		long milis2 = startDateComparison.getTime();
		long milis1 = endDateComparison.getTime();
		long diff = milis1 - milis2;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return (int)diffDays;
	}
	
	private Date adjustDate(Date dateToAdjust, int daysDiff) {
		Calendar adjusted = Calendar.getInstance();
		adjusted.setTime(dateToAdjust);
		adjusted.add(Calendar.DAY_OF_YEAR, daysDiff);
		return adjusted.getTime();
	}
	
	private Integer getCycleDay(Date firstDrugStart, Date drugStart) {
		if (firstDrugStart != null && drugStart != null) {
			long cycleDay = drugStart.getTime() - firstDrugStart.getTime();
			if (cycleDay > 0) {
				cycleDay = cycleDay / 86400000;
				cycleDay = cycleDay + 1;
				return (int) cycleDay;
			}
		}
		
		return 1;
	}
	
	public Date getCycleDate(Date cycleStart, Integer day) {
		Calendar cycleDate = Calendar.getInstance();
		cycleDate.setTime(cycleStart);
		cycleDate.add(Calendar.DAY_OF_YEAR, day - 1);
		return cycleDate.getTime();
	}

	private void updateSessionMessage(WebRequest request, Exception e) {
		String msg = Context.getMessageSourceService().getMessage("error.general", null, Context.getLocale());
		msg += ": " + e.getMessage();
		request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, msg, WebRequest.SCOPE_SESSION);
	}
}
