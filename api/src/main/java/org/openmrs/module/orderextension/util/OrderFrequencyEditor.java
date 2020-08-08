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
import org.openmrs.OrderFrequency;
import org.openmrs.api.context.Context;
import org.springframework.util.StringUtils;

/**
 * Supports conversion between an ExtendedOrderSet and a String representation
 */
public class OrderFrequencyEditor extends PropertyEditorSupport {

	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * Default constructor
	 */
	public OrderFrequencyEditor() { }
	
	/**
	 * @see PropertyEditorSupport#setAsText(String)
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try {
				setValue(Context.getOrderService().getOrderFrequency(Integer.parseInt(text)));
			}
			catch (Exception ex) {
				throw new IllegalArgumentException("Order Frequency not found with id: " + text, ex);
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
		OrderFrequency frequency = (OrderFrequency) getValue();
		if (frequency == null) {
			return "";
		} 
		else {
			return frequency.getId().toString();
		}
	}
}
