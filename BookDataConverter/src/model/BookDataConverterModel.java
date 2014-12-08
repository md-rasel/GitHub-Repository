package model;

import common.CommonConstants;
import service.BookDataConverterService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Md. Rasel on 12/6/14.
 */
public class BookDataConverterModel {

    private static BookDataConverterModel instance = null;

    protected BookDataConverterModel() {
        // Exists only to defeat instantiation.
    }

    public static BookDataConverterModel getInstance() {
        if(instance == null) {
            synchronized (BookDataConverterModel.class) {
                instance = new BookDataConverterModel();
            }
        }
        return instance;
    }

    public static Map validateFileNameAndExtension(String ioFileNames) {

        Map mapInstance = new HashMap();
        String inputFileName = "", outputFileExtn = "";

        try {
            String fileNames[] = ioFileNames.split(CommonConstants.SEPARATOR_EMPTY_SPACE);
            if (fileNames.length > 1) {
                inputFileName = fileNames[0];
                outputFileExtn = fileNames[1];

                mapInstance.put("HasErrors", false);

                if (!isFileNameFormatValid(inputFileName)) {
                    mapInstance.put("FileNameFormatError", "File Name Format Is Invalid");
                    mapInstance.put("HasErrors", true);
                }

                if (!isInputFileValid(inputFileName)) {
                    mapInstance.put("FileNotFoundError", "Input File Is Not Found");
                    mapInstance.put("HasErrors", true);
                }

                if (!isFileExtensionValid(outputFileExtn)) {
                    mapInstance.put("FileExtnError", "Input File Extension Is Not Correct");
                    mapInstance.put("HasErrors", true);
                }

                if (isSameFileExtensions(inputFileName, outputFileExtn)) {
                    mapInstance.put("FileExtnSameError", "Input And Output File Extensions Should Not Be Same");
                    mapInstance.put("HasErrors", true);
                }

                if (!isFileContainsISBN(inputFileName)) {
                    mapInstance.put("FileISBNError", "Input File Is Not Containing ISBN");
                    mapInstance.put("HasErrors", true);
                }

                if (!isConversionAllowedForTXT(inputFileName, outputFileExtn)) {
                    mapInstance.put("FileTXTAllowedError", "Conversion Support For TXT File Has Been Removed");
                    mapInstance.put("HasErrors", true);
                }

                if (!Boolean.parseBoolean(mapInstance.get("HasErrors").toString())) {
                    mapInstance.put("InputFileName", inputFileName);
                    mapInstance.put("InputFileExtn", inputFileName.split(CommonConstants.SEPARATOR_DOT)[1]);
                    mapInstance.put("OutputFileExtn", outputFileExtn);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return mapInstance;
    }

    public static boolean isParamsValid (String ioFileNames) {
        boolean isValid = false;
        if (ioFileNames.split(CommonConstants.SEPARATOR_EMPTY_SPACE).length == 2) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isInputFileValid(String inputFileName) {
        inputFileName = CommonConstants.INPUT_FILE_PATH + inputFileName;
        return BookDataConverterService.getInstance().isFileFound(inputFileName);
    }

    public static boolean isFileNameFormatValid(String fileName) {
        boolean isValid = false;
        String fileNames[] = fileName.split(CommonConstants.SEPARATOR_DOT);
        if (fileNames.length > 1 && isFileExtensionValid(fileNames[fileNames.length - 1])) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isFileExtensionValid(String fileExtension) {
        boolean isValid = false;
        for (String extension : CommonConstants.FILE_EXTENSIONS) {
            if (extension.equalsIgnoreCase(fileExtension)) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }

    public static boolean isSameFileExtensions(String inputFileName, String fileExtension) {
        boolean isValid = false;
        String fileNames[] = inputFileName.split(CommonConstants.SEPARATOR_DOT);
        if (fileNames[fileNames.length - 1].equalsIgnoreCase(fileExtension)) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isFileContainsISBN(String inputFileName) {
        boolean isValid = false;
        String fileNames[] = inputFileName.split(CommonConstants.SEPARATOR_DOT);
        String inputFileType =  fileNames[fileNames.length - 1];
        String fileContent = gettingFileContent(inputFileType, inputFileName);
        String fileContentArr[] = fileContent.split(CommonConstants.SEPARATOR_NEW_LINE_RETURN);
        String eachRowArr[];

        if (CommonConstants.IS_ISBN_VALIDATION_NEEDED) {
            if (inputFileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_TXT)) {
                for (String eachRow : fileContentArr) {
                    eachRowArr = eachRow.split(CommonConstants.SEPARATOR_COLON);
                    if (eachRowArr.length > 0 && eachRowArr[0].trim().equalsIgnoreCase(CommonConstants.ISBN_TAG_NAME)) {
                        isValid = true;
                    }
                }
            } else if (inputFileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_XML)) {
                for (String eachRow : fileContentArr) {
                    if (eachRow.trim().contains("<" + CommonConstants.ISBN_TAG_NAME + ">")) {
                        isValid = true;
                    }
                }
            }
        } else {
            isValid = true;
        }

        return isValid;
    }

    public static boolean isConversionAllowedForTXT(String inputFileName, String fileExtension) {
        boolean isValid = false;
        String fileNames[] = inputFileName.split(CommonConstants.SEPARATOR_DOT);
        if (CommonConstants.IS_ALLOWED_TXT_CONVERSION) {
            isValid = true;
        } else {
            if (fileNames[fileNames.length - 1].equalsIgnoreCase(CommonConstants.FILE_EXTENSION_TXT) || fileExtension.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_TXT)) {
                isValid = false;
            } else {
                isValid = true;
            }
        }
        return isValid;
    }

    public static boolean creatingOutputFile(String inputFileType, String outputFileType, String outputFileName, String fileContent) {
        boolean isSuccessfullySaved = true;
        outputFileName = CommonConstants.INPUT_FILE_PATH + outputFileName;
        if (inputFileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_XML) && outputFileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_TXT)) {
            fileContent = BookDataConverterService.getInstance().convertingXMLtoTXTFile(fileContent);
            isSuccessfullySaved = BookDataConverterService.getInstance().createTXTFile(outputFileName, fileContent);
        } else if ((inputFileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_TXT) || inputFileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_JSON)) && outputFileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_XML)) {
            isSuccessfullySaved = BookDataConverterService.getInstance().creatingXMLFile(outputFileName, fileContent);
        } else if (inputFileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_XML) && outputFileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_JSON)) {
            fileContent = BookDataConverterService.getInstance().convertingXMLtoTXTFile(fileContent);
            isSuccessfullySaved = BookDataConverterService.getInstance().creatingJSONFile(outputFileName, fileContent);
        }
        return isSuccessfullySaved;
    }

    public static String gettingInputFileFormat(String inputFileExtension) {
        String inputFileExtnMessage = "";
        if (inputFileExtension.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_TXT)) {
            inputFileExtnMessage = "Book Data Is In TEXT Format";
        } else if (inputFileExtension.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_XML)) {
            inputFileExtnMessage = "Book Data Is In XML Format";
        } else if (inputFileExtension.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_JSON)) {
            inputFileExtnMessage = "Book Data Is In JSON Format";
        }
        return inputFileExtnMessage;
    }

    public static String gettingFileContent(String fileType, String fileName) {
        String fileContent = "";
        fileName = CommonConstants.INPUT_FILE_PATH + fileName;
        if (fileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_TXT)) {
            fileContent = BookDataConverterService.getInstance().readTextFile(fileName);
        } else if (fileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_XML)) {
            fileContent = BookDataConverterService.getInstance().readTextFile(fileName);
            fileContent = BookDataConverterService.getInstance().readXMLFile(fileContent, CommonConstants.XML_FILE_INDENT);
        } else if (fileType.equalsIgnoreCase(CommonConstants.FILE_EXTENSION_JSON)) {
            fileContent = BookDataConverterService.getInstance().readingJSONFile(fileName);
        }
        return fileContent;
    }

    public static String modifyFilesContentForDisplay (String capitalTagName, String fileContent) {
        String fileContentArr[] = fileContent.split(CommonConstants.SEPARATOR_NEW_LINE_RETURN);
        String modFileContent = "", element = "", nodeValue = "";
        String eachRowArr[];
        for (String eachRow : fileContentArr) {
            eachRowArr = eachRow.split(CommonConstants.SEPARATOR_COLON);
            if (eachRowArr.length > 1) {
                element = eachRowArr[0];
                nodeValue = eachRowArr[1];
                if (element.trim().equalsIgnoreCase(capitalTagName)) {
                    nodeValue = nodeValue.toUpperCase();
                    eachRow = element + CommonConstants.SEPARATOR_COLON + nodeValue;
                }
            }
            modFileContent += eachRow + CommonConstants.SEPARATOR_NEW_LINE_RETURN;
        }
        return modFileContent;
    }
}
