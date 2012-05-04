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
import java.util.Calendar;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;


/**
 *
 */
public class DummyRegimens {
	
	public List<RegimenExtension> getCurrentRegimens()
	{
		List<RegimenExtension> regimens = new ArrayList<RegimenExtension>();
		
		DrugGroup cycle3 = new DrugGroup();
		cycle3.setCycleLength(21);
		cycle3.setIsCyclic(true);
		
		Calendar startDate = Calendar.getInstance();
		startDate.set(2012, 3, 18);
		
		Calendar stopDate = Calendar.getInstance();
		stopDate.setTime(startDate.getTime());
		stopDate.add(Calendar.DAY_OF_YEAR, +25);
		
		cycle3.setDrugGroupStartDate(startDate.getTime());
		cycle3.setId(1);
		cycle3.setRegimenName("CHOP");
		cycle3.setCycleNumber(3);
		
		Concept premedication = Context.getConceptService().getConcept(7084);
		Concept chemotherapy = Context.getConceptService().getConcept(7085);
		Concept postmedication = Context.getConceptService().getConcept(7087);
		Concept ancillary = Context.getConceptService().getConcept(7088);
		
		RegimenExtension prednisolone = new RegimenExtension();
		prednisolone.setClassification(chemotherapy);
		prednisolone.setDrugGroup(cycle3);
		prednisolone.setDrug(Context.getConceptService().getDrugByNameOrId("167"));
		prednisolone.setDose(new Double(10));
		prednisolone.setStartDate(startDate.getTime());
		prednisolone.setAutoExpireDate(stopDate.getTime());
		
		RegimenExtension lisinopril = new RegimenExtension();
		lisinopril.setClassification(premedication);
		lisinopril.setDrugGroup(cycle3);
		lisinopril.setDrug(Context.getConceptService().getDrugByNameOrId("179"));
		lisinopril.setDose(new Double(100));
		lisinopril.setStartDate(startDate.getTime());
		lisinopril.setAutoExpireDate(startDate.getTime());
		
		RegimenExtension salbutamol = new RegimenExtension();
		salbutamol.setClassification(chemotherapy);
		salbutamol.setDrugGroup(cycle3);
		salbutamol.setDrug(Context.getConceptService().getDrugByNameOrId("180"));
		salbutamol.setDose(new Double(10));
		salbutamol.setStartDate(startDate.getTime());
		salbutamol.setAutoExpireDate(startDate.getTime());
		salbutamol.setInfusionInstructions("250ml NS 15-30 minutes");
		
		RegimenExtension carvedilol = new RegimenExtension();
		carvedilol.setClassification(postmedication);
		carvedilol.setDrugGroup(cycle3);
		carvedilol.setDrug(Context.getConceptService().getDrugByNameOrId("185"));
		carvedilol.setDose(new Double(90));
		carvedilol.setStartDate(startDate.getTime());
		carvedilol.setAutoExpireDate(startDate.getTime());
		
		RegimenExtension warfarin = new RegimenExtension();
		warfarin.setClassification(ancillary);
		warfarin.setDrug(Context.getConceptService().getDrugByNameOrId("189"));
		warfarin.setDose(new Double(47));
		warfarin.setStartDate(startDate.getTime());
		
		regimens.add(warfarin);
		regimens.add(carvedilol);
		regimens.add(salbutamol);
		regimens.add(lisinopril);
		regimens.add(prednisolone);
		
		DrugGroup art = new DrugGroup();
		art.setDrugGroupStartDate(startDate.getTime());
		art.setRegimenName("NVP + AZT + 3TC");
		
		RegimenExtension nvp = new RegimenExtension();
		nvp.setDrug(Context.getConceptService().getDrugByNameOrId("22"));
		nvp.setDose(new Double(100));
		nvp.setStartDate(startDate.getTime());
		nvp.setDrugGroup(art);
		nvp.setClassification(Context.getConceptService().getConcept(7091));
		
		RegimenExtension azt = new RegimenExtension();
		azt.setDrug(Context.getConceptService().getDrugByNameOrId("39"));
		azt.setDose(new Double(100));
		azt.setStartDate(startDate.getTime());
		azt.setDrugGroup(art);
		azt.setClassification(Context.getConceptService().getConcept(7091));
		
		regimens.add(nvp);
		regimens.add(azt);
		
		return regimens;
	}
	
