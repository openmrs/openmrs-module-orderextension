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

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.orderextension.DrugOrderSetMember;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedDrugOrder;
import org.openmrs.module.orderextension.NestedOrderSetMember;
import org.openmrs.module.orderextension.OrderGroup;
import org.openmrs.module.orderextension.OrderSet;
import org.openmrs.module.orderextension.OrderSetMember;
import org.openmrs.module.orderextension.api.db.OrderExtensionDAO;
import org.openmrs.module.orderextension.util.OrderExtensionUtil;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
	public OrderSet getOrderSet(Integer id) {
		return dao.getOrderSet(id);
	}

	/**
	 * @see OrderExtensionService#getOrderSetByUuid(String)
	 */
	@Override
	@Transactional(readOnly=true)
	public OrderSet getOrderSetByUuid(String uuid) {
		return dao.getOrderSetByUuid(uuid);
	}

	/**
	 * @see OrderExtensionService#getNamedOrderSets(boolean)
	 */
	@Override
	@Transactional(readOnly=true)
	public List<OrderSet> getNamedOrderSets(boolean includeRetired) {
		return dao.getNamedOrderSets(null, null, includeRetired);
	}

	/**
	 * @see OrderExtensionService#findAvailableOrderSets(String, Concept)
	 */
	@Override
	@Transactional(readOnly=true)
	public List<OrderSet> findAvailableOrderSets(String partialName, Concept indication) {
		return dao.getNamedOrderSets(partialName, indication, false);
	}

	/**
	 * @see OrderExtensionService#saveOrderSet(OrderSet)
	 */
	@Override
	@Transactional
	public OrderSet saveOrderSet(OrderSet orderSet) {
		return dao.saveOrderSet(orderSet);
	}

	/**
	 * @see OrderExtensionService#purgeOrderSet(OrderSet)
	 */
	@Override
	@Transactional
	public void purgeOrderSet(OrderSet orderSet) {
		dao.purgeOrderSet(orderSet);
	}

	/**
	 * @see OrderExtensionService#getOrderSetMember(Integer)
	 */
	@Override
	@Transactional(readOnly=true)
	public OrderSetMember getOrderSetMember(Integer id) {
		return dao.getOrderSetMember(id);
	}

	/**
	 * @see OrderExtensionService#getParentOrderSets(OrderSet)
	 */
	@Override
	@Transactional(readOnly=true)
	public List<OrderSet> getParentOrderSets(OrderSet orderSet) {
		return dao.getParentOrderSets(orderSet);
	}

	/**
	 * @see OrderExtensionService#saveOrderGroup(OrderGroup)
	 */
	@Override
	@Transactional
	public <T extends OrderGroup> T saveOrderGroup(T orderGroup) {
		return dao.saveOrderGroup(orderGroup);
	}

	/**
	 * @see OrderExtensionService#getOrderGroups(Patient, Class)
	 */
	@Override
	@Transactional(readOnly=true)
	public <T extends OrderGroup> List<T> getOrderGroups(Patient patient, Class<T> type) {
		return dao.getOrderGroups(patient, type);
	}
	
	/**
	 * @see OrderExtensionService#getOrderGroup(Integer orderGroup)
	 */
	@Override
	@Transactional(readOnly=true)
	public OrderGroup  getOrderGroup(Integer id) {
		return dao.getOrderGroup(id);
	}
	
	/**
	 * @see OrderExtensionService#getDrugRegimen(Integer orderGroup)
	 */
	@Override
	@Transactional(readOnly=true)
	public DrugRegimen  getDrugRegimen(Integer id) {
		
		return dao.getDrugRegimen(id);
	}
	
	/**
     * @see org.openmrs.module.orderextension.api.OrderExtensionService#getMaxNumberOfCyclesForRegimen(Patient, org.openmrs.module.orderextension.DrugRegimen)
     */
    @Override
    public Integer getMaxNumberOfCyclesForRegimen(Patient patient, DrugRegimen regimen) {
	    if(regimen.isCyclical() && regimen.getCycleNumber() != null)
	    {
	    	return dao.getMaxNumberOfCyclesForRegimen(patient, regimen);
	    }
	    return 1;
    }
	
	/**
     * @see org.openmrs.module.orderextension.api.OrderExtensionService#getExtendedDrugOrders(Patient, Concept, Date, Date)
     */
    @Override
    public List<ExtendedDrugOrder> getExtendedDrugOrders(Patient patient, Concept indication, Date startDateAfter, Date startDateBefore) {
	    return dao.getExtendedDrugOrdersForPatient(patient, indication, startDateAfter, startDateBefore);    
    }
    
    /**
     * @see org.openmrs.module.orderextension.api.OrderExtensionService#getFutureDrugOrdersOfSameOrderSet(Patient patient, OrderSet orderSet, Date startDate)
     */
    @Override
    public List<ExtendedDrugOrder> getFutureDrugOrdersOfSameOrderSet(Patient patient, OrderSet orderSet, Date startDate) {
    	List<ExtendedDrugOrder> allOrders = getExtendedDrugOrders(patient, null, startDate, null);
	    
    	List<ExtendedDrugOrder> futureOrders = new ArrayList<ExtendedDrugOrder>();
    	for(ExtendedDrugOrder order: allOrders)
    	{
    		if(order.getGroup() != null && order.getGroup().getOrderSet() != null && order.getGroup().getOrderSet().equals(orderSet) && order.getEffectiveStartDate().after(startDate)) {
    			futureOrders.add(order);
    		}
    	}
    	return futureOrders;
    }
    
    /**
     * @see org.openmrs.module.orderextension.api.OrderExtensionService#getFutureDrugRegimensOfSameOrderSet(Patient patient, DrugRegimen drugRegimen, Date startDate)
     */
    @Override
    public List<DrugRegimen> getFutureDrugRegimensOfSameOrderSet(Patient patient, DrugRegimen drugRegimen, Date startDate) {
    	List<ExtendedDrugOrder> allOrders = getExtendedDrugOrders(patient, null, startDate, null);
	    
    	List<DrugRegimen> futureRegimens = new ArrayList<DrugRegimen>();
    	for(ExtendedDrugOrder order: allOrders)
    	{
    		DrugRegimen regimen = Context.getService(OrderExtensionService.class).getDrugRegimen(order.getGroup().getId());
    		
    		if(regimen.getFirstDrugOrderStartDate().after(drugRegimen.getLastDrugOrderEndDate()) && !futureRegimens.contains(regimen))
    		{
    			futureRegimens.add(regimen);
    		}
    	}
    	return futureRegimens;
    }

	/**
	 * @see OrderExtensionService#addOrdersForPatient(Patient, OrderSet, Date, Integer)
	 */
	@Override
	@Transactional
	public void addOrdersForPatient(Patient patient, OrderSet orderSet, Date startDate, Integer numCycles) {

		OrderExtensionService orderExtSvc = Context.getService(OrderExtensionService.class);
		
		if (numCycles == null) {
			numCycles = 1;
		}
		
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
			for (OrderSetMember member : orderSet.getMembers()) {
				
				Date memberStartDate = cycleStart;
				Date memberEndDate = null;
				if (member.getRelativeStartDay() != null) {
					memberStartDate = OrderExtensionUtil.incrementDate(memberStartDate, member.getRelativeStartDay() - 1);
				}
				if (member.getLengthInDays() != null) {
					memberEndDate = OrderExtensionUtil.incrementDateEndOfDay(memberStartDate, member.getLengthInDays() - 1);
				}
				
				if (member instanceof NestedOrderSetMember) {
					NestedOrderSetMember nestedMember = (NestedOrderSetMember)member;
					addOrdersForPatient(patient, nestedMember.getNestedOrderSet(), memberStartDate, null);
				}
				else if (member instanceof DrugOrderSetMember) {
					DrugOrderSetMember drugMember = (DrugOrderSetMember)member;
					ExtendedDrugOrder drugOrder = new ExtendedDrugOrder();
					drugOrder.setOrderType(drugMember.getOrderType());
					if (drugOrder.getOrderType() == null) {
                        drugOrder.setOrderType(Context.getOrderService().getOrderTypeByConcept(drugOrder.getConcept()));
                    }
                    if (drugOrder.getOrderType() == null) {
					    drugOrder.setOrderType(Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID));
                    }
					drugOrder.setConcept(drugMember.getConcept());
					drugOrder.setDrug(drugMember.getDrug());
					drugOrder.setDose(drugMember.getDose());
					drugOrder.setDoseUnits(drugMember.getUnits());
					drugOrder.setRoute(drugMember.getRoute());
					drugOrder.setAdministrationInstructions(drugMember.getAdministrationInstructions());
					drugOrder.setFrequency(drugMember.getFrequency());
					drugOrder.setAsNeeded(drugMember.isAsNeeded());
					drugOrder.setInstructions(drugMember.getInstructions());
					Concept indication = drugMember.getIndication();
					if (indication == null) {
						indication = orderSet.getIndication();
					}
					drugOrder.setIndication(indication);
					drugOrder.setDateActivated(startDate);
					drugOrder.setScheduledDate(memberStartDate);
					drugOrder.setAutoExpireDate(memberEndDate);
					if (drugOrder.getAutoExpireDate() == null) {
                        drugOrder.setAutoExpireDateBasedOnDuration();
                    }
                    if (drugOrder.getAutoExpireDate() != null) {
					    drugOrder.setAutoExpireDate(getEndOfDayIfNoTimeSpecified(drugOrder.getAutoExpireDate()));
                    }
					drugOrder.setGroup(orderGroup);
					drugOrder.setPatient(patient);

                    setOrderNumber(drugOrder);
					drugOrder.setCareSetting(Context.getOrderService().getCareSettingByName("INPATIENT")); // TODO

                    // TODO: Figure out how to create or amend an appropriate encounter
                    Encounter encounter = new Encounter();
                    encounter.setEncounterDatetime(new Date());
                    encounter.setEncounterType(Context.getEncounterService().getEncounterType(1));
                    encounter.setPatient(patient);
                    encounter.setLocation(Context.getLocationService().getLocation("Unknown Location"));
                    encounter = Context.getEncounterService().saveEncounter(encounter);
                    drugOrder.setEncounter(encounter);

                    drugOrder.setOrderer(Context.getProviderService().getProvider(1)); // TODO: Set this properly

					orderGroup.addMember(drugOrder);
				}
				else {
					throw new APIException("We do not yet handle TestOrders");
				}
			}
			
			orderExtSvc.saveOrderGroup(orderGroup);
		}
	}

    @Override
    public List<DrugOrder> getDrugOrdersForPatient(Patient patient) {
        List<DrugOrder> drugOrders = new ArrayList<DrugOrder>();
        for (Order o : Context.getOrderService().getAllOrdersByPatient(patient)) {
            if (o instanceof DrugOrder) {
                drugOrders.add((DrugOrder)o);
            }
        }
        return drugOrders;
    }

    @Override
    public List<DrugOrder> getNonGroupedDrugOrdersForPatient(Patient patient) {
        List<DrugOrder> drugOrders = getDrugOrdersForPatient(patient);
        List<DrugRegimen> regimens = getOrderGroups(patient, DrugRegimen.class);
        for (DrugRegimen r : regimens) {
            drugOrders.removeAll(r.getMembers());
        }
        return drugOrders;
    }

    private Date getEndOfDayIfNoTimeSpecified(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);
        int milliseconds = cal.get(Calendar.MILLISECOND);
        //roll autoExpireDate to end of day (23:59:59:999) if no time portion is specified
        if (hours == 0 && minutes == 0 && seconds == 0 && milliseconds == 0) {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
        }
        return cal.getTime();
    }

    private void setOrderNumber(ExtendedDrugOrder order) {
        Boolean isAccessible = null;
        Field field = null;
        try {
            field = Order.class.getDeclaredField("orderNumber");
            field.setAccessible(true);
            field.set(order, "ORD-" + Context.getOrderService().getNextOrderNumberSeedSequenceValue());
        }
        catch (Exception e) {
            throw new APIException("Failed to set order number for order:" + order, e);
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
