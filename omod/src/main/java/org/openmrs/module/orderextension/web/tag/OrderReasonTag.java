package org.openmrs.module.orderextension.web.tag;

import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.springframework.util.StringUtils;

/**
 * Formats objects for display
 */
public class OrderReasonTag extends TagSupport {

	public static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());

	private String var;
	private Order order;
	private String type; // Allowed values are order, discontinue, revision

	/**
	 * @see TagSupport#doStartTag()
	 */
	public int doStartTag() {
		Concept reason = null;
		if (order != null) {
			if ("order".equals(type)) {
				reason = order.getOrderReason();
			} else if ("discontinue".equals(type)) {
				Order discontinueOrder = Context.getOrderService().getDiscontinuationOrder(order);
				if (discontinueOrder != null) {
					reason = discontinueOrder.getOrderReason();
				}
			} else if ("revision".equals(type)) {
				Order revisionOrder = Context.getOrderService().getRevisionOrder(order);
				if (revisionOrder != null) {
					reason = revisionOrder.getOrderReason();
				}
			}
			else {
				throw new IllegalStateException("Type must be either order, discontinue, revision");
			}
		}
		if (StringUtils.hasText(var)) {
			pageContext.setAttribute(var, reason);
		}
		else {
			try {
				if (reason != null) {
					pageContext.getOut().write(reason.getDisplayString());
				}
			}
			catch (IOException e) {
				log.error("Failed to write to pageContext.getOut()", e);
			}
		}
		return SKIP_BODY;
	}

	/**
	 * @see TagSupport#doEndTag()
	 */
	public int doEndTag() {
		var = null;
		order = null;
		type = null;
		return EVAL_PAGE;
	}

	/**
	 * @return the var
	 */
	public String getVar() {
		return var;
	}

	/**
	 * @param var the var to set
	 */
	public void setVar(String var) {
		this.var = var;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
