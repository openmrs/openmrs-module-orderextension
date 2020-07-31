openmrs-module-orderextension
=============================

Extensions to the OpenMRS 2.3 order data model and API, mainly providing support for cyclical drug regimens
Also provides an alternative regimen tab for the patient dashboard that takes advantage of these new features

Testing cases:

- Order set creation, add/remove/re-order members, edit members, edit order set, etc. (with extended data)
- "" for order groups
- Drug Order Comparator test fix
- Drug order: including special fields
- Order HIV orders
- Order cyclical order set
- Order non-cyclical order set
- Delay order group
- Print chemo

Screens

* Admin>Manage Order Sets
* Patient dashboard>Regimens tab
* Drug list view
* Calendar view
* Report: Oncology Chemotherapy Treatment Administration Plan

Workflows

* Order drug
  - patient dashboard> Regimens tab> Add New Medication>Add New Medication> choose drug> fill form
    - Within this function, add new medicaiton is a new button on the 'add new medication' screen
    - On the drug order form, reason for prescription, administration instructions, warnings/precautions are new from orderextension
* order a set
  - Patient Dashboard> Add New Medication> Select Order set> Start date + (optional) number of cycles

* Register patient for chemotherapy and print treatment administration
  - Registers patient for chemo. This adds an obs, plus runs the chemo treatment administration plan
* Chemotherapy treatment plan summary
  - Lists the chemo cycles planned
* Calendar view
  - (Uncommonly used)
  - Patient dashboard>Calendar view
  - Shows a traditional 7 day calendar with calendar events for each drug
* Display orders with start dates
  - This is the primary view of orders in regimens tab
    - Includes ability to edit an order
* Add drug to cycle
  - Same as a drug order, but adds it to an order group
* Change cycle Start Date: 
  - New start date
  - Whether to change for all future cycles

Future design considerations for the future from OpenMRS and FHIR:

https://talk.openmrs.org/t/chemotherapy-ordering-data-design/19133/6?u=mseaton
https://github.com/openmrs/openmrs-module-oncology/blob/master/docs/Mockups.md
https://github.com/dearmasm/openmrs-module-oncology/blob/master/regimens/CHOP.yaml
https://github.com/dearmasm/openmrs-module-oncology/blob/master/utils/YAAR_DOCS.md

https://www.hl7.org/fhir/requestgroup.html
https://www.hl7.org/fhir/plandefinition.html
https://www.hl7.org/fhir/datatypes.html#Timing
http://hl7.org/fhir/medicationrequest-definitions.html#MedicationRequest.groupIdentifier
https://www.hl7.org/fhir/plandefinition-example-kdn5-simplified.json.html
https://www.hl7.org/fhir/requestgroup-kdn5-example.json.html


