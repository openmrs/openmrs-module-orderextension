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
package org.openmrs.module.orderextension.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.OpenmrsMetadata;

/**
 * Defines any utility methods
 */
public class OrderExtensionUtil  {

	/**
	 * @return the passed object formatted as a String, optionally given the passed format
	 */
	public static String format(Object o, String format) {
		if (o != null) {
			if (o instanceof DrugOrder) {
				DrugOrder drugOrder = (DrugOrder)o;
				
				if ("route".equals(format)) {
					if (drugOrder.getRoute() != null) {
						return format(drugOrder.getRoute(), null);
					}
					return "";
				}
				
				if("length".equals(format)){
					if(drugOrder.getEffectiveStopDate() != null) {
						return calculateDaysDifference(drugOrder.getEffectiveStopDate(), drugOrder.getEffectiveStartDate());
					}
					return "";
				}
				
				if ("administrationInstructions".equals(format)) {
					return drugOrder.getDosingInstructions();
				}
				
				if (drugOrder.getDrug() != null) {
					return format(drugOrder.getDrug(), null);
				}
				else {
					return format(drugOrder.getConcept(), null);
				}
			}
			else if (o instanceof Concept) {
				Concept c = (Concept)o;
				return c.getDisplayString();
			}
			else if (o instanceof OpenmrsMetadata) {
				OpenmrsMetadata m = (OpenmrsMetadata)o;
				return m.getName();
			}
			else if (o instanceof Double)
			{
				final DecimalFormat f = new DecimalFormat("0.###");
				return f.format(o);
			}
			
			return o.toString();
		}
		return "";
	}

	public static Date startOfDay(Date d) {
		if (d != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			return c.getTime();
		}
		return null;
	}

	public static Date endOfDay(Date d) {
		if (d != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(startOfDay(d));
			c.add(Calendar.DATE, 1);
			c.add(Calendar.MILLISECOND, -1);
		}
		return null;
	}
	
	/**
	 * @return a Date that is the number of days passed in relative to the date passed in
	 */
	public static Date incrementDate(Date d, Integer increment) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, increment);
		return c.getTime();
	}
	
	public static Date incrementDateEndOfDay(Date d, Integer increment) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, increment);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		return c.getTime();
	}

	public static Date adjustDateToEndOfDay(Date dateToAdjust) {
		if (dateToAdjust != null) {
			Calendar adjusted = Calendar.getInstance();
			adjusted.setTime(dateToAdjust);
			adjusted.set(Calendar.HOUR, 23);
			adjusted.set(Calendar.MINUTE, 59);
			return adjusted.getTime();
		}
		return dateToAdjust;
	}

	public static boolean sameDate(Date dateA, Date dateB) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return dateA != null && dateB != null && df.format(dateA).equals(df.format(dateB));
	}
	
	public static String calculateDaysDifference(Date observation, Date startingDate)
	{
		long milis1 = observation.getTime();
		long milis2 = startingDate.getTime();
		
		long diff = milis1 - milis2;
		
		long diffDays = diff / (24 * 60 * 60 * 1000);
		
	
		return Integer.toString((int)diffDays + 1);
	}

	public static String calculationDurationFromAutoExpiryDate(DrugOrder drugOrder) {
		Date sd = drugOrder.getEffectiveStartDate();
		Date ed = drugOrder.getAutoExpireDate();
		if (ed == null) {
			return null;
		}
		return OrderExtensionUtil.calculateDaysDifference(ed, sd);
	}

	public static boolean orderablesMatch(DrugOrder o1, DrugOrder o2) {
		if (o1.getDrug() != null && o2.getDrug() != null && o1.getDrug().equals(o2.getDrug())) {
			return true;
		}
		return o1.getConcept() != null && o2.getConcept() != null && o1.getConcept().equals(o2.getConcept());
	}

	public static boolean reasonsMatch(DrugOrder o1, DrugOrder o2) {
		Concept r1 = o1.getOrderReason();
		Concept r2 = o2.getOrderReason();
		if (r1 == null && r2 == null) {
			return true;
		}
		return r1 != null && r2 != null && r1.equals(r2);
	}
}
