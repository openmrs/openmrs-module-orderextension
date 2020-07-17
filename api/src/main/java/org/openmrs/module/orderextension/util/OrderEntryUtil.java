package org.openmrs.module.orderextension.util;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

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
	 * Needed due to removal of Context.getOrderService().getDrugOrdersByPatient(patient); method
	 */
	public static List<DrugOrder> getDrugOrdersByPatient(Patient patient) {
		return null; // TODO: Implement me
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
	 * Need to review how drug dosage form maps to drug order units, and modify as needed
	 */
	public static void setDoseUnits(DrugOrder drugOrder, Drug drug) {
		if (drug.getDosageForm() != null) {
			// drugOrder.setUnits(drug.getDosageForm().getDisplayString()); TODO: This is what was in place in some of the code need to fix/implement
			drugOrder.setDoseUnits(drug.getDosageForm()); // This is the closest equivalent, but does not seem correct
		}
	}

	/**
	 * Needed due to removal of start date from Drug Order
	 */
	public static void setStartDate(DrugOrder drugOrder, Date date) {
		drugOrder.setDateActivated(date); // TODO: Assume this is correct, but review and confirm
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

	public static void discontinueOrder(DrugOrder drugOrder, Concept stopConcept, Date stopDateDrug) {
		//Context.getOrderService().discontinueOrder(drugOrder, stopConcept, stopDateDrug); TODO: This is what was in place before
	}

	public static boolean isCurrent(DrugOrder drugOrder) {
		return drugOrder.isActive() && drugOrder.isStarted();  // TODO: Need to review this implementation
	}

	public static boolean isFuture(DrugOrder drugOrder) {
		return drugOrder.isActive() && !drugOrder.isStarted(); // TODO: Need to review this implementation
	}
}
