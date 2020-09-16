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

import org.apache.commons.io.FileUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.hibernate.dialect.MySQLDialect;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.util.OpenmrsUtil;

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

		// 50 = Non-Hodgkin Lymphoma CHOP Adult ; 82 = AZT + 3TC + DTG
		String orderSets = " order_set_id in (50,82)";

		QueryDataSet initialDataSet = new QueryDataSet(connection);

		// 2 = Indication, 5 = Dose Units, 6 = Route
		StringBuilder cq = new StringBuilder();
		cq.append("select distinct trim(replace(SUBSTRING_INDEX(SUBSTRING_INDEX(order_template, ',', 2), ':', -1), '\"','')) from order_set_member where " + orderSets);
		cq.append(" UNION ");
		cq.append("select distinct trim(replace(SUBSTRING_INDEX(SUBSTRING_INDEX(order_template, ',', 5), ':', -1), '\"','')) from order_set_member where " + orderSets);
		cq.append(" UNION ");
		cq.append("select distinct trim(replace(SUBSTRING_INDEX(SUBSTRING_INDEX(order_template, ',', 6), ':', -1), '\"','')) from order_set_member where " + orderSets);
		cq.append(" UNION ");
		cq.append("select uuid from concept where concept_id in (select category from order_set where " + orderSets + ")");
		cq.append(" UNION ");
		cq.append("select uuid from concept where concept_id in (select concept_id from order_frequency)");
		cq.append(" UNION ");
		cq.append("select property_value from global_property where property like 'order.%ConceptUuid' and property_value is not null");
		cq.append(" UNION ");
		cq.append("select uuid from concept where concept_id in (select cs.concept_id from concept_set cs inner join concept c on cs.concept_set = c.concept_id and c.uuid = '162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA')");
		cq.append(" UNION ");
		cq.append("select uuid from concept where concept_id in (select cs.concept_id from concept_set cs inner join concept c on cs.concept_set = c.concept_id and c.uuid = '162394AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA')");
		cq.append(" UNION ");
		cq.append("select uuid from concept where concept_id in (select cs.concept_id from concept_set cs inner join concept c on cs.concept_set = c.concept_id and c.uuid = '1732AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA')");
		cq.append(" UNION ");
		cq.append("select distinct uuid from concept where concept_id in (select concept_id from drug where uuid in (");
		cq.append("    select distinct trim(replace(SUBSTRING_INDEX(SUBSTRING_INDEX(order_template, ',', 3), ':', -1), '\"','')) from order_set_member where " + orderSets);
		cq.append("))");
		cq.append(" UNION ");
		cq.append("select distinct uuid from concept where concept_id in (select dosage_form from drug where uuid in (");
		cq.append("    select distinct trim(replace(SUBSTRING_INDEX(SUBSTRING_INDEX(order_template, ',', 3), ':', -1), '\"','')) from order_set_member where " + orderSets);
		cq.append("))");
		addTable(initialDataSet, "concept", "select * from concept where uuid in (" + cq.toString() + ")");
		addTable(initialDataSet, "concept_name", "select cn.* from concept_name cn where cn.concept_name_type = 'FULLY_SPECIFIED' and cn.locale = 'en' and cn.concept_id in (select concept_id from concept where uuid in (" + cq.toString() + "))");

		StringBuilder csq = new StringBuilder();
		csq.append("select cs.* from concept_set cs inner join concept c on cs.concept_set = c.concept_id ");
		csq.append("where c.uuid in ('162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA','162394AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA','1732AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA')");
		addTable(initialDataSet, "concept_set", csq.toString());

		addTable(initialDataSet, "order_frequency", "select * from order_frequency");

		StringBuilder dq = new StringBuilder();
		dq.append("select * from drug where uuid in (");
		dq.append("    select distinct trim(replace(SUBSTRING_INDEX(SUBSTRING_INDEX(order_template, ',', 3), ':', -1), '\"','')) from order_set_member where " + orderSets);
		dq.append(")");
		addTable(initialDataSet, "drug", dq.toString());

		addTable(initialDataSet, "order_set", "SELECT * FROM order_set where " + orderSets);
		addTable(initialDataSet, "orderextension_order_set", "SELECT * FROM orderextension_order_set where id in (50,82)");
		addTable(initialDataSet, "order_set_member", "SELECT * FROM order_set_member where " + orderSets);

		addTable(initialDataSet, "encounter_type", "select * from encounter_type where name = 'Drug Order Encounter'");
		addTable(initialDataSet, "global_property", "select * from global_property where property like 'orderextension.%' or property like 'order.%'");

		File outputFile = new File(OpenmrsUtil.getApplicationDataDirectory(), "orderSets.xml");
        System.out.println("Writing to file: " + outputFile.getAbsolutePath());
		FlatXmlDataSet.write(initialDataSet, new FileOutputStream(outputFile));

		String xmlFileContents = FileUtils.readFileToString(outputFile, "UTF-8");
		xmlFileContents = xmlFileContents.replace("short_name=\"\" ", "");
		xmlFileContents = xmlFileContents.replace("description=\"\" ", "");
		xmlFileContents = xmlFileContents.replace("operator=\"ANY\"", "operator=\"ANY\" description=\"No description\"");
		xmlFileContents = xmlFileContents.replaceAll("creator=\"[0-9]*\"", "creator=\"1\"");
		xmlFileContents = xmlFileContents.replaceAll("_by=\"[0-9]*\"", "_by=\"1\"");
		xmlFileContents = xmlFileContents.replaceAll("route=\"[0-9]*\" ", "");
		xmlFileContents = xmlFileContents.replace("class_id=\"19\"", "class_id=\"11\""); // ICD-10
		xmlFileContents = xmlFileContents.replace("class_id=\"20\"", "class_id=\"20\""); // Units
		xmlFileContents = xmlFileContents.replace("class_id=\"21\"", "class_id=\"19\""); // Frequency

		FileUtils.writeStringToFile(outputFile, xmlFileContents, "UTF-8");
	}

	private void addTable(QueryDataSet initialDataSet, String table, String query) throws Exception {
		System.out.println(query);
		initialDataSet.addTable(table, query);
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
	    System.setProperty("databaseUrl", "jdbc:mysql://localhost:3308/openmrs?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
	    System.setProperty("databaseUsername", "root");
	    System.setProperty("databasePassword", "password");
	    System.setProperty("databaseDriver", "com.mysql.jdbc.Driver");
	    System.setProperty("databaseDialect", MySQLDialect.class.getName());
	    return super.getRuntimeProperties();
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
