package org.openmrs.module.orderextension.web.tag;

import java.io.IOException;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.orderextension.util.OrderExtensionUtil;
import org.springframework.util.StringUtils;

/**
 * Formats objects for display
 */
public class FormatTag extends TagSupport {

	public static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(getClass());
	
	private String var;	
	private Object object;
	private String format;

	/**
	 * @see TagSupport#doStartTag()
	 */
	public int doStartTag() {
		
		String result = OrderExtensionUtil.format(object, format);

		if (StringUtils.hasText(var)) {
			pageContext.setAttribute(var, result);
		} 
		else {
			try {
				if(result != null)
					pageContext.getOut().write(result);
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
		object = null;
		format = null;
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

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
}
