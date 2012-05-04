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

import org.openmrs.Concept;
import org.openmrs.api.context.Context;


/**
 *
 */
public class RegimenHelper {

	List<RegimenExtension> regimenList;
	
	ArrayList<DrugGroup> cycles;
	ArrayList<DrugGroup> drugGroups;
	
	HashMap<DrugGroup, List<RegimenExtension>> regimensInDrugGroups;
	
	boolean hasCycles = false;
	boolean hasDrugGroups = false;
	boolean hasOtherMedications = false;
	
	
	public RegimenHelper(List<RegimenExtension> regimenList)
	{
		this.regimenList = regimenList;
		
		setUp();
	}
	
	private void setUp()
	{
		cycles = new ArrayList<DrugGroup>();
		drugGroups = new ArrayList<DrugGroup>();
		
		regimensInDrugGroups = new HashMap<DrugGroup, List<RegimenExtension>>();
		
		for(RegimenExtension regimen: regimenList)
		{
			if(regimen.getDrugGroup() != null && regimen.getDrugGroup().getIsCyclic() && !cycles.contains(regimen.getDrugGroup()))
			{
				cycles.add(regimen.getDrugGroup());
				
				List<RegimenExtension> regimensInGivenCycle = new ArrayList<RegimenExtension>();
				regimensInGivenCycle.add(regimen);
				regimensInDrugGroups.put(regimen.getDrugGroup(), regimensInGivenCycle);
				
				hasCycles = true;
			}
			else if(regimen.getDrugGroup() != null && !regimen.getDrugGroup().getIsCyclic() && !drugGroups.contains(regimen.getDrugGroup()))
			{
				drugGroups.add(regimen.getDrugGroup());
				
				List<RegimenExtension> regimensInGivenDrugGroup = new ArrayList<RegimenExtension>();
				regimensInGivenDrugGroup.add(regimen);
				regimensInDrugGroups.put(regimen.getDrugGroup(), regimensInGivenDrugGroup);
				
				hasDrugGroups = true;
			}
			else if(regimen.getDrugGroup() != null)
			{
				List<RegimenExtension> regimens = regimensInDrugGroups.get(regimen.getDrugGroup());
				regimens.add(regimen);
			}
			else
			{	
				hasOtherMedications = true;
				
				if(regimensInDrugGroups.containsKey(null))
				{
					List<RegimenExtension> regimens = regimensInDrugGroups.get(null);
					regimens.add(regimen);
				}
				else
				{
					List<RegimenExtension> regimensNotInGroup = new ArrayList<RegimenExtension>();
					regimensNotInGroup.add(regimen);
					regimensInDrugGroups.put(null, regimensNotInGroup);
				}
			}
		}
		
		Collections.sort(cycles, new DrugGroupSorter());
		Collections.sort(drugGroups, new DrugGroupSorter());
	}
	
	public List<DrugGroup> getCycleList()
	{	
		return cycles;
	}
	
	public List<DrugGroup> getDrugGroupList()
	{	
		return drugGroups;
	}
	
	public List<RegimenExtension> getOtherMedications()
	{
		return regimensInDrugGroups.get(null);
	}
	
    public boolean isHasCycles() {
    	return hasCycles;
    }

	
    public boolean isHasDrugGroups() {
    	return hasDrugGroups;
    }

	
    public boolean isHasOtherMedications() {
    	return hasOtherMedications;
    }

	public List<Concept> getClassifications(DrugGroup drugGroup, final Concept topLevelIndicator)
	{
		List<Concept> classifications = new ArrayList<Concept>();
		
		for(RegimenExtension regimen: regimensInDrugGroups.get(drugGroup))
		{
			if(regimen.getClassification() != null && !classifications.contains(regimen.getClassification()))
			{
				classifications.add(regimen.getClassification());
			}
			else if(regimen.getClassification() == null && !classifications.contains(null))
			{
				classifications.add(null);
			}
		}
		
		Collections.sort(classifications, new Comparator<Concept>(){
			 
            public int compare(Concept c1, Concept c2) {
            	
            	List<Concept> setMembers = topLevelIndicator.getSetMembers();
            	
            	int index1 = setMembers.indexOf(c1);
            	int index2 = setMembers.indexOf(c2);
            
            	return index1 - index2;
            }
 
        });
		
		//TODO: will need to be able to sort these based on a predetermined order
		
		return classifications;
	}
	
	public List<RegimenExtension> getRegimensForClassification(DrugGroup cycle, Concept classification)
	{
		List<RegimenExtension> regimenExtensions = new ArrayList<RegimenExtension>();
		
		for(RegimenExtension regimen: regimensInDrugGroups.get(cycle))
		{
			if(classification == null && regimen.getClassification() == null)
			{
				regimenExtensions.add(regimen);
			}
			else if(classification != null && regimen.getClassification() != null && regimen.getClassification().equals(classification))
			{
				regimenExtensions.add(regimen);
			}
		}
		
		Collections.sort(regimenExtensions, new Comparator<RegimenExtension>(){
			 
            public int compare(RegimenExtension r1, RegimenExtension r2) {
               
               if(r1.getDrug().equals(r2.getDrug()))
               {
            	   if(r1.getStartDate().compareTo(r2.getStartDate()) < 0)
            	   {
            		   return -1;
            	   }
            	   else if(r1.getStartDate().compareTo(r2.getStartDate()) > 0)
            	   {
						return 1;
            	   }
               }
               else
               {
            	   if(r1.getStartDate().compareTo(r2.getStartDate()) < 0)
            	   {
            		   return -10;
            	   }
            	   else if(r1.getStartDate().compareTo(r2.getStartDate()) > 0)
            	   {
						return 10;
            	   }
               }
               
               return 0;
            }
 
        });
		
		return regimenExtensions;
	}
	
	public boolean hasIVDrugs(DrugGroup drugGroup)
	{
		Concept ivConcept = Context.getConceptService().getConcept(Context.getAdministrationService().getGlobalProperty("orderextension.IVRouteConcept")); 
		for(RegimenExtension regimen: regimensInDrugGroups.get(drugGroup))
		{
			if(regimen.getDrug()!= null && regimen.getDrug().getRoute() != null && regimen.getDrug().getRoute().equals(ivConcept))
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	private class DrugGroupSorter implements Comparator<DrugGroup>
	{
		public int compare(DrugGroup c1, DrugGroup c2) {
            
			return c1.getDrugGroupStartDate().compareTo(c2.getDrugGroupStartDate());
         }
	}
}
