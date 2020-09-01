/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 *  obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.orderextension;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.util.OpenmrsUtil;

@Ignore
public class CreateInitialDataSet extends BaseModuleContextSensitiveTest {
	
	/**
	 * This test creates an xml dbunit file from the current database connection information found
	 * in the runtime properties. This method has to "skip over the base setup" because it tries to
	 * do things (like initialize the database) that shouldn't be done to a standard mysql database.
	 * 
	 * @throws Exception
	 */
	@Test
	@SkipBaseSetup
	public void shouldCreateInitialTestDataSetXmlFile() throws Exception {
		
		// only run this test if it is being run alone.
		// this allows the junit-report ant target and the "right-
		// click-on-/test/api-->run as-->junit test" methods to skip
		// over this whole "test"
		if (getLoadCount() != 1) {
            return;
        }

		IDatabaseConnection connection = new DatabaseConnection(getConnection());

		QueryDataSet initialDataSet = new QueryDataSet(connection);
		initialDataSet.addTable("orderextension_order_set", "SELECT * FROM orderextension_order_set where id = 21");
		initialDataSet.addTable("orderextension_order_set_member", "SELECT * FROM orderextension_order_set_member where order_set_id = 21");

        initialDataSet.addTable("concept",
                "SELECT * from concept where concept_id in (" +
                        "select concept_id from orderextension_order_set_member where order_set_id = 21 union " +
                        "select indication from orderextension_order_set_member where order_set_id = 21 union " +
                        "select route from orderextension_order_set_member where order_set_id = 21 union " +
                        "select dosage_form from drug where drug_id in (select drug_id from orderextension_order_set_member where order_set_id = 21) union " +
                        "select route from drug where drug_id in (select drug_id from orderextension_order_set_member where order_set_id = 21)" +
                        ")");
        initialDataSet.addTable("concept_name",
                "SELECT * from concept_name where concept_id in (" +
                        "select concept_id from orderextension_order_set_member where order_set_id = 21 union " +
                        "select indication from orderextension_order_set_member where order_set_id = 21 union " +
                        "select route from orderextension_order_set_member where order_set_id = 21" +
                        ")");
        initialDataSet.addTable("drug", "SELECT * from drug where drug_id in (select drug_id from orderextension_order_set_member where order_set_id = 21)");

        File outputFile = new File(OpenmrsUtil.getApplicationDataDirectory(), "orderSets.xml");
        System.out.println("Writing to file: " + outputFile.getAbsolutePath());
		FlatXmlDataSet.write(initialDataSet, new FileOutputStream(outputFile));
	}
	
	/**
	 * @see BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}

    /**
     * @return MS Note: use port 3306 as standard, 5538 for sandbox 5.5 mysql environment
     */
    @Override
    public Properties getRuntimeProperties() {
        Properties p = super.getRuntimeProperties();
        p.setProperty("connection.url", "jdbc:mysql://localhost:3306/openmrs_rwink?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
        p.setProperty("connection.username", "openmrs");
        p.setProperty("connection.password", "openmrs");
        p.setProperty("junit.username", "mseaton");
        p.setProperty("junit.password", "Test1234");
        return p;
    }

    @Before
    public void setupForTest() throws Exception {
        if (!Context.isSessionOpen()) {
            Context.openSession();
        }
        Context.clearSession();
        authenticate();
    }
}
