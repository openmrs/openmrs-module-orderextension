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
	
	public DrugClassificationHelper(List<DrugOrder> drugOrders)
	{
		String indicationSet = Context.getAdministrationService().getGlobalProperty("orderextension.drugGroupClassification");
		Concept indicationsSet = Context.getConceptService().getConcept(indicationSet);
		List<Concept> indicationConcepts = indicationsSet.getSetMembers();
		
		for (Concept indication : indicationConcepts)
		{
			indications.put(indication, indication.getSetMembers());
		}
		
		for (DrugOrder o : drugOrders)
		{
			if (o instanceof ExtendedDrugOrder)
			{
				ExtendedDrugOrder edo = (ExtendedDrugOrder)o;
				if (edo.getIndication() != null) 
				{
					for(Map.Entry<Concept, List<Concept>> entry: indications.entrySet())
					{
						Concept indication = entry.getKey();
						List<Concept> indicationList = entry.getValue();
						
						if ((indicationList != null && indicationList.contains(edo.getIndication())) || edo.getIndication().equals(indication))
						{
							if(!classifications.contains(indication))
							{
								classifications.add(indication);
							}
							if(classifiedRegimens.containsKey(indication))
							{
								List<DrugOrder> l = classifiedRegimens.get(indication);
								l.add(edo);
							}
							else
							{
								List<DrugOrder> l = new ArrayList<DrugOrder>();
								l.add(edo);
								classifiedRegimens.put(indication, l);
							}
							break;
						}
					}
				}
			}
			else
			{
				if(classifiedRegimens.containsKey(null))
				{
					List<DrugOrder> l = classifiedRegimens.get(null);
					l.add(o);
				}
				else
				{
					List<DrugOrder> l = new ArrayList<DrugOrder>();
					l.add(o);
					classifiedRegimens.put(null, l);
				}
			}
		}
	}
	
	public List<Concept> getClassificationsForRegimenList()
	{
		return classifications;
	}
	
	public RegimenHelper getRegimenHelperForClassification(Concept concept)
	{
		return new RegimenHelper(classifiedRegimens.get(concept));
	}
}
