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
package org.openmrs.module.orderextension;

import org.openmrs.annotation.Handler;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for {@link OrderSet}
 */
@Handler(supports = { OrderSetValidator.class }, order = 50)
public class OrderSetValidator implements Validator {
	
	/**
	 * @see Validator#supports(Class)
	 */
	@SuppressWarnings("rawtypes")
    public boolean supports(Class c) {
		return OrderSet.class.isAssignableFrom(c);
	}
	
	/**
	 * @see Validator#validate(Object, Errors)
	 */
	public void validate(Object obj, Errors errors) {
		// TODO: Implement this, and add @should annotations as appropriate
	}
}
