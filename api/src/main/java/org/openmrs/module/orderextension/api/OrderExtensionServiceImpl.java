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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderGroup;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.ExtendedOrderSetMember;
import org.openmrs.module.orderextension.api.db.OrderExtensionDAO;
import org.openmrs.module.orderextension.util.OrderExtensionUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core implementation of the OrderExensionService
 */
@Transactional
public class OrderExtensionServiceImpl extends BaseOpenmrsService implements OrderExtensionService {
	
	private OrderExtensionDAO dao;

	/**
	 * @see OrderExtensionService#getOrderSet(Integer)
	 */
	@Override
	@Transactional(readOnly=true)
	public ExtendedOrderSet getOrderSet(Integer id) {
		return dao.getOrderSet(id);
	}

	/**
	 * @see OrderExtensionService#getOrderSetByUuid(String)
	 */
	@Override
	@Transactional(readOnly=true)
	public ExtendedOrderSet getOrderSetByUuid(String uuid) {
		return dao.getOrderSetByUuid(uuid);
	}

	/**
	 * @see OrderExtensionService#getNamedOrderSets(boolean)
	 */
	@Override
	@Transactional(readOnly=true)
	public List<ExtendedOrderSet> getNamedOrderSets(boolean includeRetired) {
		return dao.getNamedOrderSets(null, null, includeRetired);
	}

	/**
	 * @see OrderExtensionService#findAvailableOrderSets(String, Concept)
	 */
	@Override
	@Transactional(readOnly=true)
	public List<ExtendedOrderSet> findAvailableOrderSets(String partialName, Concept indication) {
		return dao.getNamedOrderSets(partialName, indication, false);
	}

	/**
	 * @see OrderExtensionService#saveOrderSet(ExtendedOrderSet)
	 */
	@Override
	@Transactional
	public ExtendedOrderSet saveOrderSet(ExtendedOrderSet orderSet) {
		return dao.saveOrderSet(orderSet);
	}

	/**
	 * @see OrderExtensionService#purgeOrderSet(ExtendedOrderSet)
	 */
	@Override
	@Transactional
	public void purgeOrderSet(ExtendedOrderSet orderSet) {
		dao.purgeOrderSet(orderSet);
	}

	/**
	 * @see OrderExtensionService#getOrderSetMember(Integer)
	 */
	@Override
	@Transactional(readOnly=true)
	public ExtendedOrderSetMember getOrderSetMember(Integer id) {
		return dao.getOrderSetMember(id);
	}

	/**
	 * @see OrderExtensionService#saveDrugRegimen(DrugRegimen)
	 */
	@Override
	@Transactional
	public DrugRegimen saveDrugRegimen(DrugRegimen orderGroup) {
		return (DrugRegimen)Context.getOrderService().saveOrderGroup(orderGroup);
	}

	/**
	 * @see OrderExtensionService#getDrugRegimens(Patient)
	 */
	@Override
	@Transactional(readOnly=true)
	public List<DrugRegimen> getDrugRegimens(Patient patient) {
		return dao.getDrugRegimens(patient);
	}
	
	/**
	 * @see OrderExtensionService#getDrugRegimen(Integer orderGroup)
	 */
	@Override
	@Transactional(readOnly=true)
	public DrugRegimen getDrugRegimen(Integer id) {
		return (DrugRegimen) Context.getOrderService().getOrderGroup(id);
	}
	
	/**
     * @see OrderExtensionService#getMaxNumberOfCyclesForRegimen(DrugRegimen)
     */
    @Override
    public Integer getMaxNumberOfCyclesForRegimen(DrugRegimen regimen) {
	    if (regimen.isCyclical() && regimen.getCycleNumber() != null) {
	    	return dao.getMaxNumberOfCyclesForRegimen(regimen);
	    }
	    return 1;
    }
	
	/**
     * @see OrderExtensionService#getDrugOrders(Patient, Concept, Date, Date)
     */
    @Override
    public List<DrugOrder> getDrugOrders(Patient patient, Concept indication, Date startDateAfter, Date startDateBefore) {
	    List<DrugOrder> l = dao.getDrugOrdersForPatient(patient, indication);
	    if (startDateAfter != null || startDateBefore != null) {
		    for (Iterator<DrugOrder> i = l.iterator(); i.hasNext();) {
			    Date startDate = i.next().getEffectiveStartDate();
			    if (startDateAfter != null && startDate.before(startDateAfter)) {
				    i.remove();
			    }
			    else if (startDateBefore != null && startDate.after(startDateBefore)) {
				    i.remove();
			    }
		    }
	    }
	    return l;
    }
    
    /**
     * @see OrderExtensionService#getFutureDrugOrdersOfSameOrderSet(Patient patient, ExtendedOrderSet orderSet, Date startDate)
     */
    @Override
    public List<DrugOrder> getFutureDrugOrdersOfSameOrderSet(Patient patient, OrderSet orderSet, Date startDate) {
    	List<DrugOrder> futureOrders = new ArrayList<DrugOrder>();
    	for(DrugOrder order: getDrugOrders(patient, null, startDate, null)) {
		    OrderGroup g = order.getOrderGroup();
    		if (g != null && g.getOrderSet() != null && g.getOrderSet().equals(orderSet) && order.getEffectiveStartDate().after(startDate)) {
    			futureOrders.add(order);
    		}
    	}
    	return futureOrders;
    }
    
