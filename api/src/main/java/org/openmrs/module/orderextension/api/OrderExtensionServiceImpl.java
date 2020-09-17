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
import org.openmrs.Order;
import org.openmrs.OrderGroup;
import org.openmrs.OrderSet;
import org.openmrs.OrderSetMember;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.OrderContext;
import org.openmrs.api.OrderService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.ExtendedOrderSetMember;
import org.openmrs.module.orderextension.api.db.OrderExtensionDAO;
import org.openmrs.module.orderextension.util.DrugOrderTemplate;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.orderextension.util.OrderExtensionUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Core implementation of the OrderExensionService
 */
@Transactional
public class OrderExtensionServiceImpl extends BaseOpenmrsService implements OrderExtensionService {

	protected OrderDAO orderDao;
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
	 * @see OrderExtensionService#ensureDrugOrderEncounter(Patient, Encounter, Date)
	 */
	@Override
	@Transactional
	public Encounter ensureDrugOrderEncounter(Patient patient, Encounter encounter, Date encounterDate) {
		Date today = new Date();
		if (encounterDate.after(today)) {
			encounterDate = today;
		}
		if (encounter == null) {
			encounter = dao.getExistingDrugOrderEncounter(patient, getDefaultEncounterType(), encounterDate);
		}
		if (encounter == null) {
			encounter = new Encounter();
			encounter.setPatient(patient);
			encounter.setEncounterType(getDefaultEncounterType());
			encounter.setEncounterDatetime(encounterDate);
			encounter.addProvider(getDefaultEncounterRole(), getProviderForUser(Context.getAuthenticatedUser()));
			encounter = Context.getEncounterService().saveEncounter(encounter);
		}
		else {
			encounter.setEncounterDatetime(encounterDate);
		}
		return encounter;
	}

	/**
	 * @see OrderExtensionService#extendedSaveDrugOrder(DrugOrder)
	 */
	@Override
	@Transactional
	public DrugOrder extendedSaveDrugOrder(DrugOrderTemplate drugOrderTemplate, boolean includeCycles) {
		Patient patient = drugOrderTemplate.getPatient();
		if (drugOrderTemplate.getRegimen() != null && includeCycles) {
			DrugRegimen regimen = drugOrderTemplate.getRegimen();
			int daysIntoRegimen = OrderExtensionUtil.daysDiff(regimen.getFirstDrugOrderStartDate(), drugOrderTemplate.getStartDate());

			List<DrugRegimen> futureRegimens = getOrderExtensionService().getFutureDrugRegimensOfSameOrderSet(
					patient, regimen, regimen.getFirstDrugOrderStartDate()
			);

			for (DrugRegimen futureRegimen : futureRegimens) {
				DrugOrder drugOrder = drugOrderTemplate.toDrugOrder();
				Date startDate = OrderExtensionUtil.adjustDate(futureRegimen.getFirstDrugOrderStartDate(), daysIntoRegimen);
				OrderEntryUtil.setStartDate(drugOrder, startDate);
				drugOrder.setOrderGroup(futureRegimen);
				getOrderExtensionService().extendedSaveDrugOrder(drugOrder);
			}
		}

		DrugOrder drugOrder = drugOrderTemplate.toDrugOrder();
		return getOrderExtensionService().extendedSaveDrugOrder(drugOrder);
	}

