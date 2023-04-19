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

import org.openmrs.DrugOrder;
import org.openmrs.module.orderextension.util.DrugOrderWrapper;

import java.util.Comparator;

/**
 * Sorts Drug Orders based on order set sort weight, then by date and then by primary key id
 * @see DrugOrderWrapper
 */
public class DrugOrderComparator implements Comparator<DrugOrder> {

    /**
     * @see Comparator#compare(Object, Object)
     */
    @Override
    public int compare(DrugOrder r1, DrugOrder r2) {
        DrugOrderWrapper w1 = new DrugOrderWrapper(r1);
        DrugOrderWrapper w2 = new DrugOrderWrapper(r2);
        return w1.compareTo(w2);
     }
}
