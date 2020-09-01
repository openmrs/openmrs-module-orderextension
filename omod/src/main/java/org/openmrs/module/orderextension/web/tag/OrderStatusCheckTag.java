package org.openmrs.module.orderextension.web.tag;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Order;
import org.openmrs.module.orderextension.util.OrderEntryUtil;
import org.springframework.util.StringUtils;

/**
 * Checks aspects of the order
 */
public class OrderStatusCheckTag extends TagSupport {

	public static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());

	private Order order;
	private String statusCheck; // Allowed values are ordered,current,future,past
	private String var; // The variable to set

	/**
	 * @see TagSupport#doStartTag()
	 */
	public int doStartTag() {
		boolean pass = false;
		if (order != null) {
			if ("ordered".equalsIgnoreCase(statusCheck)) {
				pass = OrderEntryUtil.isOrdered(order);
			}
			else if ("current".equalsIgnoreCase(statusCheck)) {
				pass = OrderEntryUtil.isCurrent(order);
			}
			else if ("future".equalsIgnoreCase(statusCheck)) {
				pass = OrderEntryUtil.isFuture(order);
			}
			else if ("past".equalsIgnoreCase(statusCheck)) {
				pass = OrderEntryUtil.isPast(order);
			}
			else {
				throw new IllegalStateException("Type must be either current, future, past");
			}
		}
		if (StringUtils.hasText(var)) {
			pageContext.setAttribute(var, pass);
			return SKIP_BODY;
		}
		else {
			if (pass) {
				return EVAL_BODY_INCLUDE;
			}
			return SKIP_BODY;
		}
	}

	/**
	 * @see TagSupport#doEndTag()
	 */
	public int doEndTag() {
		statusCheck = null;
		order = null;
		return EVAL_PAGE;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getStatusCheck() {
		return statusCheck;
	}

	public void setStatusCheck(String statusCheck) {
		this.statusCheck = statusCheck;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}
}
