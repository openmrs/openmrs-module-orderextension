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

import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controls where in the order set the member is stored
 */
@Controller
public class OrderExtensionMemberSortController {

	@RequestMapping(value = "/module/orderextension/orderSetMemberSortForm.form", method = RequestMethod.GET)
	public String moveMember(
			@RequestParam(value = "uuid", required=true) String uuid,
			@RequestParam(value = "num") Integer num)
	{
		OrderSetMember member = Context.getOrderSetService().getOrderSetMemberByUuid(uuid);
		OrderSet orderSet = member.getOrderSet();
		int currentIndex = orderSet.getOrderSetMembers().indexOf(member);
		int newIndex = currentIndex + num;
		if (newIndex >=0 && newIndex < orderSet.getOrderSetMembers().size()) {
			orderSet.getOrderSetMembers().remove(member);
			orderSet.getOrderSetMembers().add(newIndex, member);
			Context.getOrderSetService().saveOrderSet(orderSet);
		}
		Context.flushSession();
		Context.clearSession();
		return "redirect:orderSet.list?id=" + orderSet.getId();
	}
}
