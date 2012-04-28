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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.OrderSet;
import org.openmrs.module.orderextension.OrderSetValidator;
import org.openmrs.module.orderextension.OrderSet.Operator;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Controls adding / editing / viewing an individual OrderSet
 */
@Controller
public class OrderExtensionOrderSetFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private OrderSetValidator validator;
	
	/**
	 * Default constructor
	 */
	public OrderExtensionOrderSetFormController() { }
	
	/**
	 * Constructor that takes in a validator
	 */
	@Autowired
	public OrderExtensionOrderSetFormController(OrderSetValidator validator) {
		this.validator = validator;
	}
	
	/**
	 * Prepares form backing object to be used by the view
	 * @param id (optional) if specified, indicates the OrderSet to edit
	 * @return backing object for associated view form
	 */
	@ModelAttribute("orderSet")
	public OrderSet formBackingObject(@RequestParam(value = "id", required = false) Integer id) {
		if (id != null) {
			return getOrderExtensionService().getOrderSet(id);
		}
		else {
			return new OrderSet();
		}
	}
	
	/**
	 */
	@RequestMapping(value = "/module/orderextension/orderSetForm.form", method = RequestMethod.GET)
	public void showOrderSetForm(ModelMap model) {
		model.addAttribute("operators", Operator.values());
	}
	
	/**
	 * Save changes which were made within token registration. If any validation error has been
	 * occurred it will populate binding result with corresponding error messages and return back to
	 * edit page.
	 */
	@RequestMapping(value = "/module/orderextension/orderSetForm.form", method = RequestMethod.POST)
	public String saveOrderSet(@ModelAttribute("orderSet") OrderSet orderSet, BindingResult result, WebRequest request) {
		
		// Validate
		validator.validate(orderSet, result);
		if (result.hasErrors()) {
			return null;
		}

		try {
			orderSet = getOrderExtensionService().saveOrderSet(orderSet);
		}
		catch (Exception e) {
			log.error("Unable to save Order Set, because of error:", e);
			updateSessionMessage(request, "orderextension.orderset.errorSaving");
			return null;
		}
		
		updateSessionMessage(request, "orderextension.orderset.saved", orderSet.getName());
		return "redirect:orderSetList.list";
	}
	
	private OrderExtensionService getOrderExtensionService() {
		return Context.getService(OrderExtensionService.class);
	}
	
	private void updateSessionMessage(WebRequest request, String code, Object... args) {
		String msg = Context.getMessageSourceService().getMessage(code, args, Context.getLocale());
		request.setAttribute(WebConstants.OPENMRS_MSG_ATTR, msg, WebRequest.SCOPE_SESSION);
	}
}
