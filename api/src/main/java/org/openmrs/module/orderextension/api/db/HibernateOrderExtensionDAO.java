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
package org.openmrs.module.orderextension.api.db;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.orderextension.DrugRegimen;
import org.openmrs.module.orderextension.ExtendedOrderSet;
import org.openmrs.module.orderextension.ExtendedOrderSetMember;

/**
 * Hibernate implementation of the OrderExtension Data Access Interface
 */
public class HibernateOrderExtensionDAO implements OrderExtensionDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
    private DbSessionFactory sessionFactory;
    
	/**
	 * @see OrderExtensionDAO#getOrderSet(Integer)
	 */
	@Override
	public ExtendedOrderSet getOrderSet(Integer id) {
		return (ExtendedOrderSet) getCurrentSession().get(ExtendedOrderSet.class, id);
	}

	/**
	 * @see OrderExtensionDAO#getOrderSetByUuid(String)
	 */
	@Override
	public ExtendedOrderSet getOrderSetByUuid(String uuid) {
		String query = "FROM ExtendedOrderSet s WHERE s.uuid = :uuid";
		return (ExtendedOrderSet) getCurrentSession().createQuery(query).setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see OrderExtensionDAO#getNamedOrderSets(String, Concept, boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ExtendedOrderSet> getNamedOrderSets(String partialName, Concept indication, boolean includeRetired) {
		Criteria criteria = getCurrentSession().createCriteria(ExtendedOrderSet.class);
		criteria.add(Restrictions.isNotNull("name"));
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		if (partialName != null) {
			criteria.add(Restrictions.ilike("name", partialName, MatchMode.ANYWHERE));
		}
		if (indication != null) {
			criteria.add(Restrictions.eq("indication", indication));
		}
		criteria.addOrder(org.hibernate.criterion.Order.asc("name"));
		return criteria.list();
	}

	/**
	 * @see OrderExtensionDAO#saveOrderSet(ExtendedOrderSet)
	 */
	@Override
	public ExtendedOrderSet saveOrderSet(ExtendedOrderSet orderSet) {
		getCurrentSession().saveOrUpdate(orderSet);
		return orderSet;
	}

	/**
	 * @see OrderExtensionDAO#purgeOrderSet(ExtendedOrderSet)
	 */
	@Override
	public void purgeOrderSet(ExtendedOrderSet orderSet) {
		getCurrentSession().delete(orderSet);
	}
	
	/**
	 * @see OrderExtensionDAO#getOrderSetMember(Integer)
	 */
	@Override
	public ExtendedOrderSetMember getOrderSetMember(Integer id) {
		return (ExtendedOrderSetMember) getCurrentSession().get(ExtendedOrderSetMember.class, id);
	}

	/**
     * @see OrderExtensionDAO#getDrugOrdersForPatient(Patient, Concept)
     */
    @Override
    public List<DrugOrder> getDrugOrdersForPatient(Patient patient, Concept indication) {
    	Criteria criteria = getCurrentSession().createCriteria(DrugOrder.class);
    	if(patient != null) {
    		criteria.add(Restrictions.eq("patient", patient));
    	}
    	if(indication != null) {
    		criteria.add(Restrictions.eq("orderReason", indication));
    	}
		criteria.add(Restrictions.eq("voided", false));
    	return criteria.list();
    }
	
	/**
     * @see org.openmrs.module.orderextension.api.db.OrderExtensionDAO#getMaxNumberOfCyclesForRegimen(DrugRegimen)
     */
    @Override
    public Integer getMaxNumberOfCyclesForRegimen(DrugRegimen regimen) {

	    Date startDate = regimen.getFirstDrugOrderStartDate();

		// First get all of the Regimens related to this regimen (based on patient and orderset)
	    Criteria criteria = getCurrentSession().createCriteria(DrugRegimen.class);
	    criteria.add(Restrictions.eq("patient", regimen.getPatient()));
	    criteria.add(Restrictions.eq("orderSet", regimen.getOrderSet()));
	    criteria.add(Restrictions.eq("voided", false));
	    List<DrugRegimen> drugRegimens = criteria.list();

	    // Then, iterate across these, and for any with orders after the start date of this regimen, look at cycle number
	    Integer maxCycleNumber = null;
	    for (DrugRegimen r : drugRegimens) {
	    	for (Order o : r.getOrders()) {
	    		if (o.getVoided() == null || !o.getVoided()) {
	    			if (o.getEffectiveStartDate().compareTo(startDate) >= 0) {
	    				Integer cycleNumber = r.getCycleNumber();
	    				if (maxCycleNumber == null || cycleNumber > maxCycleNumber) {
	    					maxCycleNumber = cycleNumber;
					    }
				    }
			    }
		    }
	    }

	    return maxCycleNumber;
    }
	
	/**
	 * @see OrderExtensionDAO#getDrugRegimens(Patient)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DrugRegimen> getDrugRegimens(Patient patient) {
		Criteria criteria = getCurrentSession().createCriteria(DrugRegimen.class);
		criteria.add(Restrictions.eq("patient", patient));
		criteria.add(Restrictions.eq("voided", false));
		return criteria.list();
	}

	/**
	 * @return the sessionFactory
	 */
	private DbSession getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * @return the sessionFactory
	 */
	public DbSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 */
    public void setSessionFactory(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
