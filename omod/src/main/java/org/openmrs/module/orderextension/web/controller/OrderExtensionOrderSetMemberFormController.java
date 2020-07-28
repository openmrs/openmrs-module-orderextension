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

import org.openmrs.OrderSetMember;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.ExtendedOrderSetMember;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.module.orderextension.util.OrderSetEditor;
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
 * Controls adding / editing / viewing an individual ExtendedOrderSetMember
 */
@Controller
public class OrderExtensionOrderSetMemberFormController {
	
	/**
	 * Registers any needed property editors
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder)    {
		binder.registerCustomEditor(ExtendedOrderSet.class, new OrderSetEditor());
	}
	
	/**
	 * Prepares form backing object to be used by the view
	 * @return backing object for associated view form
	 */
	@SuppressWarnings("unchecked")
	@ModelAttribute("orderSetMember")
	public ExtendedOrderSetMember formBackingObject(@RequestParam(value = "uuid", required=false) String uuid) {
		OrderSetMember member = new OrderSetMember();
		if (uuid != null) {
			member = Context.getOrderSetService().getOrderSetMemberByUuid(uuid);
		}
		return new ExtendedOrderSetMember(member);
	}
	
	/**
	 */
	@RequestMapping(value = "/module/orderextension/orderSetMemberForm.form", method = RequestMethod.GET)
	public void showOrderSetMemberForm(ModelMap model, 
									   @RequestParam(value = "orderSetId", required=true) Integer orderSetId) {
		ExtendedOrderSet orderSet = getOrderExtensionService().getOrderSet(orderSetId);
		model.addAttribute("orderSet", orderSet);
		model.addAttribute("drugList", Context.getConceptService().getAllDrugs());
		List<ExtendedOrderSet> existingOrderSets = getOrderExtensionService().getNamedOrderSets(false);
		existingOrderSets.remove(orderSet);
		model.addAttribute("existingOrderSets", existingOrderSets);
	}
	
	/**
	 * Save changes which were made within token registration. If any validation error has been
	 * occurred it will populate binding result with corresponding error messages and return back to
	 * edit page.
	 */
	@RequestMapping(value = "/module/orderextension/orderSetMemberForm.form", method = RequestMethod.POST)
	public String saveOrderSetMember(@ModelAttribute("orderSetMember") ExtendedOrderSetMember orderSetMember, BindingResult result,
									 @RequestParam(value="orderSetId", required=true) Integer orderSetId, WebRequest request) {
		
		ExtendedOrderSet orderSet = getOrderExtensionService().getOrderSet(orderSetId);
		OrderSetMember member = orderSetMember.getMember();
		if (member.getOrderSet() == null) {
			member.setOrderSet(orderSet);
			member.setOrderTemplateType("orderextension");
			orderSet.addOrderSetMember(member);
		}
		orderSet = getOrderExtensionService().saveOrderSet(orderSet);

		return "redirect:orderSet.list?id="+orderSet.getId();
	}
	
	private OrderExtensionService getOrderExtensionService() {
		return Context.getService(OrderExtensionService.class);
	}
}
