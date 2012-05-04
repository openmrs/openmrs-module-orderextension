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
import java.util.Date;

import org.openmrs.BaseOpenmrsData;


/**
 *
 */
public class DrugGroup extends BaseOpenmrsData implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String regimenName;
	
	private Integer cycleNumber;
	
	private Date drugGroupStartDate;
	
	private Integer cycleLength;
	
	private Boolean isCyclic = false;
	
	/**
     * @see org.openmrs.OpenmrsObject#getId()
     */
    @Override
    public Integer getId() {
	    return id;
    }

	/**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    @Override
    public void setId(Integer id) {
	    
	    this.id = id;
    }

	
    public String getRegimenName() {
    	return regimenName;
    }

	
    public void setRegimenName(String regimenName) {
    	this.regimenName = regimenName;
    }

	
    public Integer getCycleNumber() {
    	return cycleNumber;
    }

	
    public void setCycleNumber(Integer cycleNumber) {
    	this.cycleNumber = cycleNumber;
    }

	
    public Date getDrugGroupStartDate() {
    	return drugGroupStartDate;
    }

	
    public void setDrugGroupStartDate(Date drugGroupStartDate) {
    	this.drugGroupStartDate = drugGroupStartDate;
    }

	
    public Integer getCycleLength() {
    	return cycleLength;
    }

	
    public void setCycleLength(Integer cycleLength) {
    	this.cycleLength = cycleLength;
    }

	
    public Boolean getIsCyclic() {
    	return isCyclic;
    }

	
    public void setIsCyclic(Boolean isCyclic) {
    	this.isCyclic = isCyclic;
    }
}
