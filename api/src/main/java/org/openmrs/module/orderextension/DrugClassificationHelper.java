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
import org.openmrs.api.context.Context;


/**
 *
 */
public class DrugClassificationHelper {
	
	Map<Concept, List<Concept>> indicators = new HashMap<Concept, List<Concept>>();
	
	List<RegimenExtension> regimens = null;
	
	Map<Concept, List<RegimenExtension>> classifiedRegimens = new HashMap<Concept, List<RegimenExtension>>();
	
	List<Concept> classifications = new ArrayList<Concept>();
	
	public DrugClassificationHelper(List<RegimenExtension> regimens)
	{
		this.regimens = regimens;
		setUp();
	}
	
	private void setUp()
	{
		Concept indicationsSet = Context.getConceptService().getConcept(Context.getAdministrationService().getGlobalProperty("orderextension.drugGroupClassification"));
		List<Concept> indicatorConcepts = indicationsSet.getSetMembers();
		
		for(Concept indicator: indicatorConcepts)
		{
			indicators.put(indicator, indicator.getSetMembers());
		}
		
		for(RegimenExtension regimen: regimens)
		{
			if(regimen.getClassification() != null)
			{
				for(Map.Entry<Concept, List<Concept>> entry: indicators.entrySet())
				{
					List<Concept> indicatorList = entry.getValue();
					Concept indicator = entry.getKey();
					
					if(indicatorList != null && indicatorList.contains(regimen.getClassification()) || regimen.getClassification().equals(indicator))
					{
						if(!classifications.contains(indicator))
						{
							classifications.add(indicator);
						}
						if(classifiedRegimens.containsKey(indicator))
						{
							List<RegimenExtension> regimenList = classifiedRegimens.get(indicator);
							regimenList.add(regimen);
						}
						else
						{
							List<RegimenExtension> regimenList = new ArrayList<RegimenExtension>();
							regimenList.add(regimen);
							classifiedRegimens.put(indicator, regimenList);
						}
						break;
					}
				}
			}
			else
			{
				if(classifiedRegimens.containsKey(null))
				{
					List<RegimenExtension> regimenList = classifiedRegimens.get(null);
					regimenList.add(regimen);
				}
				else
				{
					List<RegimenExtension> regimenList = new ArrayList<RegimenExtension>();
					regimenList.add(regimen);
					classifiedRegimens.put(null, regimenList);
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
