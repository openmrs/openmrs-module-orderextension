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
package org.openmrs.module.orderextension.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;


/**
 *
 */
public class DrugConceptHelper {
	
	public List<String> getDistinctSortedDrugs()
	{
		Map<String, List<Drug>> drugs = new HashMap<String, List<Drug>>();
		List<Drug> allDrugs = Context.getConceptService().getAllDrugs(false);
		for(Drug drug: allDrugs)
		{
			if(drugs.containsKey(drug.getName()))
			{
				List<Drug> drugsForConcept = drugs.get(drug.getName());
				drugsForConcept.add(drug);
			}
			else
			{
				List<Drug> drugsForConcept =  new ArrayList<Drug>();
				drugsForConcept.add(drug);
				drugs.put(drug.getName(), drugsForConcept);
			}
		}
		
		Set<String> distinctDrugs = drugs.keySet();
		
		List<String> distinct = new ArrayList<String>();
		distinct.addAll(distinctDrugs);
		
		Collections.sort(distinct);
		return distinct;
	}
	
	public List<Concept> getIndications()
	{
		String indicationSet = Context.getAdministrationService().getGlobalProperty("orderextension.drugGroupClassification");
		Concept indicationsSet = Context.getConceptService().getConcept(indicationSet);
		List<Concept> indicationConcepts = indicationsSet.getSetMembers();
		
		return indicationConcepts;
	}

}
