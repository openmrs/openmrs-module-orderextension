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
import java.util.Locale;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.orderextension.OrderSet.Operator;
import org.openmrs.module.orderextension.api.OrderExtensionService;

/**
 * Demo Data Set-up Class
 */
public class DemoData {
	
	private Map<String, Concept> concepts = new HashMap<String, Concept>();
	private Map<String, Drug> drugs = new HashMap<String, Drug>();
	private Map<String, OrderSet> orderSets = new HashMap<String, OrderSet>();
	
	public Concept getConcept(String key) {
		return concepts.get(key);
	}
	
	public Drug getDrug(String key) {
		return drugs.get(key);
	}
	
	public OrderSet getOrderSet(String key) {
		return orderSets.get(key);
	}

	public void setupMetadata() {
		
		ConceptClass miscClass = Context.getConceptService().getConceptClassByName("Misc");
		ConceptClass drugClass = Context.getConceptService().getConceptClassByName("Drug");
		ConceptDatatype naType = Context.getConceptService().getConceptDatatypeByName("N/A");
		
		setupConcept("premedication", "CHEMOTHERAPY PRE-MEDICATION", miscClass, naType);
		setupConcept("chemotherapy", "CHEMOTHERAPY", miscClass, naType);
		setupConcept("postmedication", "CHEMOTHERAPY POST-MEDICATION", miscClass, naType);
		setupConcept("art", "ANTIRETROVIRAL THERAPY", miscClass, naType);
		setupConcept("ancillary", "ANCILLARY", miscClass, naType);
		
		setupConcept("IV", "IV", miscClass, naType);
		setupConcept("PO", "PO", miscClass, naType);
		
		setupConcept("normalSaline", "NORMAL SALINE", drugClass, naType);
		setupConcept("ondansetron", "ONDANSETRON", drugClass, naType);
		setupConcept("promethazine", "PROMETHAZINE", drugClass, naType);
		setupConcept("cimetidine", "CIMETIDINE", drugClass, naType);
		setupConcept("prednisolone", "PREDNISOLONE", drugClass, naType);
		setupConcept("doxorubicin", "DOXORUBICIN", drugClass, naType);
		setupConcept("cyclophosphamide", "CYCLOPHOSPHAMIDE", drugClass, naType);
		setupConcept("vincristine", "VINCRISTINE", drugClass, naType);
		setupConcept("warfarin", "WARFARIN", drugClass, naType);
		
		setupConcept("azt", "ZIDOVUDINE", drugClass, naType);
		setupConcept("3tc", "LAMIVUDINE", drugClass, naType);
		setupConcept("nvp", "NEVIRAPINE", drugClass, naType);
		
		if (setupOrderSet("chop", "CHOP", Operator.ALL, concepts.get("chemotherapy"), true)) {
			setupOrderSetMember("chop", "Pre-treatment", null, true, 1, 1, "premedication", "normalSaline", 500.0, "mL", "IV", null, false, null);
			setupOrderSetMember("chop", null, null, true, 1, 1, "premedication", "ondansetron", 8.0, "mg", "PO", null, false, null);
			setupOrderSetMember("chop", null, null, true, 1, 1, "premedication", "promethazine", 25.0, "mg", "IV", null, false, null);
			setupOrderSetMember("chop", null, null, true, 1, 1, "premedication", "cimetidine", 300.0, "mg", "IV", null, false, null);
			setupOrderSetMember("chop", null, null, true, 1, 5, "premedication", "prednisolone", 100.0, "mg", "PO", "For 5 days", false, null);
			setupOrderSetMember("chop", "Chemotherapy", null, true, 1, 1, "chemotherapy", "doxorubicin", 50.0, "mg/m2", "IV", null, false, "in 250mL NS over 15-30 minutes");
			setupOrderSetMember("chop", null, null, true, 1, 1, "chemotherapy", "cyclophosphamide", 750.0, "mg/m2", "IV", null, false, "in 500mL NS over 1 hour");
			setupOrderSetMember("chop", null, null, true, 1, 1, "chemotherapy", "vincristine", 1.4, "mg/m2", "IV", null, false, "in 100ml NS over 15 minutes");
			setupOrderSetMember("chop", "Post-treatment", null, true, 1, 1, "postmedication", "normalSaline", 500.0, "mL", "IV", null, false, null);
		}
		
		if (setupOrderSet("azt3tcNvp", "AZT+3TC+NVP", Operator.ALL, concepts.get("art"), true)) {
			setupOrderSetMember("azt3tcNvp", null, null, true, 1, null, "art", "azt", 300.0, "mg", "PO", null, false, null);
			setupOrderSetMember("azt3tcNvp", null, null, true, 1, null, "art", "3tc", 150.0, "mg", "PO", null, false, null);
			setupOrderSetMember("azt3tcNvp", null, null, true, 1, null, "art", "nvp", 200.0, "mg", "PO", null, false, null);
		}
	}

	private void setupConcept(String key, String name, ConceptClass conceptClass, ConceptDatatype conceptDatatype) {
		Concept c = Context.getConceptService().getConceptByName(name);
		if (c == null) {
			c = new Concept();
			c.setDatatype(conceptDatatype);
			c.setConceptClass(conceptClass);
			c.setFullySpecifiedName(new ConceptName(name, Locale.ENGLISH));
			Context.getConceptService().saveConcept(c);
		}
		concepts.put(key, c);
		if (conceptClass.getName().equals("Drug")) {
			Drug d = Context.getConceptService().getDrugByNameOrId(name);
			if (d == null) {
				d = new Drug();
				d.setName(name);
				d.setConcept(c);
				Context.getConceptService().saveDrug(d);
			}
			drugs.put(key, d);
		}
	}
	
	private boolean setupOrderSet(String key, String name, Operator operator, Concept indication, boolean cyclical) {
		OrderSet orderSet = null;
		for (OrderSet os : Context.getService(OrderExtensionService.class).getNamedOrderSets(true)) {
			if (os.getName().equals(name)) {
				orderSet = os;
				return false;
			}
		}
		if (orderSet == null) {
			orderSet = new OrderSet();
			orderSet.setName(name);
			orderSet.setOperator(operator);
			orderSet.setIndication(indication);
			orderSet.setCyclical(cyclical);
			Context.getService(OrderExtensionService.class).saveOrderSet(orderSet);
		}
		orderSets.put(key, orderSet);
		return true;
	}
	
	private void setupOrderSetMember(String orderSetKey, String title, String comment, boolean selected, Integer startDay, Integer lengthDays,
											String indicationKey, String drugKey, Double dose, String units, String routeKey, String frequency, 
											boolean asNeeded, String additionalInstructions) {
		OrderSet s = orderSets.get(orderSetKey);
		DrugOrderSetMember m = new DrugOrderSetMember();
		m.setTitle(title);
		m.setComment(comment);
		m.setSelected(selected);
		m.setRelativeStartDay(startDay);
		m.setLengthInDays(lengthDays);
		m.setIndication(concepts.get(indicationKey));
		m.setDrug(drugs.get(drugKey));
		m.setDose(dose);
		m.setRoute(concepts.get(routeKey));
		m.setFrequency(frequency);
		m.setAsNeeded(asNeeded);
		m.setAdditionalInstructions(additionalInstructions);
		s.addMember(m);
		Context.getService(OrderExtensionService.class).saveOrderSet(s);
	}
}
