package org.openmrs.module.orderextension.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.CareSetting;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderGroup;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.api.OrderExtensionService;
import org.openmrs.parameter.OrderSearchCriteriaBuilder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a new class, created during the upgrade from 1.9 to 2.3 data model, to encapsulate methods needed
 * in order to support this migration effort, either because they were removed from core, or because there are
 * adaptations needed.
 */
public class OrderEntryUtil {

	private static final Log log = LogFactory.getLog(OrderEntryUtil.class);

	/**
	 * Needed due to removal of the drug order type hard-coded primary key in core
	 */
	public static OrderType getDrugOrderType() {
		String propertyValue = Context.getAdministrationService().getGlobalProperty("orderextension.drugOrderType");
		if (StringUtils.isEmpty(propertyValue)) {
			throw new IllegalStateException("Please configure the orderextension.drugOrderType global property");
		}
		try {
			int orderTypeId = Integer.parseInt(propertyValue);
			OrderType ret = Context.getOrderService().getOrderType(orderTypeId);
			if (ret != null) {
				return ret;
			}
		}
		catch (Exception e) {}
		try {
			OrderType ret = Context.getOrderService().getOrderTypeByUuid(propertyValue);
			if (ret != null) {
				return ret;
			}
		}
		catch (Exception e) {}
		try {
			OrderType ret = Context.getOrderService().getOrderTypeByName(propertyValue);
			if (ret != null) {
				return ret;
			}
		}
		catch (Exception e) {}
		throw new IllegalStateException("Please ensure that the orderextension.drugOrderType global property references a uuid or name of an order type in the system");
	}

	/**
	 * @return the first found INPATIENT care setting
	 * We choose INPATIENT over OUTPATIENT as outpatient orders have stricter validation requirements around
	 * required fields for quantity, quantityUnits, and numRefills, which we do not collect or utilize
	 */
	public static CareSetting getDefaultCareSetting() {
		List<CareSetting> careSettings = Context.getOrderService().getCareSettings(false);
		if (careSettings.isEmpty()) {
			throw new IllegalStateException("No care settings defined, unable to create Orders");
		}
		for (CareSetting cs : careSettings) {
			if (cs.getCareSettingType() == CareSetting.CareSettingType.INPATIENT) {
				return cs;
			}
		}
		throw new IllegalStateException("No inpatient care settings defined, unable to create Orders");
	}

	/**
	 * Needed due to removal of Context.getOrderService().getDrugOrdersByPatient(patient); method
	 * This may need to be improved for performance, etc. but for now should do the trick.
	 */
	public static List<DrugOrder> getDrugOrdersByPatient(Patient patient) {
		log.warn("Getting Drug Orders for Patient: " + patient.getId());
		OrderSearchCriteriaBuilder b = new OrderSearchCriteriaBuilder();
		b.setPatient(patient).setIncludeVoided(false).setExcludeDiscontinueOrders(true);
		b.setOrderTypes(Arrays.asList(getDrugOrderType()));
		List<Order> allOrders = Context.getOrderService().getOrders(b.build());
		log.warn("Got " + allOrders.size() + " orders");
		List<DrugOrderWrapper> wrappers = new ArrayList<>();
		for (Order o : allOrders) {
			o = HibernateUtil.getRealObjectFromProxy(o);
			if (o instanceof DrugOrder) {
				wrappers.add(new DrugOrderWrapper((DrugOrder)o));
			}
		}
		log.warn("Converted to " + wrappers.size() + " drug order wrappers for sorting");
		Collections.sort(wrappers);
		log.warn("Sorting complete, returning drug orders");
		return wrappers.stream().map(DrugOrderWrapper::getDrugOrder).collect(Collectors.toList());
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
			drugOrder.setDateActivated(orderDate);
		}
		else {
			drugOrder.setUrgency(Order.Urgency.ROUTINE);
			drugOrder.setScheduledDate(null);
			drugOrder.setDateActivated(startDate);
		}
	}

	public static void setEndDate(DrugOrder drugOrder, Integer lengthInDays) {
		if (lengthInDays == null) {
			drugOrder.setAutoExpireDate(null);
		}
		else {
			Date startDate = drugOrder.getEffectiveStartDate();
			Date endDate = OrderExtensionUtil.incrementDateEndOfDay(startDate, lengthInDays - 1);
			drugOrder.setAutoExpireDate(endDate);
		}
	}

	public static boolean isOrdered(Order drugOrder) {
		return !drugOrder.getVoided() && drugOrder.getAction() != Order.Action.DISCONTINUE;
	}

	public static boolean isPast(Order drugOrder) {
		return isPast(drugOrder, new Date());
	}

	public static boolean isPast(Order drugOrder, Date onDate) {
		return isOrdered(drugOrder) && (drugOrder.isDiscontinued(onDate) || drugOrder.isExpired(onDate));
	}

	public static boolean isCurrent(Order drugOrder) {
		return isOrdered(drugOrder) && !isPast(drugOrder) && drugOrder.isStarted();
	}

	public static boolean isCurrent(Order drugOrder, Date onDate) {
		return isOrdered(drugOrder) && !isPast(drugOrder, onDate) && drugOrder.isStarted(onDate);
	}

	public static boolean isFuture(Order drugOrder) {
		return isOrdered(drugOrder) && !isPast(drugOrder) && !drugOrder.isStarted();
	}

	public static String getDiscontinueReason(Order order) {
		String ret = null;
		Order discontinueOrder = Context.getOrderService().getDiscontinuationOrder(order);
		if (discontinueOrder != null) {
			ret = discontinueOrder.getOrderReasonNonCoded();
			if (StringUtils.isEmpty(ret) && discontinueOrder.getOrderReason() != null) {
				ret = discontinueOrder.getOrderReason().getDisplayString();
			}
		}
		return ret;
	}

	public static OrderGroup getOrderGroup(Order order) {
		return HibernateUtil.getRealObjectFromProxy(order.getOrderGroup());
	}

	public static boolean isCyclical(OrderGroup group) {
		if (group instanceof DrugRegimen) {
			return ((DrugRegimen) group).isCyclical();
		}
		return false;
	}

	public static OrderExtensionService getOrderExtensionService() {
		return Context.getService(OrderExtensionService.class);
	}
}
