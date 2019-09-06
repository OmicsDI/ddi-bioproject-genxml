package uk.ac.ebi.ddi.task.ddibioprojectgenxml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.ddi.api.readers.utils.Constants;
import uk.ac.ebi.ddi.api.readers.utils.Transformers;
import uk.ac.ebi.ddi.service.db.service.dataset.IDatasetService;
import uk.ac.ebi.ddi.task.ddibioprojectgenxml.model.BioprojectDataset;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class DdiBioProjectGenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DdiBioProjectGenService.class);

    @Autowired
    private BioProjectService bioProjectService;

    @Autowired
    private IDatasetService datasetService;


    public List<Entry> getDatasets(File inputFile, String databaseName) throws Exception {
        List<BioprojectDataset> datasets = bioProjectService.getDatasets(inputFile, databaseName);
        List<Entry> entries = new ArrayList<>();
        for (BioprojectDataset dataset : datasets) {
            if (dataset.getIdentifier() != null) {
                dataset.addOmicsType(Constants.GENOMICS_TYPE);
                String accession = dataset.getIdentifier();
                if (datasetService.existsBySecondaryAccession(accession)) {
                    // Dataset already exists in OmicsDI,
                    // TODO: add some data
                    LOGGER.info("Accession " + accession + " exists as secondary accession");
                } else {
                    entries.add(Transformers.transformAPIDatasetToEntry(dataset)); //
                }
            }
        }
        return entries;
    }
}
