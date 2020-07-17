openmrs-module-orderextension
=============================

Extensions to the OpenMRS 2.3 order data model and API, mainly providing support for cyclical drug regimens
Also provides an alternative regimen tab for the patient dashboard that takes advantage of these new features

# Changes required for upgrade to 2.3

* All Orders must be placed in the context of an Encounter

* OrderExtensionServiceImpl.addOrdersForPatient -> This needs to be fixed, lots of quick fixes made to get past validation errors, but not correct

* Migration needed to change any roles with "View Order Sets" privilege to have the core "Get Order Sets" privilege.
* Remove any assignment of "Delete Order Sets", this is not a supported privilege in core - use Manage Order Sets
* Make sure all service methods and other privilege checks are using the Core "Get Order Sets" or "Manage Order Sets" privilege.

* OrderEntryUtil needs to be fully implemented and fixed
* Changes to Ajax representations - see comments in OrderExtensionAjaxController



Further Info:

* Removing route from drug:  https://issues.openmrs.org/browse/TRUNK-3323
  - Belongs on DrugOrder, not Drug  (Drug form normally implies the route, and that's usually what you want to know, per Darius)

* Drug doseStrength (Double) + units (String) removed in favor of a single "strength" String, as this allows for
situations like combination drugs and other more general cases that the more strict doseStrength doesn't allow for.
  - See Burke and Darius comments here:  https://talk.openmrs.org/t/capturing-drug-strength-in-a-structured-way-in-database/1820/2
