/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis; WITHOUT WARRANTY OF ANY KIND; either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS; LLC.  All Rights Reserved.
 */
package org.openmrs.module.orderextension.util;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.module.orderextension.DrugRegimen;

/**
 * Simple object for representing a drug order that should be instantiated
 */
public class DrugOrderTemplate {

	private Patient patient;
	private Drug drug;
	private Double dose;
	private Concept doseUnits;
	private OrderFrequency frequency;
	private Date startDate;
	private Integer duration;
	private boolean asNeeded;
	private Concept route;
	private Concept orderReason;
	private String instructions;
	private String adminInstructions;
	private DrugRegimen regimen;

	public DrugOrderTemplate(Patient patient, Drug drug, Double dose, Concept doseUnits,
			OrderFrequency frequency, Date startDate, Integer duration, boolean asNeeded,
			Concept route, Concept orderReason, String instructions, String adminInstructions, DrugRegimen regimen) {

		this.patient = patient;
		this.drug = drug;
		this.dose = dose;
		this.doseUnits = doseUnits;
		this.frequency = frequency;
		this.startDate = startDate;
		this.duration = duration;
		this.asNeeded = asNeeded;
		this.route = route;
		this.orderReason = orderReason;
		this.instructions = instructions;
		this.adminInstructions = adminInstructions;
		this.regimen = regimen;
	}

	public DrugOrderTemplate(DrugOrder drugOrder) {
		this(
				drugOrder.getPatient(), drugOrder.getDrug(), drugOrder.getDose(), drugOrder.getDoseUnits(),
				drugOrder.getFrequency(), drugOrder.getEffectiveStartDate(), drugOrder.getDuration(),
				drugOrder.getAsNeeded(), drugOrder.getRoute(), drugOrder.getOrderReason(), drugOrder.getInstructions(),
				drugOrder.getDosingInstructions(), (DrugRegimen)drugOrder.getOrderGroup()
		);
	}

	public DrugOrder toDrugOrder() {
		DrugOrder drugOrder = new DrugOrder();
		drugOrder.setPatient(patient);
		if (drugOrder.getOrderType() == null) {
			drugOrder.setOrderType(OrderEntryUtil.getDrugOrderType());
		}
		drugOrder.setDrug(drug);
		drugOrder.setConcept(drug.getConcept());
		drugOrder.setDose(dose);
		drugOrder.setDoseUnits(doseUnits);
		drugOrder.setFrequency(frequency);
		OrderEntryUtil.setStartDate(drugOrder, startDate);
		OrderEntryUtil.setEndDate(drugOrder, duration);
		drugOrder.setAsNeeded(asNeeded);
		drugOrder.setRoute(route);
		drugOrder.setOrderReason(orderReason);
		drugOrder.setDosingInstructions(adminInstructions);
		drugOrder.setInstructions(instructions);
		drugOrder.setOrderGroup(regimen);
		return drugOrder;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Drug getDrug() {
		return drug;
	}

	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	public Double getDose() {
		return dose;
	}

	public void setDose(Double dose) {
		this.dose = dose;
	}

	public Concept getDoseUnits() {
		return doseUnits;
	}

	public void setDoseUnits(Concept doseUnits) {
		this.doseUnits = doseUnits;
	}

	public OrderFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(OrderFrequency frequency) {
		this.frequency = frequency;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public boolean isAsNeeded() {
		return asNeeded;
	}

	public void setAsNeeded(boolean asNeeded) {
		this.asNeeded = asNeeded;
	}

	public Concept getRoute() {
		return route;
	}

	public void setRoute(Concept route) {
		this.route = route;
	}

	public Concept getOrderReason() {
		return orderReason;
	}

	public void setOrderReason(Concept orderReason) {
		this.orderReason = orderReason;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getAdminInstructions() {
		return adminInstructions;
	}

	public void setAdminInstructions(String adminInstructions) {
		this.adminInstructions = adminInstructions;
	}

	public DrugRegimen getRegimen() {
		return regimen;
	}

	public void setRegimen(DrugRegimen regimen) {
		this.regimen = regimen;
	}
}
