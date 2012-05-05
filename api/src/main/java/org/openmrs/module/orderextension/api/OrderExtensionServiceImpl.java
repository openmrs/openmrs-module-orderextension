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
package org.openmrs.module.orderextension.api;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.orderextension.OrderGroup;
import org.openmrs.module.orderextension.OrderSet;
import org.openmrs.module.orderextension.OrderSetMember;
import org.openmrs.module.orderextension.RegimenExtension;
import org.openmrs.module.orderextension.api.db.OrderExtensionDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core implementation of the OrderExensionService
 */
@Transactional
public class OrderExtensionServiceImpl extends BaseOpenmrsService implements OrderExtensionService {
	
	private OrderExtensionDAO dao;

	/**
	 * @see OrderExtensionService#getOrderSet(Integer)
	 */
	@Override
	@Transactional(readOnly=true)
	public OrderSet getOrderSet(Integer id) {
		return dao.getOrderSet(id);
	}

	/**
	 * @see OrderExtensionService#getOrderSetByUuid(String)
	 */
	@Override
	@Transactional(readOnly=true)
	public OrderSet getOrderSetByUuid(String uuid) {
		return dao.getOrderSetByUuid(uuid);
	}

	/**
	 * @see OrderExtensionService#getAllOrderSets(boolean)
	 */
	@Override
	@Transactional(readOnly=true)
	public List<OrderSet> getNamedOrderSets(boolean includeRetired) {
		return dao.getNamedOrderSets(null, null, includeRetired);
	}

	/**
	 * @see OrderExtensionService#findOrderSets(String, Concept)
	 */
	@Override
	@Transactional(readOnly=true)
	public List<OrderSet> findAvailableOrderSets(String partialName, Concept indication) {
		return dao.getNamedOrderSets(partialName, indication, false);
	}

	/**
	 * @see OrderExtensionService#saveOrderSet(OrderSet)
	 */
	@Override
	@Transactional
	public OrderSet saveOrderSet(OrderSet orderSet) {
		return dao.saveOrderSet(orderSet);
	}

	/**
	 * @see OrderExtensionService#purgeOrderSet(OrderSet)
	 */
	@Override
	@Transactional
	public void purgeOrderSet(OrderSet orderSet) {
		dao.purgeOrderSet(orderSet);
	}

	/**
	 * @see OrderExtensionService#getOrderSetMember(Integer)
	 */
	@Transactional(readOnly=true)
	@Override
	public OrderSetMember getOrderSetMember(Integer id) {
		return dao.getOrderSetMember(id);
	}

	/**
	 * @see OrderExtensionService#getParentOrderSets(OrderSet)
	 */
	@Transactional(readOnly=true)
	@Override
	public List<OrderSet> getParentOrderSets(OrderSet orderSet) {
		return dao.getParentOrderSets(orderSet);
	}

	/**
	 * @see OrderExtensionService#getOrderGroups(Patient, Class)
	 */
	@Override
	public <T extends OrderGroup> List<T> getOrderGroups(Patient patient, Class<T> type) {
		return dao.getOrderGroups(patient, type);
	}
	
	/**
     * @see org.openmrs.module.ORDERextension.api.OrderExtensionService#getCurrentRegimens(org.openmrs.Patient, java.util.Date)
     */
    @Override
    public List<RegimenExtension> getCurrentRegimens(Patient patient, Date effectiveOn) {
	    return dao.getCurrentRegimens(patient, effectiveOn);
    }

	/**
     * @see org.openmrs.module.ORDERextension.api.OrderExtensionService#getFutureRegimens(org.openmrs.Patient, java.util.Date)
     */
    @Override
    public List<RegimenExtension> getFutureRegimens(Patient patient, Date effectionOn) {
	   return dao.getFutureRegimens(patient, effectionOn);
    }

	/**
     * @see org.openmrs.module.ORDERextension.api.OrderExtensionService#getCompletedRegimens(org.openmrs.Patient, java.util.Date)
     */
    @Override
    public List<RegimenExtension> getCompletedRegimens(Patient patient, Date effectiveOn) {
	    return dao.getCompletedRegimens(patient, effectiveOn);
    }

	/**
     * @see org.openmrs.module.ORDERextension.api.OrderExtensionService#getCompletedRegimens(org.openmrs.Patient, java.util.Date, java.util.Date)
     */
    @Override
    public List<RegimenExtension> getCompletedRegimens(Patient patient, Date effectiveOn, Date startDate) {
	    return dao.getCompletedRegimens(patient, effectiveOn, startDate);
    }

	/**
     * @see org.openmrs.module.ORDERextension.api.OrderExtensionService#getAllRegimens(org.openmrs.Patient, boolean)
     */
    @Override
    public List<RegimenExtension> getAllRegimens(Patient patient, boolean includeVoided) {
	    return dao.getAllRegimens(patient, includeVoided);
    }

	/**
	 * @return the dao
	 */
	public OrderExtensionDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(OrderExtensionDAO dao) {
		this.dao = dao;
	}
}