	public List<RegimenExtension> getFutureRegimens()
	{
		List<RegimenExtension> regimens = new ArrayList<RegimenExtension>();
		
		DrugGroup cycle4 = new DrugGroup();
		cycle4.setCycleLength(21);
		cycle4.setIsCyclic(true);
		
		Calendar startDate = Calendar.getInstance();
		startDate.set(2012, 5, 18);
		
		Calendar stopDate = Calendar.getInstance();
		stopDate.setTime(startDate.getTime());
		stopDate.add(Calendar.DAY_OF_YEAR, 5);
		
		cycle4.setDrugGroupStartDate(startDate.getTime());
		cycle4.setId(1);
		cycle4.setRegimenName("CHOP");
		cycle4.setCycleNumber(4);
		
		Concept premedication = Context.getConceptService().getConcept(7084);
		Concept chemotherapy = Context.getConceptService().getConcept(7085);
		Concept postmedication = Context.getConceptService().getConcept(7087);
		
		RegimenExtension prednisolone = new RegimenExtension();
		prednisolone.setClassification(chemotherapy);
		prednisolone.setDrugGroup(cycle4);
		prednisolone.setDrug(Context.getConceptService().getDrugByNameOrId("167"));
		prednisolone.setDose(new Double(10));
		prednisolone.setStartDate(startDate.getTime());
		prednisolone.setAutoExpireDate(stopDate.getTime());
		
		RegimenExtension lisinopril = new RegimenExtension();
		lisinopril.setClassification(premedication);
		lisinopril.setDrugGroup(cycle4);
		lisinopril.setDrug(Context.getConceptService().getDrugByNameOrId("179"));
		lisinopril.setDose(new Double(100));
		lisinopril.setStartDate(startDate.getTime());
		lisinopril.setAutoExpireDate(startDate.getTime());
		
		RegimenExtension salbutamol = new RegimenExtension();
		salbutamol.setClassification(chemotherapy);
		salbutamol.setDrugGroup(cycle4);
		salbutamol.setDrug(Context.getConceptService().getDrugByNameOrId("180"));
		salbutamol.setDose(new Double(10));
		salbutamol.setStartDate(startDate.getTime());
		salbutamol.setAutoExpireDate(startDate.getTime());
		salbutamol.setInfusionInstructions("250ml NS 15-30 minutes");
		
		RegimenExtension carvedilol = new RegimenExtension();
		carvedilol.setClassification(postmedication);
		carvedilol.setDrugGroup(cycle4);
		carvedilol.setDrug(Context.getConceptService().getDrugByNameOrId("185"));
		carvedilol.setDose(new Double(90));
		carvedilol.setStartDate(startDate.getTime());
		carvedilol.setAutoExpireDate(startDate.getTime());
		
		DrugGroup cycle5 = new DrugGroup();
		cycle5.setCycleLength(21);
		cycle5.setIsCyclic(true);
		
		startDate.set(2012, 6, 18);
		
		stopDate.setTime(startDate.getTime());
		stopDate.add(Calendar.DAY_OF_YEAR, 5);
		
		cycle5.setDrugGroupStartDate(startDate.getTime());
		cycle5.setId(1);
		cycle5.setRegimenName("CHOP");
		cycle5.setCycleNumber(5);
		
		RegimenExtension prednisolone2 = new RegimenExtension();
		prednisolone2.setClassification(chemotherapy);
		prednisolone2.setDrugGroup(cycle5);
		prednisolone2.setDrug(Context.getConceptService().getDrugByNameOrId("167"));
		prednisolone2.setDose(new Double(10));
		prednisolone2.setStartDate(startDate.getTime());
		prednisolone2.setAutoExpireDate(stopDate.getTime());
		
		RegimenExtension lisinopril2 = new RegimenExtension();
		lisinopril2.setClassification(premedication);
		lisinopril2.setDrugGroup(cycle5);
		lisinopril2.setDrug(Context.getConceptService().getDrugByNameOrId("179"));
		lisinopril2.setDose(new Double(100));
		lisinopril2.setStartDate(startDate.getTime());
		lisinopril2.setAutoExpireDate(startDate.getTime());
		
		RegimenExtension salbutamol2 = new RegimenExtension();
		salbutamol2.setClassification(chemotherapy);
		salbutamol2.setDrugGroup(cycle5);
		salbutamol2.setDrug(Context.getConceptService().getDrugByNameOrId("180"));
		salbutamol2.setDose(new Double(10));
		salbutamol2.setStartDate(startDate.getTime());
		salbutamol2.setAutoExpireDate(startDate.getTime());
		salbutamol2.setInfusionInstructions("250ml NS 15-30 minutes");
		
		RegimenExtension carvedilol2 = new RegimenExtension();
		carvedilol2.setClassification(postmedication);
		carvedilol2.setDrugGroup(cycle5);
		carvedilol2.setDrug(Context.getConceptService().getDrugByNameOrId("185"));
		carvedilol2.setDose(new Double(90));
		carvedilol2.setStartDate(startDate.getTime());
		carvedilol2.setAutoExpireDate(startDate.getTime());
		
		DrugGroup cycle6 = new DrugGroup();
		cycle6.setCycleLength(21);
		cycle6.setIsCyclic(true);
		
		startDate.set(2012, 7, 18);
		
		stopDate.setTime(startDate.getTime());
		stopDate.add(Calendar.DAY_OF_YEAR, 5);
		
		cycle6.setDrugGroupStartDate(startDate.getTime());
		cycle6.setId(1);
		cycle6.setRegimenName("CHOP");
		cycle6.setCycleNumber(6);
		
		RegimenExtension prednisolone3 = new RegimenExtension();
		prednisolone3.setClassification(chemotherapy);
		prednisolone3.setDrugGroup(cycle6);
		prednisolone3.setDrug(Context.getConceptService().getDrugByNameOrId("167"));
		prednisolone3.setDose(new Double(10));
		prednisolone3.setStartDate(startDate.getTime());
		prednisolone3.setAutoExpireDate(stopDate.getTime());
		
		RegimenExtension lisinopril3 = new RegimenExtension();
		lisinopril3.setClassification(premedication);
		lisinopril3.setDrugGroup(cycle6);
		lisinopril3.setDrug(Context.getConceptService().getDrugByNameOrId("179"));
		lisinopril3.setDose(new Double(100));
		lisinopril3.setStartDate(startDate.getTime());
		lisinopril3.setAutoExpireDate(startDate.getTime());
		
		RegimenExtension salbutamol3 = new RegimenExtension();
		salbutamol3.setClassification(chemotherapy);
		salbutamol3.setDrugGroup(cycle6);
		salbutamol3.setDrug(Context.getConceptService().getDrugByNameOrId("180"));
		salbutamol3.setDose(new Double(10));
		salbutamol3.setStartDate(startDate.getTime());
		salbutamol3.setAutoExpireDate(startDate.getTime());
		salbutamol3.setInfusionInstructions("250ml NS 15-30 minutes");
		
		RegimenExtension carvedilol3 = new RegimenExtension();
		carvedilol3.setClassification(postmedication);
		carvedilol3.setDrugGroup(cycle6);
		carvedilol3.setDrug(Context.getConceptService().getDrugByNameOrId("185"));
		carvedilol3.setDose(new Double(90));
		carvedilol3.setStartDate(startDate.getTime());
		carvedilol3.setAutoExpireDate(startDate.getTime());
		
		regimens.add(carvedilol);
		regimens.add(salbutamol);
		regimens.add(lisinopril);
		regimens.add(prednisolone);
		regimens.add(carvedilol2);
		regimens.add(salbutamol2);
		regimens.add(lisinopril2);
		regimens.add(prednisolone2);
		regimens.add(carvedilol3);
		regimens.add(salbutamol3);
		regimens.add(lisinopril3);
		regimens.add(prednisolone3);
		
		return regimens;
	}
	
