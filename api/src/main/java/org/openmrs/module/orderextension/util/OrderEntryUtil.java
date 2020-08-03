package org.openmrs.module.orderextension.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.CareSetting;
import org.openmrs.Drug;
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
	 * Needed due to fact that method Context.getOrderService().saveOrder(drugOrder) is invalid in 2.3
	 */
	public static void saveDrugOrder(DrugOrder drugOrder) {
		// return null;  // TODO
	}

	/**
	 * Needed due to the fact that frequency is changed from String to OrderFrequency reference in 2.3
	 */
	public static void setFrequency(DrugOrder drugOrder, String frequency) {
		// TODO
	}

	/**
	 * Need to review how string units maps to drug order units, and modify as needed
	 */
	public static void setDoseUnits(DrugOrder drugOrder, String doseUnits) {
		// TODO
	}

	/**
	 * Need to review how drug dosage form maps to drug order units, and modify as needed
	 */
	public static void setDoseUnits(DrugOrder drugOrder, Drug drug) {
		if (drug.getDosageForm() != null) {
			// drugOrder.setUnits(drug.getDosageForm().getDisplayString()); TODO: This is what was in place in some of the code need to fix/implement
			drugOrder.setDoseUnits(drug.getDosageForm()); // This is the closest equivalent, but does not seem correct
		}
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

	/**
	 * Needed to cover controller code that is trying to set/update the stop and expire dates based on input
	 */
	public static void updateStopAndExpireDates(DrugOrder drugOrder, Date stopDateDrug) {
		// TODO: The below implementation was taken from a Controller.  Had to comment out the one line to compile, but leaving code to understand
		if (drugOrder.isDiscontinuedRightNow()) {
			//we want to set the stop date to the end of the evening, otherwise drugs orders that are only for one day never show up as active
			// drugOrder.setDiscontinuedDate(OrderExtensionUtil.adjustDateToEndOfDay(stopDateDrug)); // TODO: Need to figure out how to do this
		} else {
			drugOrder.setAutoExpireDate(OrderExtensionUtil.adjustDateToEndOfDay(stopDateDrug));
		}
	}



	public static boolean isCurrent(DrugOrder drugOrder) {
		return drugOrder.isActive() && drugOrder.isStarted();  // TODO: Need to review this implementation
	}

	public static boolean isFuture(DrugOrder drugOrder) {
		return drugOrder.isActive() && !drugOrder.isStarted(); // TODO: Need to review this implementation
	}

	public static String getProtocol(Drug drug) {
		// In OrderEntryAjaxController, is drug.getDoseStrength() + " "  + drug.getUnits();.
		// TODO: This seems to match to the intention of drug.strength - Review This
		return drug.getStrength();
	}

	public static String getProtocolDose(Drug drug) {
		// In OrderEntryAjaxController, is drug.getDoseStrength().toString()
		// TODO: Review how where this is used, and if this can just use the Strength
		return drug.getStrength();
	}

	public static String getRoute(Drug drug) {
		// In OrderEntryAjaxController, is drug.getRoute().getDisplayString();
		// TODO: Doesn't seem to have a supported equivalent, should work to remove need for this.
		return "";
	}

	public static String getUnits(Drug drug) {
		// No longer a method, not just part of drug strength, used to be drug.getUnits()
		return "";
	}

	/**
	 * This is based sort of on what was present in OrderExtensionAjaxController.  Needs more investigation
	 */
	public static String getDoseReduction(Drug drug) {
		if (drug.getStrength() != null && drug.getStrength().contains("/")) { // TODO: I doubt this is right. Needs review
			return "true";
		}
		return "";
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
