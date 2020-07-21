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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.api.OrderSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.ExtendedOrderSetMember;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Primary Controller for administering order sets
 */
@Controller
public class OrderExtensionOrderSetListController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Shows the page to list order sets
	 */
	@RequestMapping(value = "/module/orderextension/orderSetList")
	public void listOrderSets(ModelMap model, @RequestParam(value="includeRetired", required=false) boolean includeRetired) {
		List<ExtendedOrderSet> ordersets = Context.getService(OrderExtensionService.class).getNamedOrderSets(includeRetired);
		model.addAttribute("orderSets", ordersets);
	}
	
	/**
	 * Shows the page to view a single order set
	 */
	@RequestMapping(value = "/module/orderextension/orderSet")
	public void viewOrderSet(ModelMap model, 
							 @RequestParam(value="id", required=true) Integer id) {
		ExtendedOrderSet orderSet = Context.getService(OrderExtensionService.class).getOrderSet(id);
		model.addAttribute("orderSet", orderSet);
		List<Class<? extends ExtendedOrderSetMember>> memberTypes = new ArrayList<Class<? extends ExtendedOrderSetMember>>();
		memberTypes.add(ExtendedOrderSetMember.class);
		model.addAttribute("memberTypes", memberTypes);
	}
	
	/**
	 * Deletes a single order set
	 */
	@RequestMapping(value = "/module/orderextension/deleteOrderSet")
	public String deleteOrderSet(ModelMap model, @RequestParam(value="id", required=true) Integer id) {
		ExtendedOrderSet orderSet = Context.getService(OrderExtensionService.class).getOrderSet(id);
		Context.getService(OrderExtensionService.class).purgeOrderSet(orderSet);
		return "redirect:orderSetList.list";
	}
	
	/**
	 * Deletes an order set member
	 */
	@RequestMapping(value = "/module/orderextension/deleteOrderSetMember")
	public String deleteOrderSetMember(ModelMap model, WebRequest request,
									@RequestParam(value="uuid", required=true) String uuid) {
		OrderSetMember orderSetMember = Context.getOrderSetService().getOrderSetMemberByUuid(uuid);
		OrderSet orderSet = orderSetMember.getOrderSet();
		orderSet.removeOrderSetMember(orderSetMember);
		Context.getService(OrderSetService.class).saveOrderSet(orderSet);
		return "redirect:orderSet.list?id="+orderSet.getId();
	}
}
