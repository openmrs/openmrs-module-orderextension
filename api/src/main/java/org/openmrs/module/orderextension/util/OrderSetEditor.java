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
package org.openmrs.module.orderextension.util;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.springframework.util.StringUtils;

/**
 * Supports conversion between an ExtendedOrderSet and a String representation
 */
public class OrderSetEditor extends PropertyEditorSupport {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Default constructor
	 */
	public OrderSetEditor() { }
	
	/**
	 * @see PropertyEditorSupport#setAsText(String)
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		OrderExtensionService svc = Context.getService(OrderExtensionService.class);
		if (StringUtils.hasText(text)) {
			try {
				setValue(svc.getOrderSet(Integer.valueOf(text)));
			}
			catch (Exception ex) {
				setValue(svc.getOrderSetByUuid(text));
				if (getValue() == null) {
					log.error("Error setting text: " + text, ex);
					throw new IllegalArgumentException("Order Set not found: " + ex.getMessage());
				}
			}
		} 
		else {
			setValue(null);
		}
	}
	
	/**
	 * @see PropertyEditorSupport#getAsText()
	 */
	public String getAsText() {
		ExtendedOrderSet orderSet = (ExtendedOrderSet) getValue();
		if (orderSet == null) {
			return "";
		} 
		else {
			Integer orderTypeId = orderSet.getId();
			if (orderTypeId == null) {
				return "";
			} 
			else {
				return orderTypeId.toString();
			}
		}
	}
}