	/**
	 * @see OrderExtensionService#extendedSaveDrugOrder(DrugOrder)
	 */
	@Override
	@Transactional
	public DrugOrder extendedSaveDrugOrder(DrugOrder drugOrder) {

		// First ensure that the encounter is set correctly
		Patient patient = drugOrder.getPatient();
		Date encounterDate = drugOrder.getDateActivated();
		Encounter encounter = drugOrder.getEncounter();
		if (drugOrder.getOrderGroup() != null) {
			OrderGroup regimen = HibernateUtil.getRealObjectFromProxy(drugOrder.getOrderGroup());
			encounter = (encounter == null ? regimen.getEncounter() : encounter);
			encounterDate = OrderExtensionUtil.getFirstDrugOrderDateActivated(regimen);
		}
		encounter = ensureDrugOrderEncounter(patient, encounter, encounterDate);
		drugOrder.setEncounter(encounter);

		// Now ensure other defaults are set correctly if not specified
		if (drugOrder.getOrderer() == null) {
			drugOrder.setOrderer(getProviderForUser(Context.getAuthenticatedUser()));
		}
		if (drugOrder.getOrderType() == null) {
			drugOrder.setOrderType(OrderEntryUtil.getDrugOrderType());
		}
		if (drugOrder.getCareSetting() == null) {
			drugOrder.setCareSetting(OrderEntryUtil.getDefaultCareSetting());
		}

		// Create an order context for saving this drug order.  This requires ensuring that if this is part of a regimen,
		// Order Service does not allow multiple orders of the same orderable unless you explicitly allow
		// Given we have some order sets that have the same orderable in sequence, we need to allow this here.
		OrderContext orderContext = new OrderContext();
		if (drugOrder.getOrderGroup() != null) {
			String[] groupOrderUuids = new String[drugOrder.getOrderGroup().getOrders().size()];
			for (int i = 0; i < drugOrder.getOrderGroup().getOrders().size(); i++) {
				groupOrderUuids[i] = drugOrder.getOrderGroup().getOrders().get(i).getUuid();
			}
			orderContext.setAttribute(OrderService.PARALLEL_ORDERS, groupOrderUuids);
		}

		return (DrugOrder)Context.getOrderService().saveOrder(drugOrder, orderContext);
	}

	/**
	 * @see OrderExtensionService#saveDrugRegimen(DrugRegimen)
	 */
	@Override
	@Transactional
	public DrugRegimen saveDrugRegimen(DrugRegimen drugRegimen) {

		// First ensure that the encounter is set correctly
		Patient patient = drugRegimen.getPatient();
		Encounter encounter = drugRegimen.getEncounter();
		Date encounterDate = OrderExtensionUtil.getFirstDrugOrderDateActivated(drugRegimen);
		encounter = ensureDrugOrderEncounter(patient, encounter, encounterDate);
		drugRegimen.setEncounter(encounter);

		drugRegimen = (DrugRegimen) orderDao.saveOrderGroup(drugRegimen);

		// Now ensure underlying orders are set correctly
		for (Order order : drugRegimen.getOrders()) {
			DrugOrder drugOrder = (DrugOrder)order;
			drugOrder = getOrderExtensionService().extendedSaveDrugOrder(drugOrder);
		}

		return drugRegimen;
	}

