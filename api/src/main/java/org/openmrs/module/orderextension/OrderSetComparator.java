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

import java.util.Comparator;

import org.openmrs.api.context.Context;

/**
 * Sorts Drug Orders by date and then by name
 */
public class OrderSetComparator implements Comparator<OrderSet> {
	
	private String undefined = Context.getAdministrationService().getGlobalProperty("orderextension.regimen.unclassified");
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(OrderSet r1, OrderSet r2) {
        
        String r1Name = "";
        String r2Name = "";
        
        if(r1.getIndication() != null)
        {
        	r1Name = r1.getIndication().getDisplayString() + " - " + r1.getName();
        }
        else
        {
        	r1Name = undefined + " - " + r1.getName();
        }
        
        if(r2.getIndication() != null)
        {
        	r2Name = r2.getIndication().getDisplayString() + " - " + r2.getName();
        }
        else
        {
        	r2Name = undefined + " - " + r2.getName();
        }
        
        return r1Name.compareTo(r2Name);
     }
}
