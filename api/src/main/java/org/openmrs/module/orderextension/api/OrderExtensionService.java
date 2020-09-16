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

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.OrderSet;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.ExtendedOrderSetMember;
import org.openmrs.module.orderextension.util.DrugOrderTemplate;
import org.openmrs.module.orderextension.util.OrderExtensionConstants;
import org.openmrs.util.PrivilegeConstants;

/**
 * Interface for managing extensions to Orders
 */
public interface OrderExtensionService extends OpenmrsService {
	
	/**
	 * @param id the primary key id for the ExtendedOrderSet
	 * @return the ExtendedOrderSet matching the passed primary key id
	 * should return an ExtendedOrderSet with the passed id
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	ExtendedOrderSet getOrderSet(Integer id);

	/**
	 * @param uuid the unique uuid for the ExtendedOrderSet
	 * @return the ExtendedOrderSet matching the passed uuid
	 * should return an ExtendedOrderSet with the passed uuid
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	ExtendedOrderSet getOrderSetByUuid(String uuid);

	/**
	 * This will retrieve all named OrderSets, optionally including those that are retired
	 * Non-named OrderSets are assumed to be nested, "inner" OrderSets within other OrderSets and are not returned by this method
	 * @param includeRetired if false, should not include retired OrderSets
	 * @return all existing, named OrderSets, limited to non-retired if applicable
	 * should return all order sets
	 * should not return retired order sets if specified
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	List<ExtendedOrderSet> getNamedOrderSets(boolean includeRetired);

	/**
	 * Retrieves all named, non-retired Order Sets that match all or part of the specified name and the specified Concept
	 * Both parameters are optional, and only limit the resulting Order Sets if specified.
	 * @param partialName the name fragment to search for Order Sets
	 * @return the ExtendedOrderSet matching the passed uuid
	 * should limit results to only those order sets whose name matches the partialName if specified
	 * should not limit results by name if partial name is not specified
	 * should limit results to only those order sets matching the passed indication if specified
	 * should not limit results by indication if not specified
	 * should only return named order sets
	 * should not return retired order sets
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	List<ExtendedOrderSet> findAvailableOrderSets(String partialName, Concept indication);

	/**
	 * @param orderSet the ExtendedOrderSet to save
	 * @return the saved ExtendedOrderSet
	 * should save a new ExtendedOrderSet
	 * should save changes to an existing ExtendedOrderSet
	 */
	@Authorized(OrderExtensionConstants.PRIV_MANAGE_ORDER_SETS)
	ExtendedOrderSet saveOrderSet(ExtendedOrderSet orderSet);

	/**
	 * This will purge an ExtendedOrderSet from the database.  If this ExtendedOrderSet contains
	 * nested, unnamed OrderSets, these will be purged as well.
	 * @param orderSet the ExtendedOrderSet to purge from the database
	 * should delete the passed ExtendedOrderSet if no Order Groups are linked to it
	 * should delete nested OrderSets if these are unnamed
	 * should throw an exception if any Order Groups are linked to this order set
	 * should throw an exception if any Order Groups are linked to unnamed, nested order sets
	 */
	@Authorized(OrderExtensionConstants.PRIV_DELETE_ORDER_SETS)
	void purgeOrderSet(ExtendedOrderSet orderSet);

	/**
	 * @param id the primary key id for the ExtendedOrderSetMember
	 * @return the ExtendedOrderSetMember matching the passed primary key id
	 * should return an ExtendedOrderSetMember with the passed id
	 */
	@Authorized(OrderExtensionConstants.PRIV_VIEW_ORDER_SETS)
	ExtendedOrderSetMember getOrderSetMember(Integer id);

	/**
	 * @return in 2.3, all Orders must be associated with Encounters
	 * This method servers to return the EncounterType to use if not otherwise specified
	 */
	EncounterType getDefaultEncounterType();

	/**
	 * @return in 2.3, all Orders must be associated with Encounters
	 * This method servers to return the EncounterRole to use if not otherwise specified
	 */
	EncounterRole getDefaultEncounterRole();

	/**
	 * @return a Provider account to use for Orders and Encounters, given a User
	 */
	Provider getProviderForUser(User user);

	/**
	 * @return a non-null encounter with an appropriate date for this encounter date and patient
	 */
	Encounter ensureDrugOrderEncounter(Patient patient, Encounter encounter, Date encounterDate);

	/**
	 * Save a given Drug Order
	 */
	DrugOrder extendedSaveDrugOrder(DrugOrderTemplate drugOrderTemplate, boolean includeCycles);

	/**
	 * Save a given Drug Order
	 */
	DrugOrder extendedSaveDrugOrder(DrugOrder drugOrder);

