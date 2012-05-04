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

import java.io.Serializable;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or {@link BaseOpenmrsMetadata}.
 */
public class RegimenExtension extends DrugOrder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String infusionInstructions;
	
	private Concept classification;
	
	private DrugGroup drugGroup = null;

    public Concept getClassification() {
    	return classification;
    }
	
    public void setClassification(Concept classification) {
    	this.classification = classification;
    }
	
    public DrugGroup getDrugGroup() {
    	return drugGroup;
    }
	
    public void setDrugGroup(DrugGroup drugGroup) {
    	this.drugGroup = drugGroup;
    }
	
    public String getInfusionInstructions() {
    	return infusionInstructions;
    }

    public void setInfusionInstructions(String infusionInstructions) {
    	this.infusionInstructions = infusionInstructions;
    }
}