	public List<RegimenExtension> getCompletedRegimens()
	{	
		List<RegimenExtension> regimens = new ArrayList<RegimenExtension>();
		
		DrugGroup cycle1 = new DrugGroup();
		cycle1.setCycleLength(21);
		cycle1.setIsCyclic(true);
		
		Calendar startDate = Calendar.getInstance();
		startDate.set(2012, 1, 18);
		
		Calendar stopDate = Calendar.getInstance();
		stopDate.setTime(startDate.getTime());
		stopDate.add(Calendar.DAY_OF_YEAR, 5);
		
		cycle1.setDrugGroupStartDate(startDate.getTime());
		cycle1.setId(1);
		cycle1.setRegimenName("CHOP");
		cycle1.setCycleNumber(1);
		
		Concept premedication = Context.getConceptService().getConcept(7084);
		Concept chemotherapy = Context.getConceptService().getConcept(7085);
		Concept postmedication = Context.getConceptService().getConcept(7087);
		
		RegimenExtension prednisolone = new RegimenExtension();
		prednisolone.setClassification(chemotherapy);
		prednisolone.setDrugGroup(cycle1);
		prednisolone.setDrug(Context.getConceptService().getDrugByNameOrId("167"));
		prednisolone.setDose(new Double(10));
		prednisolone.setStartDate(startDate.getTime());
		prednisolone.setAutoExpireDate(stopDate.getTime());
		
		RegimenExtension lisinopril = new RegimenExtension();
		lisinopril.setClassification(premedication);
		lisinopril.setDrugGroup(cycle1);
		lisinopril.setDrug(Context.getConceptService().getDrugByNameOrId("179"));
		lisinopril.setDose(new Double(100));
		lisinopril.setStartDate(startDate.getTime());
		lisinopril.setAutoExpireDate(startDate.getTime());
		
		RegimenExtension salbutamol = new RegimenExtension();
		salbutamol.setClassification(chemotherapy);
		salbutamol.setDrugGroup(cycle1);
		salbutamol.setDrug(Context.getConceptService().getDrugByNameOrId("180"));
		salbutamol.setDose(new Double(10));
		salbutamol.setStartDate(startDate.getTime());
		salbutamol.setAutoExpireDate(startDate.getTime());
		salbutamol.setInfusionInstructions("250ml NS 15-30 minutes");
		
		RegimenExtension carvedilol = new RegimenExtension();
		carvedilol.setClassification(postmedication);
		carvedilol.setDrugGroup(cycle1);
		carvedilol.setDrug(Context.getConceptService().getDrugByNameOrId("185"));
		carvedilol.setDose(new Double(90));
		carvedilol.setStartDate(startDate.getTime());
		carvedilol.setAutoExpireDate(startDate.getTime());
		
		DrugGroup cycle2 = new DrugGroup();
		cycle2.setCycleLength(21);
		cycle2.setIsCyclic(true);
		
		startDate.set(2012, 2, 18);
		
		stopDate.setTime(startDate.getTime());
		stopDate.add(Calendar.DAY_OF_YEAR, 5);
		
		cycle2.setDrugGroupStartDate(startDate.getTime());
		cycle2.setId(1);
		cycle2.setRegimenName("CHOP");
		cycle2.setCycleNumber(2);
		
		RegimenExtension prednisolone2 = new RegimenExtension();
		prednisolone2.setClassification(chemotherapy);
		prednisolone2.setDrugGroup(cycle2);
		prednisolone2.setDrug(Context.getConceptService().getDrugByNameOrId("167"));
		prednisolone2.setDose(new Double(10));
		prednisolone2.setStartDate(startDate.getTime());
		prednisolone2.setAutoExpireDate(stopDate.getTime());
		
		RegimenExtension lisinopril2 = new RegimenExtension();
		lisinopril2.setClassification(premedication);
		lisinopril2.setDrugGroup(cycle2);
		lisinopril2.setDrug(Context.getConceptService().getDrugByNameOrId("179"));
		lisinopril2.setDose(new Double(100));
		lisinopril2.setStartDate(startDate.getTime());
		lisinopril2.setAutoExpireDate(startDate.getTime());
		
		RegimenExtension salbutamol2 = new RegimenExtension();
		salbutamol2.setClassification(chemotherapy);
		salbutamol2.setDrugGroup(cycle2);
		salbutamol2.setDrug(Context.getConceptService().getDrugByNameOrId("180"));
		salbutamol2.setDose(new Double(10));
		salbutamol2.setStartDate(startDate.getTime());
		salbutamol2.setAutoExpireDate(startDate.getTime());
		salbutamol2.setInfusionInstructions("250ml NS 15-30 minutes");
		
		RegimenExtension carvedilol2 = new RegimenExtension();
		carvedilol2.setClassification(postmedication);
		carvedilol2.setDrugGroup(cycle2);
		carvedilol2.setDrug(Context.getConceptService().getDrugByNameOrId("185"));
		carvedilol2.setDose(new Double(90));
		carvedilol2.setStartDate(startDate.getTime());
		carvedilol2.setAutoExpireDate(startDate.getTime());
		
		regimens.add(carvedilol);
		regimens.add(salbutamol);
		regimens.add(lisinopril);
		regimens.add(prednisolone);
		regimens.add(carvedilol2);
		regimens.add(salbutamol2);
		regimens.add(lisinopril2);
		regimens.add(prednisolone2);
		
		return regimens;
	}
}