	@Override
	@Transactional
	public void saveDrugRegimens(List<DrugRegimen> drugRegimens) {
		for (DrugRegimen drugRegimen : drugRegimens) {
			getOrderExtensionService().saveDrugRegimen(drugRegimen);
		}
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
		OrderGroup group = Context.getOrderService().getOrderGroup(id);
		return (DrugRegimen) HibernateUtil.getRealObjectFromProxy(group);
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

	@Override
	@Transactional
	public void updateOrderStartAndEndDates(DrugOrder oldOrder, int daysDiff, String reason) {
		if (daysDiff != 0) {
			Date newStartDate = OrderExtensionUtil.adjustDate(oldOrder.getEffectiveStartDate(), daysDiff);
			Date newAutoExpireDate = null;
			if (oldOrder.getAutoExpireDate() != null) {
				newAutoExpireDate = OrderExtensionUtil.adjustDate(oldOrder.getAutoExpireDate(), daysDiff);
			};
			DrugOrder newOrder = getOrderExtensionService().cloneAndVoidPrevious(oldOrder, reason);
			OrderEntryUtil.setStartDate(newOrder, newStartDate);
			newOrder.setAutoExpireDate(newAutoExpireDate);
			getOrderExtensionService().extendedSaveDrugOrder(newOrder);
		}
	}

	@Override
	@Transactional
	public DrugOrder cloneAndVoidPrevious(DrugOrder orderToVoid, String reason) {
		DrugOrder newOrder = orderToVoid.cloneForRevision();
		if (newOrder.getOrderGroup() != null && !newOrder.getOrderGroup().getOrders().contains(newOrder)) {
			newOrder.getOrderGroup().addOrder(newOrder);
		}
		newOrder.setAction(Order.Action.NEW);
		newOrder.setPreviousOrder(null);
		Context.getOrderService().voidOrder(orderToVoid, reason);
		return newOrder;
	}

	@Override
	@Transactional
	public void changeStartDateOfGroup(Patient patient, DrugRegimen regimen, Date changeDate, boolean includeCycles) {
    	Date firstDate = regimen.getFirstDrugOrderStartDate();
		int daysDiff = OrderExtensionUtil.daysDiff(firstDate, changeDate);
		if (daysDiff != 0) {
			if (includeCycles) {
				List<DrugRegimen> futureOrders = getOrderExtensionService().getFutureDrugRegimensOfSameOrderSet(
						patient, regimen, firstDate
				);
				for (DrugRegimen drugRegimen : futureOrders) {
					for (DrugOrder order : drugRegimen.getMembers()) {
						getOrderExtensionService().updateOrderStartAndEndDates(order, daysDiff, "Changing start date of order group");
					}
				}
			}
			for (DrugOrder order : regimen.getMembers()) {
				getOrderExtensionService().updateOrderStartAndEndDates(order, daysDiff, "Changing start date of order group");
			}
		}
	}

	@Override
	@Transactional
	public void changeStartDateOfPartGroup(Patient patient, DrugRegimen regimen, Date changeDate, Integer cycleDay,
			boolean includeCycles, boolean includePartCycles, boolean includeThisCycle) {

		Date startDate = OrderExtensionUtil.getCycleDate(regimen.getFirstDrugOrderStartDate(), cycleDay);
		int days = OrderExtensionUtil.daysDiff(startDate, changeDate);

		if (includeCycles || includePartCycles) {

			List<DrugRegimen> futureOrders = getOrderExtensionService()
					.getFutureDrugRegimensOfSameOrderSet(patient, regimen, regimen.getFirstDrugOrderStartDate());

			for (DrugRegimen drugRegimen : futureOrders) {
				for (DrugOrder order : drugRegimen.getMembers()) {
					if (includeCycles) {
						int cd = OrderExtensionUtil.getCycleDay(drugRegimen.getFirstDrugOrderStartDate(), order.getEffectiveStartDate());
						if (cd == cycleDay) {
							updateOrderStartAndEndDates(order, days, "Changing start date of part of order group");
						}
					}
					else {
						updateOrderStartAndEndDates(order, days, "Changing start date of part of order group");
					}
				}
			}
		}

		for (DrugOrder order : regimen.getMembers()) {
			int cd = OrderExtensionUtil.getCycleDay(regimen.getFirstDrugOrderStartDate(), order.getEffectiveStartDate());
			if (includeThisCycle || includePartCycles) {
				if (cd >= cycleDay) {
					updateOrderStartAndEndDates(order, days, "Changing start date of part of order group");
				}
			}
			else {
				if (cd == cycleDay) {
					updateOrderStartAndEndDates(order, days, "Changing start date of part of order group");
				}
			}
		}
	}

	@Override
	@Transactional
	public DrugOrder changeDrugOrder(DrugOrder drugOrder, DrugOrderTemplate drugOrderTemplate, String changeReason, boolean includeCycles) {
		OrderGroup regimen = OrderEntryUtil.getOrderGroup(drugOrder);

		if (includeCycles) {

			List<DrugOrder> futureOrders = getOrderExtensionService().getFutureDrugOrdersOfSameOrderSet(
					drugOrder.getPatient(), regimen.getOrderSet(), OrderExtensionUtil.getFirstDrugOrderStartDate(regimen)
			);
			for (DrugOrder order : futureOrders) {
				if (OrderExtensionUtil.orderablesMatch(order, drugOrder)) {
					//assuming that the same drug won't appear twice in the same indication within a cycle and that you would want to change the dose on one
					if (OrderExtensionUtil.reasonsMatch(order, drugOrder)) {
						Date startDate = order.getEffectiveStartDate();
						Date startDateDrug = drugOrderTemplate.getStartDate();
						if (drugOrder.getEffectiveStartDate().getTime() != startDateDrug.getTime()) {
							startDate = OrderExtensionUtil.adjustDate(startDate, OrderExtensionUtil.daysDiff(drugOrder.getEffectiveStartDate(), startDateDrug));
						}
						DrugOrder newOrder = cloneAndVoidPrevious(order, changeReason);
						if (drugOrder.getOrderType() == null) {
							drugOrder.setOrderType(OrderEntryUtil.getDrugOrderType());
						}
						newOrder.setDrug(drugOrderTemplate.getDrug());
						newOrder.setConcept(drugOrderTemplate.getDrug().getConcept());
						newOrder.setOrderReason(drugOrderTemplate.getOrderReason());
						newOrder.setDose(drugOrderTemplate.getDose());
						newOrder.setDoseUnits(drugOrderTemplate.getDoseUnits());
						newOrder.setFrequency(drugOrderTemplate.getFrequency());
						OrderEntryUtil.setStartDate(newOrder, startDate);
						OrderEntryUtil.setEndDate(newOrder, drugOrderTemplate.getDuration());
						newOrder.setAsNeeded(drugOrderTemplate.isAsNeeded());
						newOrder.setRoute(drugOrderTemplate.getRoute());
						drugOrder.setDosingInstructions(drugOrderTemplate.getAdminInstructions());
						drugOrder.setInstructions(drugOrderTemplate.getInstructions());
						getOrderExtensionService().extendedSaveDrugOrder(newOrder);
					}
				}
			}
		}

		DrugOrder newOrder = cloneAndVoidPrevious(drugOrder, changeReason);
		if (newOrder.getOrderType() == null) {
			newOrder.setOrderType(OrderEntryUtil.getDrugOrderType());
		}
		newOrder.setDrug(drugOrderTemplate.getDrug());
		newOrder.setConcept(drugOrderTemplate.getDrug().getConcept());
		newOrder.setOrderReason(drugOrderTemplate.getOrderReason());
		newOrder.setDose(drugOrderTemplate.getDose());
		newOrder.setDoseUnits(drugOrderTemplate.getDoseUnits());
		newOrder.setFrequency(drugOrderTemplate.getFrequency());
		OrderEntryUtil.setStartDate(newOrder, drugOrderTemplate.getStartDate());
		OrderEntryUtil.setEndDate(newOrder, drugOrderTemplate.getDuration());
		newOrder.setAsNeeded(drugOrderTemplate.isAsNeeded());
		newOrder.setRoute(drugOrderTemplate.getRoute());
		drugOrder.setDosingInstructions(drugOrderTemplate.getAdminInstructions());
		drugOrder.setInstructions(drugOrderTemplate.getInstructions());

		return getOrderExtensionService().extendedSaveDrugOrder(newOrder);
	}

	@Override
	@Transactional
	public void stopDrugOrder(Patient patient, DrugOrder drugOrder, Date stopDate, Concept stopReason, boolean includeCycles) {
    	if (includeCycles) {
			OrderGroup regimen = drugOrder.getOrderGroup();

			List<DrugOrder> futureOrders = getOrderExtensionService().getFutureDrugOrdersOfSameOrderSet(
					patient, regimen.getOrderSet(), OrderExtensionUtil.getFirstDrugOrderStartDate(regimen)
			);

			String stopReasonStr = stopReason.getDisplayString();

			for (DrugOrder order : futureOrders) {
				if (order.getDrug() != null && drugOrder.getDrug() != null) {
					if (order.getDrug().equals(drugOrder.getDrug())) {
						Context.getOrderService().voidOrder(order, stopReasonStr);
					}
				}
				else if (order.getConcept() != null && drugOrder.getConcept() != null) {
					if (order.getConcept().equals(drugOrder.getConcept())) {
						Context.getOrderService().voidOrder(order, stopReasonStr);
					}
				}
			}
		}

		Date discontinueDate = stopDate;
		Date now = new Date();
		if (OrderExtensionUtil.sameDate(discontinueDate, now)) {
			discontinueDate = new Date();
		}
		else if (OrderExtensionUtil.sameDate(drugOrder.getEffectiveStartDate(), discontinueDate)) {
			discontinueDate = OrderExtensionUtil.adjustDateToEndOfDay(stopDate);
		}

		Encounter encounter = ensureDrugOrderEncounter(patient, null, discontinueDate);
		Provider orderer = getProviderForUser(Context.getAuthenticatedUser());
		Context.getOrderService().discontinueOrder(drugOrder, stopReason, discontinueDate, orderer, encounter);
	}

	@Override
	@Transactional
	public void stopDrugOrdersInGroup(Patient patient, DrugRegimen regimen, Date stopDate, Concept stopReason, boolean includeCycles) {
		if (includeCycles) {
			List<DrugOrder> futureOrders = getOrderExtensionService().getFutureDrugOrdersOfSameOrderSet(
					patient, regimen.getOrderSet(), regimen.getLastDrugOrderEndDate()
			);
			for (DrugOrder order : futureOrders) {
				Context.getOrderService().voidOrder(order, stopReason.getDisplayString());
			}
		}
		Encounter encounter = ensureDrugOrderEncounter(patient, null, stopDate);
		Provider orderer = getProviderForUser(Context.getAuthenticatedUser());

		for (DrugOrder order : regimen.getMembers()) {
			if (OrderEntryUtil.isCurrent(order)) {
				Context.getOrderService().discontinueOrder(order, stopReason, stopDate, orderer, encounter);
			}
			else if (OrderEntryUtil.isFuture(order)) {
				Context.getOrderService().voidOrder(order, stopReason.getDisplayString());
			}
		}
	}

	@Override
	@Transactional
	public void voidDrugOrder(Patient patient, DrugOrder drugOrder, String voidReason, boolean includeCycles) {
		if (includeCycles) {
			OrderGroup regimen = drugOrder.getOrderGroup();
			List<DrugOrder> futureOrders = getOrderExtensionService().getFutureDrugOrdersOfSameOrderSet(
					patient, regimen.getOrderSet(), OrderExtensionUtil.getFirstDrugOrderStartDate(regimen)
			);
			for (DrugOrder order : futureOrders) {
				if (order.getDrug() != null && drugOrder.getDrug() != null) {
					if (order.getDrug().equals(drugOrder.getDrug())) {
						Context.getOrderService().voidOrder(order, voidReason);
					}
				}
				else if (order.getConcept() != null && drugOrder.getConcept() != null) {
					if (order.getConcept().equals(drugOrder.getConcept())) {
						Context.getOrderService().voidOrder(order, voidReason);
					}
				}
			}
		}
		Context.getOrderService().voidOrder(drugOrder, voidReason);
	}

	@Override
	@Transactional
	public void voidDrugOrdersInGroup(Patient patient, DrugRegimen regimen, String voidReason, boolean includeCycles) {
		if (includeCycles) {
			List<DrugOrder> futureOrders = getOrderExtensionService().getFutureDrugOrdersOfSameOrderSet(
					patient, regimen.getOrderSet(), regimen.getLastDrugOrderEndDate()
			);
			for (DrugOrder order : futureOrders) {
				Context.getOrderService().voidOrder(order, voidReason);
			}
		}
		for (DrugOrder order : regimen.getMembers()) {
			Context.getOrderService().voidOrder(order, voidReason);
		}
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
			    DrugRegimen regimen = getOrderExtensionService().getDrugRegimen(g.getId());
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
	public List<DrugRegimen> addOrdersForPatient(Patient patient, ExtendedOrderSet orderSet, Date startDate, Integer numCycles) {

		List<DrugRegimen> ret = new ArrayList<DrugRegimen>();

		if (numCycles == null) {
			numCycles = 1;
		}

		Date currentDate = new Date();
		User currentUser = Context.getAuthenticatedUser();
		
		for (int i=0; i<numCycles; i++) {
			
			DrugRegimen orderGroup = new DrugRegimen();
			orderGroup.setPatient(patient);
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
				drugOrder.setOrderer(getProviderForUser(currentUser));
				drugOrder.setOrderType(OrderEntryUtil.getDrugOrderType());
				drugOrder.setCareSetting(OrderEntryUtil.getDefaultCareSetting());

				orderGroup.addOrder(drugOrder);
			}

			orderGroup = getOrderExtensionService().saveDrugRegimen(orderGroup);
			ret.add(orderGroup);
		}
		return ret;
	}

	protected OrderExtensionService getOrderExtensionService() {
		return Context.getService(OrderExtensionService.class);
	}

	public OrderExtensionDAO getDao() {
		return dao;
	}

	public void setDao(OrderExtensionDAO dao) {
		this.dao = dao;
	}

	public OrderDAO getOrderDao() {
		return orderDao;
	}

	public void setOrderDao(OrderDAO orderDao) {
		this.orderDao = orderDao;
	}
}