    /**
     * @see OrderExtensionService#getFutureDrugRegimensOfSameOrderSet(Patient patient, DrugRegimen drugRegimen, Date startDate)
     */
    @Override
    public List<DrugRegimen> getFutureDrugRegimensOfSameOrderSet(Patient patient, DrugRegimen drugRegimen, Date startDate) {
    	List<DrugRegimen> futureRegimens = new ArrayList<DrugRegimen>();
    	for (DrugOrder order: getDrugOrders(patient, null, startDate, null)) {
		    OrderGroup g = order.getOrderGroup();
		    if (g != null) {
			    DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(g.getId());
			    if (regimen.getFirstDrugOrderStartDate().after(drugRegimen.getLastDrugOrderEndDate()) && !futureRegimens.contains(regimen)) {
				    futureRegimens.add(regimen);
			    }
		    }
    	}
    	return futureRegimens;
    }

	/**
	 * @see OrderExtensionService#addOrdersForPatient(Patient, ExtendedOrderSet, Date, Integer)
	 */
	@Override
	@Transactional
	public void addOrdersForPatient(Patient patient, ExtendedOrderSet orderSet, Date startDate, Integer numCycles) {

		OrderExtensionService orderExtSvc = Context.getService(OrderExtensionService.class);
		
		if (numCycles == null) {
			numCycles = 1;
		}

		// TODO: This new encounter creation is something we need to fix, it isn't part of the original module, and we need to properly support this.
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setEncounterDatetime(new Date());
		encounter.setEncounterType(Context.getEncounterService().getEncounterType(1));
		encounter.setLocation(Context.getLocationService().getLocation(1));
		Context.getEncounterService().saveEncounter(encounter);
		
		for (int i=0; i<numCycles; i++) {
			
			DrugRegimen orderGroup = new DrugRegimen();
			orderGroup.setOrderSet(orderSet);
			if (orderSet.isCyclical()) {
				orderGroup.setCycleNumber(i+1);
			}
			
			Date cycleStart = startDate;

			if (orderSet.getCycleLengthInDays() != null) {
				cycleStart = OrderExtensionUtil.incrementDate(cycleStart, orderSet.getCycleLengthInDays() * i);
			}
			for (OrderSetMember member : orderSet.getOrderSetMembers()) {

				ExtendedOrderSetMember drugMember = new ExtendedOrderSetMember(member);
				Date memberStartDate = cycleStart;
				Date memberEndDate = null;
				if (drugMember.getRelativeStartDay() != null) {
					memberStartDate = OrderExtensionUtil.incrementDate(memberStartDate, drugMember.getRelativeStartDay() - 1);
				}
				if (drugMember.getLengthInDays() != null) {
					memberEndDate = OrderExtensionUtil.incrementDateEndOfDay(memberStartDate, drugMember.getLengthInDays() - 1);
				}

				DrugOrder drugOrder = new DrugOrder();
				drugOrder.setConcept(drugMember.getConcept());
				drugOrder.setDrug(drugMember.getDrug());
				drugOrder.setDose(drugMember.getDose());
				// drugOrder.setUnits(drugMember.getUnits()); TODO: Need to fix with migration
				drugOrder.setRoute(drugMember.getRoute());
				drugOrder.setDosingInstructions(drugMember.getAdministrationInstructions());
				// drugOrder.setFrequency(drugMember.getFrequency()); TODO: Need to fix with migration
				drugOrder.setAsNeeded(drugMember.isAsNeeded());
				drugOrder.setInstructions(drugMember.getInstructions());
				Concept indication = drugMember.getIndication();
				if (indication == null) {
					indication = orderSet.getIndication();
				}
				drugOrder.setOrderReason(indication);
				drugOrder.setDateActivated(memberStartDate); // TODO: Confirm that setting Date Activated is right
				drugOrder.setAutoExpireDate(memberEndDate);
				drugOrder.setOrderGroup(orderGroup);
				drugOrder.setPatient(patient);
				drugOrder.setEncounter(encounter);

				// TODO: This is not null safe, and not reliable - replace with proper method during migraiton
				Person p = Context.getAuthenticatedUser().getPerson();
				Provider orderer = Context.getProviderService().getProvidersByPerson(p).iterator().next();
				drugOrder.setOrderer(orderer);

				drugOrder.setOrderType(Context.getOrderService().getOrderTypeByName("Drug order")); // TODO: Replace this with proper method
				drugOrder.setCareSetting(Context.getOrderService().getCareSettingByName("INPATIENT")); // TODO: Do this properly during 2.3 upgrade
				setProperty(drugOrder, "orderNumber", UUID.randomUUID().toString()); // TODO: Remove this, added for 2.3 upgrade temporarily

				orderGroup.addOrder(drugOrder);
			}
			
			orderExtSvc.saveDrugRegimen(orderGroup);
		}
	}

	private void setProperty(Order order, String propertyName, Object value) {
		Boolean isAccessible = null;
		Field field = null;
		try {
			field = Order.class.getDeclaredField(propertyName);
			field.setAccessible(true);
			field.set(order, value);
		}
		catch (Exception e) {
			throw new APIException("Order.failed.set.property", new Object[] { propertyName, order }, e);
		}
		finally {
			if (field != null && isAccessible != null) {
				field.setAccessible(isAccessible);
			}
		}
	}

	/**
	 * @return the dao
	 */
	public OrderExtensionDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(OrderExtensionDAO dao) {
		this.dao = dao;
	}
}
