package uk.ac.ebi.ddi.task.ddibioprojectgenxml.model;

/**
 * Created by azorin on 29/11/2017.
 */
public enum SeriesAttribute {
    Series_title("Series_title"),
    Series_geo_accession("Series_geo_accession"),
    Series_status("Series_status"),
    Series_submission_date("Series_submission_date"),
    Series_last_update_date("Series_last_update_date"),
    Series_pubmed_id("Series_pubmed_id"),

    Series_summary("Series_summary"),
    Series_overall_design("Series_overall_design"),
    Series_type("Series_type"),
    Series_contributor("Series_contributor"),
    Series_sample_id("Series_sample_id"),
    Series_contact_name("Series_contact_name"),

    Series_contact_email("Series_contact_email"),
    Series_contact_institute("Series_contact_institute"),
    Series_contact_address("Series_contact_address"),
    Series_contact_city("Series_contact_city"),
    Series_contact_zip_postal_code("Series_contact_zip/postal_code "),
    Series_contact_country("Series_contact_country"),
    Series_supplementary_file("Series_supplementary_file"),
    Series_platform_id("Series_platform_id"),
    Series_platform_organism("Series_platform_organism"),
    Series_sample_organism("Series_sample_organism"),
    Series_sample_taxid("Series_sample_taxid"),
    Series_relation("Series_relation");

    private final String name;

    SeriesAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