	/**
	 * @param orderGroup the DrugRegimen to save
	 * @return the saved ExtendedOrderGroup
	 * should save a new ExtendedOrderGroup
	 * should save changes to an existing ExtendedOrderGroup
	 */
	@Authorized(PrivilegeConstants.ADD_ORDERS)
	DrugRegimen saveDrugRegimen(DrugRegimen orderGroup);

	/**
	 * @param drugRegimens the DrugRegimens to save
	 */
	@Authorized(PrivilegeConstants.ADD_ORDERS)
	void saveDrugRegimens(List<DrugRegimen> drugRegimens);

	/**
	 * @param patient the Patient for whom to retrieve the Orders
	 * @return a List of all OrderGroups
	 * should return all OrderGroups for the passed patient for the passed type
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	List<DrugRegimen> getDrugRegimens(Patient patient);

	/**
	 * @param patient the Patient for whom to add the Orders
	 * @param orderSet the ExtendedOrderSet to use as a template to create the new Orders
	 * @param startDate the Date on which to start the new Orders
	 * @param numCycles if the ExtendedOrderSet represents a cyclical set of Orders, the number of cycles to Order
	 * should add the appropriate number of Orders for the patient given the passed ExtendedOrderSet
	 */
	@Authorized(PrivilegeConstants.ADD_ORDERS)
	List<DrugRegimen> addOrdersForPatient(Patient patient, ExtendedOrderSet orderSet, Date startDate, Integer numCycles);
    
    /**
     * @param id the id of the drugRegimen to be returned
     * @return the requested drugRegimen
     */
    DrugRegimen getDrugRegimen(Integer id);
    
    /**
     * @param regimen for which the maximum number of cycles should be retrieved for
     * @return the Integer representing the maximum number of cycles
     */
    Integer getMaxNumberOfCyclesForRegimen(DrugRegimen regimen);

	/**
	 * Clones the passed in order, voids it, updates the new order with revised dates, and returns it
	 */
	void updateOrderStartAndEndDates(DrugOrder order, int daysDiff, String reason);

	/**
	 * Clones the passed in order, voids it, and returns it
	 */
	DrugOrder cloneAndVoidPrevious(DrugOrder orderToVoid, String reason);

	/**
	 * Change the start date of the given regimen
	 */
	void changeStartDateOfGroup(Patient patient, DrugRegimen drugRegimen, Date changeDate, boolean includeCycles);

	/**
	 * Changes the start date of part of a given regimen
	 */
	void changeStartDateOfPartGroup(Patient patient, DrugRegimen drugRegimen, Date changeDate, Integer cycleDay,
			boolean includeCycles, boolean includePartCycles, boolean includeThisCycle);

	/**
	 * Changes the details of a drug order and optionally other similar orders in the same regimen
	 */
	DrugOrder changeDrugOrder(DrugOrder drugOrder, DrugOrderTemplate drugOrderTemplate, String changeReason, boolean includeCycles);

	/**
	 * Stops the order and optionally all of the same instances of the order in future cycles
	 */
	void stopDrugOrder(Patient patient, DrugOrder drugOrder, Date stopDate, Concept stopReason, boolean includeCycles);

	/**
	 * Stops the regimen and optionally all of the same instances of the regimen in future cycles
	 */
	void stopDrugOrdersInGroup(Patient patient, DrugRegimen regimen, Date stopDate, Concept stopReason, boolean includeCycles);

	/**
	 * Deletes/voids the order and optionally all of the same instances of the order in future cycles
	 */
	void voidDrugOrder(Patient patient, DrugOrder drugOrder, String voidReason, boolean includeCycles);

	/**
	 * Deletes/voids the regimen and optionally all of the same instances of the regimen in future cycles
	 */
	void voidDrugOrdersInGroup(Patient patient, DrugRegimen regimen, String voidReason, boolean includeCycles);

	/**
     * @param patient the Patient for whom to retrieve orders
     * @param orderSet the id of the order set to which the drug orders should belong
     */
    List<DrugOrder> getFutureDrugOrdersOfSameOrderSet(Patient patient, OrderSet orderSet, Date startDate);

    /**
     * @param patient the Patient for whom to retrieve orders
     * @param drugRegimen the regimen whose orderSet is used to retrieve other regimens for the patient
     */
    List<DrugRegimen> getFutureDrugRegimensOfSameOrderSet(Patient patient, DrugRegimen drugRegimen, Date startDate);
    
	/**
     * @param patient the Patient for whom to retrieve the orders
     */
     List<DrugOrder> getDrugOrders(Patient patient, Concept indication, Date startDateAfter, Date startDateBefore);
}
