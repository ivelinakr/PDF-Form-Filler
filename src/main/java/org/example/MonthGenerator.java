package org.example;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MonthGenerator {

    public int tempJohn = 4;
    int number = 1;
    HashMap<String, String> nameToKraenKM = new HashMap<>();
    String folderPathGIVEN = "lib/test";

    public MonthGenerator() {
        nameToKraenKM.put("John","");
    }

    public void generateDocsForMonth(String startDay, String endDay, String monthNumber, String year) {
        //work once
        //copy all files with new name for next date
        //open those new files now and work on them using last kraen for nachalen
        //copy new ones with new names...

        //YEAR MUST BE 03.03.2023 FORMAT WITH THE ZEROS
        String dateGIVEN = startDay + "." + monthNumber + "." + year;
        //initial work stage 1
        File[] files = getFilePaths(folderPathGIVEN);
        generateDocsForDay(dateGIVEN, files, true);

//stage 1 end
        int firstDay = Integer.parseInt(startDay);
        String firstDayStr = startDay;
        boolean seventhDay = false;
        int count = 1;

        for (int currentDay = firstDay; currentDay < Integer.parseInt(endDay); currentDay++) {
            count++;

            String currentDayStr = "";
            if (!seventhDay) {
                currentDayStr = convertIntDateToStr(currentDay);
            } else {
                currentDayStr = convertIntDateToStr(currentDay-1); //dont touch this
                currentDay++;
                seventhDay = false;
            }
            String nextFullDate = convertIntDateToStr(currentDay + 1) + "." + monthNumber + "." + year;



            String fileNamePattern = "Пътен лист_" + currentDayStr + "\\." + monthNumber + "\\." + year + "_.*\\.pdf"; // Pattern to match filenames


            // Create a FilenameFilter to match files by name pattern
            FilenameFilter filenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.matches(fileNamePattern);
                }
            };

            String genFolder = "C:\\Users\\njoy\\IdeaProjects\\pdfff2";
            File folder = new File(genFolder);
            // List files in the folder using the FilenameFilter
            File[] matchingFilesAfterStage1 = folder.listFiles(filenameFilter);

//            if ((currentDay+1 - firstDay) != 0 && (currentDay+1 - firstDay) % 6 == 0) {
            if (count == 6) {
                System.out.println("TRUE REACHED 7TH DAY " + currentDay +" - "+ firstDay);
//                nextFullDate = convertIntDateToStr(currentDay + 2) + "." + monthNumber + "." + year;
                seventhDay = true;
                count = 0;
//                continue;
            }
            generateDocsForDay(nextFullDate, matchingFilesAfterStage1, false);

        }

    }

    // initial work done on given file array.
    public void generateDocsForDay(String date, File[] files, Boolean FIRST_TIME_RUNNING) {

        // TRQBVA DA SE POPYLNI PURVI KILOMETRAJ I DATA

        if (files != null) {
            for (File file : files) {
                String SRC = file.getAbsolutePath();
                AcroFormPopulator afp = new AcroFormPopulator(SRC);

                // format: пътен лист_03-01-2023_John
                String OUTPUT = "Пътен лист_" + date + "_" + getLastWord(SRC);

                if (Objects.equals(getLastWord(SRC), "John") && tempJohn == 0) {
                    break;
                }

                try {


                    Map.Entry<String, String> entry = afp.populateAndCopy(OUTPUT, date, number, nameToKraenKM.get(afp.getCurrentName()));
                    System.out.println("ENTRY "+ entry.getKey() + " "+entry.getValue());
                    nameToKraenKM.replace(entry.getKey(), entry.getValue());
                    System.out.println(" ???????? " + nameToKraenKM.get(entry.getKey()));



                    number++;
                } catch (IOException e) {
                    System.out.println("Couldn't open file");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Files in folder path not found");
        }
    }

    public String getLastWord(String str) {
        // Split the input string by whitespace
        String[] words = str.split("_");
        // Check if there are words in the array
        if (words.length > 0) {
            // Get the last word (last element of the array)
            if (Objects.equals(words[words.length - 1], "John")) {
                tempJohn--;
            }

            return words[words.length - 1];
        } else {
            System.out.println("No words found in the input string.");
            return "";
        }
    }

    public static File[] getFilePaths(String folderPath) {
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            return folder.listFiles();

        } else {
            System.err.println("The specified path is not a valid directory.");
            return null;
        }
    }

    public String convertIntDateToStr(int currentDay) {
        if (currentDay < 10) {
            return "0" + currentDay;
        } else {
            return String.valueOf(currentDay);
        }
    }
}
