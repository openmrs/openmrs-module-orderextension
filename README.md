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


