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
package org.openmrs.module.orderextension.api;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.orderextension.OrderGroup;
import org.openmrs.module.orderextension.OrderSet;
import org.openmrs.module.orderextension.OrderSetMember;
import org.openmrs.module.orderextension.util.OrderExtensionConstants;
import org.openmrs.util.PrivilegeConstants;

/**
 * Interface for managing extensions to Orders
 */
public interface OrderExtensionService extends OpenmrsService {
	
	/**
	 * @param id the primary key id for the OrderSet 
	 * @return the OrderSet matching the passed primary key id
	 * @should return an OrderSet with the passed id
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	public OrderSet getOrderSet(Integer id);
	
	/**
	 * @param uuid the unique uuid for the OrderSet 
	 * @return the OrderSet matching the passed uuid
	 * @should return an OrderSet with the passed uuid
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	public OrderSet getOrderSetByUuid(String uuid);
	
	/**
	 * This will retrieve all named OrderSets, optionally including those that are retired
	 * Non-named OrderSets are assumed to be nested, "inner" OrderSets within other OrderSets and are not returned by this method
	 * @param includeRetired if false, should not include retired OrderSets 
	 * @return all existing, named OrderSets, limited to non-retired if applicable
	 * @should return all order sets
	 * @should not return retired order sets if specified
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	public List<OrderSet> getNamedOrderSets(boolean includeRetired);
	
	/**
	 * Retrieves all named, non-retired Order Sets that match all or part of the specified name and the specified Concept
	 * Both parameters are optional, and only limit the resulting Order Sets if specified.
	 * @param partialName the name fragment to search for Order Sets
	 * @return the OrderSet matching the passed uuid
	 * @should limit results to only those order sets whose name matches the partialName if specified
	 * @should not limit results by name if partial name is not specified
	 * @should limit results to only those order sets matching the passed indication if specified
	 * @should not limit results by indication if not specified
	 * @should only return named order sets
	 * @should not return retired order sets
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	public List<OrderSet> findAvailableOrderSets(String partialName, Concept indication);
	
	/**
	 * @param orderSet the OrderSet to save
	 * @return the saved OrderSet
	 * @should save a new OrderSet
	 * @should save changes to an existing OrderSet
	 */
	@Authorized(OrderExtensionConstants.PRIV_MANAGE_ORDER_SETS)
	public OrderSet saveOrderSet(OrderSet orderSet);
	
	/**
	 * This will purge an OrderSet from the database.  If this OrderSet contains
	 * nested, unnamed OrderSets, these will be purged as well.
	 * @param orderSet the OrderSet to purge from the database
	 * @should delete the passed OrderSet if no Order Groups are linked to it
	 * @should delete nested OrderSets if these are unnamed
	 * @should throw an exception if any Order Groups are linked to this order set
	 * @should throw an exception if any Order Groups are linked to unnamed, nested order sets
	 */
	@Authorized(OrderExtensionConstants.PRIV_DELETE_ORDER_SETS)
	public void purgeOrderSet(OrderSet orderSet);
	
	/**
	 * @param id the primary key id for the OrderSetMember
	 * @return the OrderSetMember matching the passed primary key id
	 * @should return an OrderSetMember with the passed id
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	public OrderSetMember getOrderSetMember(Integer id);
	
	/**
	 * @param orderSet the OrderSet to check
	 * @return all OrderSets that contain the passed OrderSet as a nested member
	 * @should return all OrderSets that contain the passed OrderSet as a nested member
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	public List<OrderSet> getParentOrderSets(OrderSet orderSet);
	
	/**
	 * @param patient the Patient for whom to retrieve the Orders
	 * @param type the type of OrderGroup to retrieve
	 * @return a List of all OrderGroups
	 * @should return all OrderGroups for the passed patient for the passed type
	 */
	@Authorized(PrivilegeConstants.VIEW_ORDERS)
	public <T extends OrderGroup> List<T> getOrderGroups(Patient patient, Class<T> type);
}
