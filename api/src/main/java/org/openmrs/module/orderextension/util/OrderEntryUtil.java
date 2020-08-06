package org.openmrs.module.orderextension.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.CareSetting;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.api.OrderExtensionService;

/**
 * This is a new class, created during the upgrade from 1.9 to 2.3 data model, to encapsulate methods needed
 * in order to support this migration effort, either because they were removed from core, or because there are
 * adaptations needed.
 */
public class OrderEntryUtil {

	/**
	 * Needed due to removal of the drug order type hard-coded primary key in core
	 */
	public static OrderType getDrugOrderType() {
		return Context.getOrderService().getOrderType(2); // TODO: Needs review and improvement to ensure correct
	}

	/**
	 * @return the first found outpatient care setting, or first found care setting if no outpatient ones found
	 */
	public static CareSetting getDefaultCareSetting() {
		CareSetting.CareSettingType outpatient = CareSetting.CareSettingType.OUTPATIENT;
		List<CareSetting> careSettings = Context.getOrderService().getCareSettings(false);
		if (careSettings.isEmpty()) {
			throw new IllegalStateException("No care settings defined, unable to create Orders");
		}
		for (CareSetting cs : careSettings) {
			if (cs.getCareSettingType() == outpatient) {
				return cs;
			}
		}
		return careSettings.get(0);
	}

	/**
	 * Needed due to removal of Context.getOrderService().getDrugOrdersByPatient(patient); method
	 * This may need to be improved for performance, etc. but for now should do the trick.
	 */
	public static List<DrugOrder> getDrugOrdersByPatient(Patient patient) {
		List<DrugOrder> l = new ArrayList<DrugOrder>();
		for (Order o : Context.getOrderService().getAllOrdersByPatient(patient)) {
			if (o instanceof DrugOrder) {
				DrugOrder drugOrder = (DrugOrder)o;
				if (drugOrder.getAction() != Order.Action.DISCONTINUE ) {
					l.add((DrugOrder)o);
				}
			}
		}
		return l;
	}

	/**
	 * Needed due to the fact that frequency is changed from String to OrderFrequency reference in 2.3
	 */
	public static void setFrequency(DrugOrder drugOrder, String frequency) {
		// TODO
	}

	/**
	 * Set the start date for orders placed as of now
	 */
	public static void setStartDate(DrugOrder drugOrder, Date startDate) {
		setStartDate(drugOrder, new Date(), startDate);
	}

	/**
	 * Needed due to removal of start date from Drug Order
	 */
	public static void setStartDate(DrugOrder drugOrder, Date orderDate, Date startDate) {
		Date orderDateNoTime = OrderExtensionUtil.startOfDay(orderDate);
		Date startDateNoTime = OrderExtensionUtil.startOfDay(startDate);
		if (startDateNoTime.after(orderDateNoTime)) {
			drugOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
			drugOrder.setScheduledDate(startDate);
		}
		else {
			drugOrder.setUrgency(Order.Urgency.ROUTINE);
			drugOrder.setDateActivated(startDate);
		}
	}

	public static boolean isOrdered(Order drugOrder) {
		return !drugOrder.getVoided() && drugOrder.getAction() != Order.Action.DISCONTINUE;
	}

	public static boolean isPast(Order drugOrder) {
		Date now = new Date();
		return isOrdered(drugOrder) && (drugOrder.isDiscontinued(now) || drugOrder.isExpired(now));
	}

	public static boolean isCurrent(Order drugOrder) {
		return isOrdered(drugOrder) && !isPast(drugOrder) && drugOrder.isStarted();
	}

	public static boolean isFuture(Order drugOrder) {
		return isOrdered(drugOrder) && !isPast(drugOrder) && !drugOrder.isStarted();
	}

	public static String getFrequencyPerDayAsString(DrugOrder drugOrder) {
		/*
			TODO: Don't really know what to do here. Logic before was this:
			if(frequency != null && frequency.length() > 0 && frequency.contains("x")) {
				String[] substrings = frequency.split("x");
				freqDay = substrings[0].trim();
				freqWeek = substrings[1].trim();
			}
			else if (frequency != null && frequency.length() > 0) {
				if (frequency.contains("week")) {
					freqWeek = frequency.trim();
				}
				else {
					freqDay = frequency.trim();
				}
			}
		*/
		if (drugOrder.getFrequency() != null && drugOrder.getFrequency().getFrequencyPerDay() != null) {
			return drugOrder.getFrequency().getFrequencyPerDay().toString(); // TODO: Review if this is compatible with usage in OrderExtensionAjaxController
		}
		return "";
	}

	public static String getFrequencyPerWeekAsString(DrugOrder drugOrder) {
		/*
			TODO: Don't really know what to do here. Logic before was this:
			if(frequency != null && frequency.length() > 0 && frequency.contains("x")) {
				String[] substrings = frequency.split("x");
				freqDay = substrings[0].trim();
				freqWeek = substrings[1].trim();
			}
			else if (frequency != null && frequency.length() > 0) {
				if (frequency.contains("week")) {
					freqWeek = frequency.trim();
				}
				else {
					freqDay = frequency.trim();
				}
			}
		*/
		if (drugOrder.getFrequency() != null) {
			return "";

		}
		return "";
	}

	public static OrderExtensionService getOrderExtensionService() {
		return Context.getService(OrderExtensionService.class);
	}
}
