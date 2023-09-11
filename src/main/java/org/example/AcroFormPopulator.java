package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class AcroFormPopulator {

    public static int[] KM_JOHN = {78, 78, 41, 51, 70, 70};

    public static String FIELD_NUMBER = "number.1";


    public static String FIELD_DATE1 = "date.1";
    public static String FIELD_DATE2 = "hour.3";
    public static String FIELD_DATE3 = "hour.6";
    public static String[] FIELDS_DATES = {FIELD_DATE1, FIELD_DATE2, FIELD_DATE3};


    public static String FIELD_NAME1 = "blank_field.2";
    public static String FIELD_NAME2 = "name_last.2";
    public static String[] FIELDS_NAMES = {FIELD_NAME1, FIELD_NAME2};


    public static String FIELD_NACHALEN = "name_last.6";
    public static String FIELD_KRAEN = "name_last.7";

    public static String[] KM_FIELDS_JOHN = {"name_last.11", "name_last.16", "name_last.21", "name_last.26"};

    private String LAST_KRAEN_JOHN = "";


    PDDocument document;
    PDAcroForm acroForm;

    public AcroFormPopulator(String originalPdf) {
        System.out.println("--- OPENING FILE: " + originalPdf + " ---");
        File file = new File(originalPdf);

        try {
            document = PDDocument.load(file);
            acroForm = document.getDocumentCatalog().getAcroForm();
        } catch (Exception e) {
            System.out.println("intitialize error");
        }
    }

//    public static void main(String[] args) {
//        AcroFormPopulator abd = new AcroFormPopulator();
//        try {
//            abd.populateAndCopy(SRC, OUTPUT);
//        } catch (IOException e) {
//            System.out.println("Couldn't open file");
//            e.printStackTrace();
//        }
//    }

    public Map.Entry<String,String> populateAndCopy(String targetPdf, String date, int number, String nachalen) throws IOException {
        // if nachalen == "" dont write anything

        //        getAllFieldsAndTheirNames(acroForm);

        // check if it's the same name everywhere
        for (int i = 0; i < FIELDS_NAMES.length - 1; i++) {
            if (!Objects.equals(acroForm.getField(FIELDS_NAMES[i]).getValueAsString(), acroForm.getField(FIELDS_NAMES[i + 1]).getValueAsString())) {
                System.out.println("[ERROR] Names are not the same: (1) " + acroForm.getField(FIELDS_NAMES[i]).getValueAsString() +
                        " (2) " + acroForm.getField(FIELDS_NAMES[i + 1]).getValueAsString());
            }
        }

        if (nachalen != "") {
            writeNachalen(nachalen);
        }

        writeNumber(number);
        writeDates(date);

        String currentName = getCurrentName();
        String lastKM = "";
        switch (currentName) {
            case "":
                System.out.println("No name found in document");
                break;
            case "John":
                lastKM = writeKM(currentName, KM_FIELDS_JOHN, KM_JOHN);
                break;
        }
        Map.Entry<String,String> result = new AbstractMap.SimpleEntry<>(currentName,lastKM);

        document.save(targetPdf);
        document.close();
        System.out.println("Populated!");

        return result;
    }

    // writes KMs and returns the last kilometraj (kraen)
    private String writeKM(String name, String[] KM_FIELDS_NAME, int[] KM_NAME) {
        String kraenKilometraj = "";

        try {
            System.out.println("Filling out form for " + name);

            for (int i = 0; i < KM_FIELDS_NAME.length; i++) {
                System.out.println("CURRENT " + name + " " + KM_FIELDS_NAME[i]);

                String result ="";


                if (i == 0) {
                    result = String.valueOf(Integer.parseInt(acroForm.getField(FIELD_NACHALEN).getValueAsString()) + KM_NAME[i]);
                } else {
                    result = String.valueOf(Integer.parseInt(acroForm.getField(KM_FIELDS_NAME[i - 1]).getValueAsString()) + KM_NAME[i]);

                }

                acroForm.getField(KM_FIELDS_NAME[i]).setValue(result);

                if (i == KM_FIELDS_NAME.length - 1) {
                    kraenKilometraj = result;
                    acroForm.getField(FIELD_KRAEN).setValue(kraenKilometraj);
                }
            }
        } catch (Exception IOException) {
            IOException.printStackTrace();
            System.out.println("Couldn't write KM");
        }

        return kraenKilometraj;
    }

    public String getLastKraen(String name) {
        switch (name) {
            case "":
                System.out.println("No name found in document");
                return "";
            case "John":
                return LAST_KRAEN_JOHN;
        }

        return "";
    }

    public void writeDates(String date) throws IOException {
        for (String field : FIELDS_DATES) {
            acroForm.getField(field).setValue(date);
        }
    }

    public void writeNames(String name) throws IOException {
        for (String field : FIELDS_NAMES) {
            acroForm.getField(field).setValue(name);
        }
    }

    public void writeNachalen(String nachalen) throws IOException {
        acroForm.getField(FIELD_NACHALEN).setValue(nachalen);
    }

    // prints all fields with their fully qualified names
    public void getAllFieldsAndTheirNames() {
        List<PDField> fields = acroForm.getFields();
        for (PDField field : fields) {
            System.out.println(field.getFullyQualifiedName());
            System.out.println(field.getValueAsString());
        }
    }

    public String getCurrentName() {
        return acroForm.getField(FIELD_NAME1).getValueAsString();
    }

    private void writeNumber(int number) {
        try {
            acroForm.getField(FIELD_NUMBER).setValue(String.valueOf(number));
        } catch (Exception IOException) {
            System.out.println("Couldn't write number");
        }
    }
}
