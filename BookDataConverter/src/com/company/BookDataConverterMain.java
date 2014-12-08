package com.company;

import model.BookDataConverterModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by Md. Rasel on 12/8/14.
 */

public class BookDataConverterMain {

    public static void main(String[] args) {

        String fileContent = "", inputFileTypeMsg = "", outputFileName = "", modTagName = "";
        System.out.println("Enter input file name and output file extension [separating space e.g. input-txt.txt xml] : ");

        try {
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            String ioFileNames = bufferRead.readLine();

            if (BookDataConverterModel.getInstance().isParamsValid(ioFileNames.trim())) {
                Map mapInstance = BookDataConverterModel.getInstance().validateFileNameAndExtension(ioFileNames.trim());
                if (!Boolean.parseBoolean(mapInstance.get("HasErrors").toString())) {
                    System.out.println("Reading Input...");
                    System.out.println("++++");

                    fileContent = BookDataConverterModel.getInstance().gettingFileContent(mapInstance.get("InputFileExtn").toString(), mapInstance.get("InputFileName").toString());
                    System.out.println(fileContent);

                    System.out.println("----");
                    System.out.println("Guessing Input File Format...");
                    System.out.println("++++");

                    inputFileTypeMsg = BookDataConverterModel.getInstance().gettingInputFileFormat(mapInstance.get("InputFileExtn").toString());
                    System.out.println(inputFileTypeMsg);

                    System.out.println("Converting to " + mapInstance.get("OutputFileExtn").toString().toUpperCase() + " format");
                    outputFileName = mapInstance.get("InputFileExtn").toString() + "-to-" + mapInstance.get("OutputFileExtn").toString() + "." + mapInstance.get("OutputFileExtn").toString();
                    BookDataConverterModel.getInstance().creatingOutputFile(mapInstance.get("InputFileExtn").toString(), mapInstance.get("OutputFileExtn").toString(), outputFileName, fileContent);

                    System.out.println("Here is the output...");
                    System.out.println("++++");
                    fileContent = BookDataConverterModel.getInstance().gettingFileContent(mapInstance.get("OutputFileExtn").toString(), outputFileName);
                    modTagName = "name";
                    fileContent = BookDataConverterModel.getInstance().modifyFilesContentForDisplay(modTagName, fileContent);
                    System.out.println(fileContent);

                } else {
                    int errNo = 0;
                    System.out.println("Following Error Introduces : ");
                    if (mapInstance.containsKey("FileNameFormatError")) {
                        System.out.println(++errNo + ". " + mapInstance.get("FileNameFormatError"));
                    }
                    if (mapInstance.containsKey("FileNotFoundError")) {
                        System.out.println(++errNo + ". " + mapInstance.get("FileNotFoundError"));
                    }
                    if (mapInstance.containsKey("FileExtnError")) {
                        System.out.println(++errNo + ". " + mapInstance.get("FileExtnError"));
                    }
                    if (mapInstance.containsKey("FileExtnSameError")) {
                        System.out.println(++errNo + ". " + mapInstance.get("FileExtnSameError"));
                    }
                    if (mapInstance.containsKey("FileISBNError")) {
                        System.out.println(++errNo + ". " + mapInstance.get("FileISBNError"));
                    }
                }
            } else {
                System.out.println("Input file name and output file extension is not correct.");
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }

    }
}

