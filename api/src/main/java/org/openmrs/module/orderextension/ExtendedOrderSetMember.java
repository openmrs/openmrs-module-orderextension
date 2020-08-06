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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.OpenmrsObject;
import org.openmrs.OrderFrequency;
import org.openmrs.OrderSetMember;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.util.OrderEntryUtil;

/**
 * This represents a single member within an CyclicalOrderSet
 */
public class ExtendedOrderSetMember {
	
	public static final long serialVersionUID = 1L;

	private OrderSetMember member;

	public ExtendedOrderSetMember() {
		this(new OrderSetMember());
	}
	
	/**
	 * Default Constructor
	 */
	public ExtendedOrderSetMember(OrderSetMember member) {
		this.member = member;
		if (member.getOrderType() == null) {
			member.setOrderType(OrderEntryUtil.getDrugOrderType());
		}
	}

	public OrderSetMember getMember() {
		return member;
	}

	public String getUuid() {
		return member.getUuid();
	}

	public ExtendedOrderSet getOrderSet() {
		return (ExtendedOrderSet) member.getOrderSet();
	}

	public void setOrderSet(ExtendedOrderSet orderSet) {
		member.setOrderSet(orderSet);
	}

	public Concept getConcept() {
		return member.getConcept();
	}

	public void setConcept(Concept concept) {
		member.setConcept(concept);
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof ExtendedOrderSetMember) {
			ExtendedOrderSetMember that = (ExtendedOrderSetMember) o;
			return this.member.equals(that.member);
		}
		return false;
	}

	protected <T> T getTemplateProperty(String propertyName, Class<T> type) {
		T ret = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			if (member.getOrderTemplate() != null) {
				JsonNode o = mapper.readTree(member.getOrderTemplate()).get(propertyName);
				if (o != null && !o.isNull()) {
					if (type == String.class) {
						ret = (T) o.asText();
					}
					else if (type == Integer.class) {
						ret = (T) Integer.valueOf(o.asInt());
					}
					else if (type == Double.class) {
						ret = (T) Double.valueOf(o.asDouble());
					}
					else if (type == Concept.class) {
						Concept c = Context.getConceptService().getConceptByUuid(o.asText());
						if (c == null) {
							c = Context.getConceptService().getConcept(o.asInt());
						}
						ret = (T)c;
					}
					else if (type == Drug.class) {
						Drug d = Context.getConceptService().getDrugByUuid(o.asText());
						if (d == null) {
							d = Context.getConceptService().getDrug(o.asInt());
						}
						ret = (T)d;
					}
					else if (type == OrderFrequency.class) {
						OrderFrequency f = Context.getOrderService().getOrderFrequencyByUuid(o.asText());
						if (f == null) {
							f = Context.getOrderService().getOrderFrequency(o.asInt());
						}
						ret = (T)f;
					}
					else {
						throw new IllegalStateException("Template datatype unsupported: " + type);
					}
					if (ret == null) {
						throw new IllegalStateException("Error parsing " + o + " into " + propertyName + " of type " + type);
					}
				}
			}
			return ret;
		}
		catch (Exception e) {
			throw new IllegalStateException("Unable to read " + propertyName + " from order set member");
		}
	}

	protected void setTemplateProperty(String propertyName, Object propertyValue) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = new HashMap<String, Object>();
			if (member.getOrderTemplate() != null) {
				map = mapper.readValue(member.getOrderTemplate(), Map.class);
			}
			if (propertyValue instanceof OpenmrsObject) {
				propertyValue = ((OpenmrsObject)propertyValue).getUuid();
			}
			map.put(propertyName, propertyValue);
			member.setOrderTemplate(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
		}
		catch (Exception e) {
			throw new IllegalStateException("Unable to write " + propertyName + " = " + propertyValue + " for order set member");
		}
	}
	
	//***** INSTANCE METHODS *****

	public Integer getSortWeight() {
		if (member.getOrderSet() != null) {
			for (int i = 0; i < member.getOrderSet().getOrderSetMembers().size(); i++) {
				OrderSetMember osm = member.getOrderSet().getOrderSetMembers().get(i);
				if (osm.equals(this)) {
					return i + 1;
				}
			}
		}
		return -1;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return getTemplateProperty("title", String.class);
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		setTemplateProperty("title", title);
	}

	/**
	 * @return the relativeStartDay
	 */
	public Integer getRelativeStartDay() {
		return getTemplateProperty("relativeStartDay", Integer.class);
	}

	/**
	 * @param relativeStartDay the relativeStartDay to set
	 */
	public void setRelativeStartDay(Integer relativeStartDay) {
		setTemplateProperty("relativeStartDay", relativeStartDay);
	}

	/**
	 * @return the lengthInDays
	 */
	public Integer getLengthInDays() {
		return getTemplateProperty("lengthInDays", Integer.class);
	}

	/**
	 * @param lengthInDays the lengthInDays to set
	 */
	public void setLengthInDays(Integer lengthInDays) {
		setTemplateProperty("lengthInDays", lengthInDays);
	}

	/**
	 * @return the indication
	 */
	public Concept getIndication() {
		return getTemplateProperty("indication", Concept.class);
	}

	/**
	 * @param indication the indication to set
	 */
	public void setIndication(Concept indication) {
		setTemplateProperty("indication", indication);
	}

	/**
	 * @return the instructions
	 */
	public String getInstructions() {
		return getTemplateProperty("instructions", String.class);
	}

	/**
	 * @param instructions the instructions to set
	 */
	public void setInstructions(String instructions) {
		setTemplateProperty("instructions", instructions);
	}

	/**
	 * @return the drug
	 */
	public Drug getDrug() {
		return getTemplateProperty("drug", Drug.class);
	}

	/**
	 * @param drug the drug to set
	 */
	public void setDrug(Drug drug) {
		setTemplateProperty("drug", drug);
		if (drug != null && drug.getConcept() != null) {
			member.setConcept(drug.getConcept());
		}
	}

	/**
	 * @return the dose
	 */
	public Double getDose() {
		return getTemplateProperty("dose", Double.class);
	}

	/**
	 * @param dose the dose to set
	 */
	public void setDose(Double dose) {
		setTemplateProperty("dose", dose);
	}

	/**
	 * @return the units
	 */
	public Concept getDoseUnits() {
		return getTemplateProperty("doseUnits", Concept.class);
	}

	/**
	 * @return the units
	 */
	public void setDoseUnits(Concept doseUnits) {
		setTemplateProperty("doseUnits", doseUnits);
	}

	public String getUnits() {
		return getTemplateProperty("units", String.class);
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(String units) {
		setTemplateProperty("units", units);
	}

	/**
	 * @return the route
	 */
	public Concept getRoute() {
		return getTemplateProperty("route", Concept.class);
	}

	/**
	 * @param route the route to set
	 */
	public void setRoute(Concept route) {
		setTemplateProperty("route", route);
	}

	/**
	 * @return the frequency
	 */
	public OrderFrequency getOrderFrequency() {
		return getTemplateProperty("orderFrequency", OrderFrequency.class);
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setOrderFrequency(OrderFrequency frequency) {
		setTemplateProperty("orderFrequency", frequency);
	}

	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return getTemplateProperty("frequency", String.class);
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		setTemplateProperty("frequency", frequency);
	}

	/**
	 * @return the asNeeded
	 */
	public boolean isAsNeeded() {
		return "true".equals(getTemplateProperty("asNeeded", String.class));
	}

	/**
	 * @param asNeeded the asNeeded to set
	 */
	public void setAsNeeded(boolean asNeeded) {
		setTemplateProperty("asNeeded", Boolean.toString(asNeeded));
	}

	/**
	 * @return the administrationInstructions
	 */
	public String getAdministrationInstructions() {
		return getTemplateProperty("administrationInstructions", String.class);
	}

	/**
	 * @param administrationInstructions the administrationInstructions to set
	 */
	public void setAdministrationInstructions(String administrationInstructions) {
		setTemplateProperty("administrationInstructions", administrationInstructions);
	}
}
