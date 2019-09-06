package uk.ac.ebi.ddi.task.ddibioprojectgenxml.model;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by azorin on 28/11/2017.
 */
public class SoftFile {

    protected String type; //SAMPLE,SERIES,..
    protected String id;
    protected Map<String, List<String>> attributes = new HashMap<>();

    public SoftFile(File file, Set<String> allowedAttribute) throws IOException {
        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                if (line.startsWith("^")) {
                    SoftFileEntry entry = parseLine(line);
                    type = entry.getKey();
                    id = entry.getValue();
                } else if (line.startsWith("!")) {
                    SoftFileEntry entry = parseLine(line);
                    if (!allowedAttribute.contains(entry.getKey())) {
                        continue;
                    }
                    List<String> values = attributes.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
                    values.add(entry.getValue());
                }
//                else if (line.startsWith("#")) {
//                    // TODO: data header line
//                } else {
//                    // TODO: data line
//                }
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

    public List<String> getAttribute(String attribute) {
        if (attributes.containsKey(attribute)) {
            return attributes.get(attribute);
        }
        return new ArrayList<>();
    }

    private SoftFileEntry parseLine(String line) {
        SoftFileEntry result = new SoftFileEntry();

        String[] val = line.split("=");

        if (val.length > 0) {
            result.setKey(val[0].substring(1).trim());
        }
        if (val.length > 1) {
            result.setValue(val[1].trim());
        }
        return result;
    }

    public String getFirstAttribute(String key) {
        if (attributes.containsKey(key)) {
            if (null != attributes.get(key)) {
                if (attributes.get(key).size() > 0) {
                    return attributes.get(key).get(0);
                }
            }
        }
        return null;
    }

    /***
     find value for given attribute, started with "prefix:"
     ***/
    public String findAttributeValue(String key, String prefix) {
        for (String value : getAttribute(key)) {
            if (value.startsWith(prefix + ":")) {
                return value.replace(prefix + ":", "").trim();
            }
        }
        return null;
    }
}
