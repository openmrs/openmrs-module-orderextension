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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;

/**
 * Helper class for classifying Drug Orders
 */
public class DrugClassificationHelper {
	
	private Map<Concept, List<Concept>> indications = new HashMap<Concept, List<Concept>>();
	private List<Concept> classifications = new ArrayList<Concept>();
	private Map<Concept, List<DrugOrder>> classifiedRegimens = new HashMap<Concept, List<DrugOrder>>();
	private List<Concept> indicationConcepts = new ArrayList<Concept>();
	
	/**
	 * Default Constructor
	 */
	public DrugClassificationHelper(List<DrugOrder> drugOrders) 
	{
		String indicationSet = Context.getAdministrationService().getGlobalProperty("orderextension.drugGroupClassification");
		Concept indicationsSet = Context.getConceptService().getConceptByUuid(indicationSet);
		if(indicationsSet == null)
		{
			indicationsSet = Context.getConceptService().getConcept(indicationSet);
		}
		indicationConcepts = indicationsSet.getSetMembers();
		
		for (Concept indication : indicationConcepts) {
			indications.put(indication, indication.getSetMembers());
		}
		
		for (DrugOrder o : drugOrders) {
			
			Concept indication = o.getOrderReason();

			boolean found = false;
			if (indication != null) {
				for (Map.Entry<Concept, List<Concept>> entry: indications.entrySet()) {
					Concept c = entry.getKey();
					List<Concept> indicationList = entry.getValue();
						
					if ((indicationList != null && indicationList.contains(indication)) || indication.equals(c)) {
						indication = c;
						found = true;
						break;
					}
				}
				if (!found) {
					indication = null;
				}
			}

			if(!classifications.contains(indication)) {
				classifications.add(indication);
			}
			List<DrugOrder> l = classifiedRegimens.get(indication);
			if (l == null) {
				l = new ArrayList<DrugOrder>();
				classifiedRegimens.put(indication, l);
			}
			l.add(o);
		}
		
		Collections.sort(classifications, new Comparator<Concept>() {
			
			public int compare(Concept left, Concept right) {
				Integer l = indicationConcepts.indexOf(left);
				Integer r = indicationConcepts.indexOf(right);
				if(l == -1)
				{
					l = 99;
				}
				if(r == -1)
				{
					r = 99;
				}
				return l.compareTo(r);
			}
		} );
	}
	
	public List<Concept> getClassificationsForRegimenList() {
		return classifications;
	}
	
	public RegimenHelper getRegimenHelperForClassification(Concept concept) {
		return new RegimenHelper(classifiedRegimens.get(concept));
	}	
}
