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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.NestedOrderSetMember;
import org.openmrs.module.orderextension.OrderSet;
import org.openmrs.module.orderextension.OrderSetMember;
import org.openmrs.module.orderextension.OrderSetMemberValidator;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.module.orderextension.util.OrderSetEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Controls adding / editing / viewing an individual OrderSetMember
 */
@Controller
public class OrderExtensionOrderSetMemberFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private OrderSetMemberValidator validator;
	
	/**
	 * Default constructor
	 */
	public OrderExtensionOrderSetMemberFormController() { }
	
	/**
	 * Constructor that takes in a validator
	 */
	@Autowired
	public OrderExtensionOrderSetMemberFormController(OrderSetMemberValidator validator) {
		this.validator = validator;
	}
	
	/**
	 * Registers any needed property editors
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder)    {
		binder.registerCustomEditor(OrderSet.class, new OrderSetEditor());
	}
	
	/**
	 * Prepares form backing object to be used by the view
	 * @param id (optional) if specified, indicates the OrderSet to edit
	 * @return backing object for associated view form
	 */
	@SuppressWarnings("unchecked")
	@ModelAttribute("orderSetMember")
	public OrderSetMember formBackingObject(@RequestParam(value = "memberId", required=false) Integer memberId,
									  		@RequestParam(value = "memberType", required=false) String memberType) {
		
		OrderSetMember ret = null;
		if (memberId != null) {
			ret = getOrderExtensionService().getOrderSetMember(memberId);
		}
		else {
			try {
				Class<? extends OrderSetMember> memberClazz = (Class<? extends OrderSetMember>)Context.loadClass(memberType);
				ret = memberClazz.newInstance();
			}
			catch (Exception e) {
				throw new APIException("Unable to instantiate OrderSetMember of type " + memberType + " because of error:", e);
			}
		}
		return ret;
	}
	
	/**
	 */
	@RequestMapping(value = "/module/orderextension/orderSetMemberForm.form", method = RequestMethod.GET)
	public void showOrderSetMemberForm(ModelMap model, 
									   @RequestParam(value = "orderSetId", required=true) Integer orderSetId) {
		OrderSet orderSet = getOrderExtensionService().getOrderSet(orderSetId);
		model.addAttribute("orderSet", orderSet);
		model.addAttribute("drugList", Context.getConceptService().getAllDrugs());
		List<OrderSet> existingOrderSets = getOrderExtensionService().getNamedOrderSets(false);
		for (OrderSetMember member : orderSet.getMembers()) {
			if (member instanceof NestedOrderSetMember) {
				existingOrderSets.add(((NestedOrderSetMember)member).getNestedOrderSet());
			}
		}
		existingOrderSets.remove(orderSet);
		model.addAttribute("existingOrderSets", existingOrderSets);
	}
	
	/**
	 * Save changes which were made within token registration. If any validation error has been
	 * occurred it will populate binding result with corresponding error messages and return back to
	 * edit page.
	 */
	@RequestMapping(value = "/module/orderextension/orderSetMemberForm.form", method = RequestMethod.POST)
	public String saveOrderSetMember(@ModelAttribute("orderSetMember") OrderSetMember orderSetMember, BindingResult result, 
									 @RequestParam(value="orderSetId", required=true) Integer orderSetId, WebRequest request) {
		
		Integer nestedOrderSetId = null;
		
		if (orderSetMember instanceof NestedOrderSetMember) {
			NestedOrderSetMember nestedMember = (NestedOrderSetMember) orderSetMember;
			if (nestedMember.getNestedOrderSet() == null) {
				OrderSet newNestedOrderSet = new OrderSet();
				newNestedOrderSet = getOrderExtensionService().saveOrderSet(newNestedOrderSet);
				nestedMember.setNestedOrderSet(newNestedOrderSet);
				nestedOrderSetId = newNestedOrderSet.getId();
			}
		}
		
		// Validate
		validator.validate(orderSetMember, result);
		if (result.hasErrors()) {
			return null;
		}
		
		OrderSet orderSet = getOrderExtensionService().getOrderSet(orderSetId);
		if (!orderSet.getMembers().contains(orderSetMember)) {
			orderSet.addMember(orderSetMember);
		}
		orderSet = getOrderExtensionService().saveOrderSet(orderSet);
		
		String redirect = "redirect:orderSet.list?id="+orderSet.getId();
		if (nestedOrderSetId != null) {
			redirect = "redirect:orderSetForm.form?id=" + nestedOrderSetId;
		}
		
		return redirect;
	}
	
	private OrderExtensionService getOrderExtensionService() {
		return Context.getService(OrderExtensionService.class);
	}
}
