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
package org.openmrs.module.orderextension.api.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.ExtendedOrderSetMember;
import org.openmrs.module.orderextension.api.OrderExtensionService;

/**
 * Order Extension Data Access Interface
 */
public interface OrderExtensionDAO {
	
	/**
	 * @see OrderExtensionService#getOrderSet(Integer)
	 */
	public ExtendedOrderSet getOrderSet(Integer id);
	
	/**
	 * @see OrderExtensionService#getOrderSetByUuid(String)
	 */
	public ExtendedOrderSet getOrderSetByUuid(String uuid);
	
	/**
	 * Return all OrderSets that have a non-null name property, 
	 * whose name matches the partialName if specified, whose indication matches the indication if specified,
	 * and which are not retired, if specified
	 */
	public List<ExtendedOrderSet> getNamedOrderSets(String partialName, Concept indication, boolean includeRetired);
	
	/**
	 * @see OrderExtensionService#saveOrderSet(ExtendedOrderSet)
	 */
	public ExtendedOrderSet saveOrderSet(ExtendedOrderSet orderSet);
	
	/**
	 * @see OrderExtensionService#purgeOrderSet(ExtendedOrderSet)
	 */
	public void purgeOrderSet(ExtendedOrderSet orderSet);
	
	/**
	 * @see OrderExtensionService#getOrderSetMember(Integer)
	 */
	public ExtendedOrderSetMember getOrderSetMember(Integer id);
	
	/**
	 * @see OrderExtensionService#getDrugRegimens(Patient)
	 */
	public List<DrugRegimen> getDrugRegimens(Patient patient);

    /**
     * Retrieves the Drug Orders for a patient
     */
    public List<DrugOrder> getDrugOrdersForPatient(Patient patient, Concept indication);

	/**
     * @see OrderExtensionService#getMaxNumberOfCyclesForRegimen(DrugRegimen)
     */
    public Integer getMaxNumberOfCyclesForRegimen(DrugRegimen regimen);
    
}

