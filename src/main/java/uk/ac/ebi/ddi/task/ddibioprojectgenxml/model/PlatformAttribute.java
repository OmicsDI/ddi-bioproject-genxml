package uk.ac.ebi.ddi.task.ddibioprojectgenxml.model;

/**
 * Created by azorin on 29/11/2017.
 */
public enum PlatformAttribute {

    Platform_title("Platform_title"),
    Platform_geo_accession("Platform_geo_accession"),
    Platform_status("Platform_status"),
    Platform_submission_date("Platform_submission_date"),
    Platform_last_update_date("Platform_last_update_date"),
    Platform_technology("Platform_technology"),
    Platform_distribution("Platform_distribution"),
    Platform_organism("Platform_organism"),
    Platform_taxid("Platform_taxid"),
    Platform_manufacturer("Platform_manufacturer"),
    Platform_manufacture_protocol("Platform_manufacture_protocol"),
    Platform_description("Platform_description"),
    Platform_web_link("Platform_web_link"),
    Platform_contact_name("Platform_contact_name"),
    Platform_contact_email("Platform_contact_email"),
    Platform_contact_phone("Platform_contact_phone"),
    Platform_contact_institute("Platform_contact_institute"),
    Platform_contact_address("Platform_contact_address"),
    Platform_contact_city("Platform_contact_city"),
    Platform_contact_state("Platform_contact_state"),
    Platform_contact_zip_postal_code("Platform_contact_zip_postal_code"),
    Platform_contact_country("Platform_contact_country"),
    Platform_contact_web_link("Platform_contact_web_link"),
    Platform_relation("Platform_relation"),
    Platform_series_id("Platform_series_id"),
    Platform_data_row_count("Platform_data_row_count");

    private final String name;

    PlatformAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
