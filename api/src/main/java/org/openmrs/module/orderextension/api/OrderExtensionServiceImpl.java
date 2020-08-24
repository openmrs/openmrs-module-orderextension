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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.OrderGroup;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.OrderContext;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.ExtendedOrderSetMember;
import org.openmrs.module.orderextension.api.db.OrderExtensionDAO;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.orderextension.util.OrderExtensionUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
	 * @see OrderExtensionService#getDefaultEncounterType()
	 */
	@Override
	@Transactional(readOnly=true)
	public EncounterType getDefaultEncounterType() {
		EncounterType ret = null;
		AdministrationService as = Context.getAdministrationService();
		String gpName = "orderextension.drugOrderDefaultEncounterTypeUuid";
		String uuid = as.getGlobalProperty(gpName);
		if (StringUtils.hasText(uuid)) {
			ret = Context.getEncounterService().getEncounterTypeByUuid(uuid);
		}
		if (ret == null) {
			throw new IllegalStateException("Missing global property configuration: " + gpName + " = " + uuid);
		}
		return ret;
	}

	/**
	 * @see OrderExtensionService#getDefaultEncounterRole()
	 */
	@Override
	@Transactional(readOnly=true)
	public EncounterRole getDefaultEncounterRole() {
		EncounterRole role = Context.getEncounterService().getEncounterRoleByName("Unknown");
		if (role == null) {
			throw new IllegalStateException("Missing encounter role named 'Unknown'");
		}
		return role;
	}

	/**
	 * @see OrderExtensionService#getProviderForUser(User)
	 */
	@Override
	@Transactional(readOnly=true)
	public Provider getProviderForUser(User user) {
		ProviderService ps = Context.getProviderService();
		Collection<Provider> providers = ps.getProvidersByPerson(user.getPerson(), true);
		if (providers.isEmpty()) {
			throw new IllegalStateException("User " + user + " has no provider accounts, unable to create orders");
		}
		return providers.iterator().next();
	}

	/**
	 * @see OrderExtensionService#createDrugOrderEncounter(Patient, Date)
	 */
	@Override
	@Transactional
	public Encounter createDrugOrderEncounter(Patient patient, Date encounterDate) {
		Encounter e = new Encounter();
		e.setPatient(patient);
		e.setEncounterType(getDefaultEncounterType());
		e.setEncounterDatetime(encounterDate);
		e.addProvider(getDefaultEncounterRole(), getProviderForUser(Context.getAuthenticatedUser()));
		return Context.getEncounterService().saveEncounter(e);
	}

	/**
	 * @see OrderExtensionService#getExistingDrugOrderEncounter(Patient, Date, User)
	 */
	@Override
	@Transactional(readOnly=true)
	public Encounter getExistingDrugOrderEncounter(Patient patient, Date dateCreated, User creator) {
		return dao.getExistingDrugOrderEncounter(patient, getDefaultEncounterType(), dateCreated, creator);
	}

	/**
	 * @see OrderExtensionService#discontinueOrder(DrugOrder, Concept, Date)
	 */
	@Override
	@Transactional
	public void discontinueOrder(DrugOrder drugOrder, Concept stopConcept, Date stopDateDrug) {
		Date currentDate = new Date();
		User currentUser = Context.getAuthenticatedUser();
		Encounter encounter = getExistingDrugOrderEncounter(drugOrder.getPatient(), currentDate, currentUser);
		if (encounter == null) {
			encounter = createDrugOrderEncounter(drugOrder.getPatient(), currentDate);
		}
		Provider orderer = getProviderForUser(currentUser);
		Context.getOrderService().discontinueOrder(drugOrder, stopConcept, stopDateDrug, orderer, encounter);
	}

	/**
	 * @see OrderExtensionService#extendedSaveDrugOrder(DrugOrder)
	 */
	@Override
	@Transactional
	public DrugOrder extendedSaveDrugOrder(DrugOrder drugOrder) {
		Date currentDate = new Date();
		User currentUser = Context.getAuthenticatedUser();
		if (drugOrder.getEncounter() == null) {
			Encounter encounter = getExistingDrugOrderEncounter(drugOrder.getPatient(), currentDate, currentUser);
			if (encounter == null) {
				Date encDate = drugOrder.getDateActivated() == null ? currentDate : drugOrder.getDateActivated();
				encounter = createDrugOrderEncounter(drugOrder.getPatient(), encDate);
			}
			drugOrder.setEncounter(encounter);
		}
		if (drugOrder.getOrderer() == null) {
			drugOrder.setOrderer(getProviderForUser(currentUser));
		}
		if (drugOrder.getOrderType() == null) {
			drugOrder.setOrderType(OrderEntryUtil.getDrugOrderType());
		}
		if (drugOrder.getCareSetting() == null) {
			drugOrder.setCareSetting(OrderEntryUtil.getDefaultCareSetting());
		}

		return (DrugOrder)Context.getOrderService().saveOrder(drugOrder, new OrderContext());
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
     * @see OrderExtensionService#getFutureDrugOrdersOfSameOrderSet(Patient patient, OrderSet orderSet, Date startDate)
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
		
		if (numCycles == null) {
			numCycles = 1;
		}

		Date currentDate = new Date();
		User currentUser = Context.getAuthenticatedUser();
		Encounter encounter = getExistingDrugOrderEncounter(patient, currentDate, currentUser);
		if (encounter == null) {
			Date encDate = startDate == null || currentDate.before(startDate) ? currentDate : startDate;
			encounter = createDrugOrderEncounter(patient, encDate);
		}
		
		for (int i=0; i<numCycles; i++) {
			
			DrugRegimen orderGroup = new DrugRegimen();
			orderGroup.setPatient(patient);
			orderGroup.setEncounter(encounter);
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

				if (drugMember.getRelativeStartDay() != null) {
					memberStartDate = OrderExtensionUtil.incrementDate(memberStartDate, drugMember.getRelativeStartDay() - 1);
				}

				DrugOrder drugOrder = new DrugOrder();
				drugOrder.setConcept(drugMember.getConcept());
				drugOrder.setDrug(drugMember.getDrug());
				drugOrder.setDose(drugMember.getDose());
				drugOrder.setDoseUnits(drugMember.getDoseUnits());
				drugOrder.setRoute(drugMember.getRoute());
				drugOrder.setDosingInstructions(drugMember.getAdministrationInstructions());
				drugOrder.setFrequency(drugMember.getOrderFrequency());
				drugOrder.setAsNeeded(drugMember.isAsNeeded());
				drugOrder.setInstructions(drugMember.getInstructions());
				Concept indication = drugMember.getIndication();
				if (indication == null) {
					indication = orderSet.getIndication();
				}
				drugOrder.setOrderReason(indication);

				OrderEntryUtil.setStartDate(drugOrder, currentDate, memberStartDate);
				OrderEntryUtil.setEndDate(drugOrder, drugMember.getLengthInDays());

				drugOrder.setOrderGroup(orderGroup);
				drugOrder.setPatient(patient);
				drugOrder.setEncounter(encounter);
				drugOrder.setOrderer(getProviderForUser(currentUser));
				drugOrder.setOrderType(OrderEntryUtil.getDrugOrderType());
				drugOrder.setCareSetting(OrderEntryUtil.getDefaultCareSetting());

				orderGroup.addOrder(drugOrder);
			}

			Context.getService(OrderExtensionService.class).saveDrugRegimen(orderGroup);
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
