package uk.ac.ebi.ddi.task.ddibioprojectgenxml.model;

/**
 * Created by azorin on 29/11/2017.
 */
public enum SampleAttribute {
    Sample_title("Sample_title"),
    Sample_geo_accession("Sample_geo_accession"),
    Sample_status("Sample_status"),
    Sample_submission_date("Sample_submission_date"),
    Sample_last_update_date("Sample_last_update_date"),
    Sample_type("Sample_type"),
    Sample_channel_count("Sample_channel_count"),
    Sample_source_name_ch1("Sample_source_name_ch1"),
    Sample_organism_ch1("Sample_organism_ch1"),
    Sample_taxid_ch1("Sample_taxid_ch1"),
    Sample_characteristics_ch1("Sample_characteristics_ch1"),
    Sample_growth_protocol_ch1("Sample_growth_protocol_ch1"),
    Sample_molecule_ch1("Sample_molecule_ch1"),
    Sample_extract_protocol_ch1("Sample_extract_protocol_ch1"),
    Sample_data_processing("Sample_data_processing"),
    Sample_platform_id("Sample_platform_id"),
    Sample_contact_name("Sample_contact_name"),
    Sample_contact_email("Sample_contact_email"),
    Sample_contact_institute("Sample_contact_institute"),
    Sample_contact_address("Sample_contact_address"),
    Sample_contact_city("Sample_contact_city"),
    Sample_contact_zip_postal_code("Sample_contact_zip/postal_code"),
    Sample_contact_country("Sample_contact_country"),
    Sample_instrument_model("Sample_instrument_model"),
    Sample_library_selection("Sample_library_selection"),
    Sample_library_source("Sample_library_source"),
    Sample_library_strategy("Sample_library_strategy"),
    Sample_relation("Sample_relation"),
    Sample_supplementary_file_1("Sample_supplementary_file_1"),
    Sample_supplementary_file_2("Sample_supplementary_file_2"),
    Sample_series_id("Sample_series_id"),
    Sample_data_row_count("Sample_data_row_count");

    private final String name;

    SampleAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
