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
import java.util.List;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Concept;
import org.openmrs.OpenmrsObject;

/**
 * Order sets are used to pre-define sets of orders in order to make the
 * ordering process easier i.e., pick from a list instead of having to manually
 * enter orders for common orders or groups of orders. Order sets can contain
 * 0-to-n members; each member can be a reference to an order-able concept, an
 * order template (pre-defined order), or another order set. Order set is a
 * definition of one or more related orders – e.g.,
 * "orders to consider when a patient presents with pneumonia" or
 * "common ways to order Penicillin", where someone may be picking & choosing
 * from the set of orders. Order group is a set of actual orders that are
 * grouped from some reason (e.g., they were ordered from a pre-defined order
 * set OR we come up with other reasons to group orders). For example, you might
 * define an order set FIRST LINE HIV THERAPIES that described two recommended
 * starting regimens, each of which contained three drugs. You haven't ordered
 * anything for any patient; rather, you've just created metadata defining the
 * set of orders to be considered in a specific situation. Later, a doctor in
 * the midst of placing orders for a patient finds your order set and uses it to
 * select the 2nd regimen. Three drugs (the three in the 2nd regimen) are added
 * to the orders table for the patient and marked as being in an order group
 * (basically tagging those orders as being created from your order set). The
 * order group – much like an obs group – is a grouping mechanism of actual
 * orders for a patient.
 */
public class OrderSet extends BaseOpenmrsMetadata implements java.io.Serializable {
	
	public static final long serialVersionUID = 1L;
	
	/**
	 * Defines all of the ways that member sets can be selected:
	 * ANY allows for multiple selection, ONE forces single selection amongst
	 * members, and ALL means that you must either order all members or none.
	 */
	public enum Operator {
		ANY, ONE, ALL
	}
	
	/**
	 * The primary key id for this OrderSet when saved
	 */
	private Integer id;
	
	/**
	 * Represents how members of the set can be selected
	 * @see Operator
	 */
	private Operator operator = Operator.ANY;
	
	/**
	 * If specified, this indicates the overall indication for this set of Orders, and the
	 * allowable indications for it's members.  If this Concept is a Set, then all OrderSetMembers
	 * must have Indications that fall within this ConceptSet.  If this Concept is not a Set, then
	 * all OrderSetMembers must have the same indication as this.  If this is left null, then
	 * no restrictions are placed upon what the underlying OrderSetMember indications can be.
	 */
	private Concept indication;
	
	/**
	 * If true, indicates that this OrderSet is cyclical, and when Ordered, may be repeated
	 */
	private boolean cyclical;
	
	/**
	 * If cyclical, indicates the length of time between the start of one cycle and the start of the next cyle
	 */
	private Integer cycleLengthInDays;

	/**
	 * List of all members within this OrderSet that may be of one of three types:
	 * An order-able Concept
	 * A template describing a pre-defined Order
	 * Another OrderSet
	 * This is a List in order to preserve the order in which the members have been
	 * added to this OrderSet, which may have meaning
	 */
	private List<OrderSetMember> members;
	
	/**
	 * Default Constructor
	 */
	public OrderSet() { }
	
	/**
	 * @see OpenmrsObject#getId()
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * @see OpenmrsObject#setId(Integer)
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	/**
	 * @return the indication
	 */
	public Concept getIndication() {
		return indication;
	}

	/**
	 * @param indication the indication to set
	 */
	public void setIndication(Concept indication) {
		this.indication = indication;
	}

	/**
	 * @return the cyclical
	 */
	public boolean isCyclical() {
		return cyclical;
	}

	/**
	 * @param cyclical the cyclical to set
	 */
	public void setCyclical(boolean cyclical) {
		this.cyclical = cyclical;
	}

	/**
	 * @return the cycleLengthInDays
	 */
	public Integer getCycleLengthInDays() {
		return cycleLengthInDays;
	}

	/**
	 * @param cycleLengthInDays the cycleLengthInDays to set
	 */
	public void setCycleLengthInDays(Integer cycleLengthInDays) {
		this.cycleLengthInDays = cycleLengthInDays;
	}

	/**
	 * @return the members
	 */
	public List<OrderSetMember> getMembers() {
		if (members == null) {
			members = new ArrayList<OrderSetMember>();
		}
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<OrderSetMember> members) {
		this.members = members;
	}
	
	/**
	 * @param member the member to add
	 */
	public void addMember(OrderSetMember member) {
		getMembers().add(member);
	}
	
	/**
	 * @param member the member to remove
	 */
	public void removeMember(OrderSetMember member) {
		getMembers().remove(member);
	}
}
