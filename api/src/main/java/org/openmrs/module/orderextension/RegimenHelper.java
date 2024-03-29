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

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderGroup;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.openmrs.module.orderextension.util.OrderExtensionUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for classifying Regimens
 */
public class RegimenHelper {

	private List<OrderGroup> cycles = new ArrayList<OrderGroup>();
	private List<OrderGroup> drugGroups = new ArrayList<OrderGroup>();
	private Map<OrderGroup, List<DrugOrder>> ordersInDrugGroups = new HashMap<OrderGroup, List<DrugOrder>>();
	
	private boolean hasCycles = false;
	private boolean hasDrugGroups = false;
	private boolean hasOtherMedications = false;
	
	public RegimenHelper(List<DrugOrder> drugOrders)
	{	
		for(DrugOrder o : drugOrders)
		{
			OrderGroup group = OrderEntryUtil.getOrderGroup(o);
			boolean isCyclical = OrderEntryUtil.isCyclical(group);
			
			if (group != null) {
				if (isCyclical) {
					if (!cycles.contains(group)) {
						cycles.add(group);
					}
					hasCycles = true;
				}
				else {
					if (!drugGroups.contains(group)) {
						drugGroups.add(group);
					}
					hasDrugGroups = true;
				}
			}
			else
			{	
				hasOtherMedications = true;
			}
			List<DrugOrder> ordersInGroup = ordersInDrugGroups.get(group);
			if (ordersInGroup == null) {
				ordersInGroup = new ArrayList<DrugOrder>();
				ordersInDrugGroups.put(group, ordersInGroup);
			}
			ordersInGroup.add(o);
		}
		
		Collections.sort(cycles, new DrugGroupSorter());
		Collections.sort(drugGroups, new DrugGroupSorter());
	}
	
	public List<OrderGroup> getCycleList()
	{	
		return cycles;
	}
	
	public List<OrderGroup> getDrugGroupList()
	{	
		return drugGroups;
	}
	
	public List<DrugOrder> getOtherMedications()
	{
		return ordersInDrugGroups.get(null);
	}
	
    public boolean getHasCycles() {
    	return hasCycles;
    }
	
    public boolean getHasDrugGroups() {
    	return hasDrugGroups;
    }

    public boolean isHasOtherMedications() {
    	return hasOtherMedications;
    }

	public List<Concept> getClassifications(DrugRegimen drugGroup, final Concept topLevelIndicator, Integer day)
	{
		List<Concept> classifications = new ArrayList<Concept>();
		
		for(DrugOrder o : ordersInDrugGroups.get(drugGroup))
		{		
			Concept classification = null;
			if( drugGroup == null ||
				getCycleDay(drugGroup.getFirstDrugOrderStartDate(), o.getEffectiveStartDate()).equals(day) ) {
				classification = o.getOrderReason();
			}

			if (!classifications.contains(classification)) {
				classifications.add(classification);
			}
		}
		
		if (topLevelIndicator != null) {
			Collections.sort(classifications, new Comparator<Concept>() {
	            public int compare(Concept c1, Concept c2) {
	            	List<Concept> setMembers = topLevelIndicator.getSetMembers();
	            	int index1 = setMembers.indexOf(c1);
	            	int index2 = setMembers.indexOf(c2);
	            	return index1 - index2;
	            }
	        });
		}
		
		//TODO: will need to be able to sort these based on a predetermined order
		
		return classifications;
	}
	
	public List<Integer> getDrugStartDates(DrugRegimen drugGroup)
	{
		List<Integer> startDays = new ArrayList<Integer>();
		startDays.add(1);
		
		if(drugGroup != null)
		{
			Date startDate = drugGroup.getFirstDrugOrderStartDate();
			
			for(Order od: OrderExtensionUtil.getOrdersInGroup(drugGroup))
			{
				if(od.getEffectiveStartDate() != null && startDate != null)
				{
					long cycleDay = od.getEffectiveStartDate().getTime() - startDate.getTime();
					if(cycleDay > 0)
					{
						cycleDay = cycleDay/86400000;
						cycleDay = cycleDay + 1;
						
						Integer day = (int)cycleDay;
						
						if(!startDays.contains(day))
						{
							startDays.add(day);
						}
					}
				}
			}
		}
		
		Collections.sort(startDays);
		return startDays;
	}
	
	
	
	public List<DrugOrder> getRegimensForClassification(DrugRegimen cycle, Concept classification, Integer day)
	{
		List<DrugOrder> ret = new ArrayList<DrugOrder>();
		
		for(DrugOrder o : ordersInDrugGroups.get(cycle))
		{
			if(cycle == null || (cycle != null && getCycleDay(cycle.getFirstDrugOrderStartDate(), o.getEffectiveStartDate()).equals(day)))
			{
				Concept orderIndication = o.getOrderReason();
				if(classification == null && orderIndication == null)
				{
					ret.add(o);
				}
				else if(classification != null && orderIndication != null && orderIndication.equals(classification))
				{
					ret.add(o);
				}
			}
		}

		return ret;
	}
	
	public Date getCycleDate(DrugRegimen cycle, Integer day)
	{
		Date cycleStart = cycle.getFirstDrugOrderStartDate();
		Calendar cycleDate = Calendar.getInstance();
		cycleDate.setTime(cycleStart);
		cycleDate.add(Calendar.DAY_OF_YEAR, day-1);
		return cycleDate.getTime();
	}
	
	private class DrugGroupSorter implements Comparator<OrderGroup>
	{
		public int compare(OrderGroup c1, OrderGroup c2) {
            Date d1 = OrderExtensionUtil.getFirstDrugOrderStartDate(c1);
            Date d2 = OrderExtensionUtil.getFirstDrugOrderStartDate(c2);
            return d1.compareTo(d2);
         }
	}
	
	private Integer getCycleDay(Date firstDrugStart, Date drugStart)
	{
		if(firstDrugStart != null && drugStart != null)
		{
			long cycleDay = drugStart.getTime() - firstDrugStart.getTime();
			if(cycleDay > 0)
			{
				cycleDay = cycleDay/86400000;
				cycleDay = cycleDay + 1;
				return (int)cycleDay;
			}
		}
		
		return 1;
	}
}
