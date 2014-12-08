package TestCases;

import common.CommonConstants;
import junit.framework.TestCase;
import model.BookDataConverterModel;

/**
 * Created by Md. Rasel on 12/8/14.
 */
public class BookDataConverterTestCases extends TestCase {

    private String inputFileName = "", outputFileExtn = "";

    public BookDataConverterTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception{
        super.setUp();
        inputFileName = "input-xml.xml";
        outputFileExtn = "json";
    }

    public void testBookDataConverter() {

        // Input Params Validation
        assertTrue(BookDataConverterModel.getInstance().isParamsValid(inputFileName + CommonConstants.SEPARATOR_EMPTY_SPACE + outputFileExtn));

        // Input File Format Validation
        assertTrue(BookDataConverterModel.getInstance().isFileNameFormatValid(inputFileName));

        // Input File Exist Validation
        assertTrue(BookDataConverterModel.getInstance().isInputFileValid(inputFileName));

        // Output File Extension Validation
        assertTrue(BookDataConverterModel.getInstance().isFileExtensionValid(outputFileExtn));

        // Input And Output File Extensions Same Validation
        assertFalse(BookDataConverterModel.getInstance().isSameFileExtensions(inputFileName, outputFileExtn));

        // Input File Contains ISBN Validation
        assertTrue(BookDataConverterModel.getInstance().isFileContainsISBN(inputFileName));

        // Removing Support TXT File Conversion
        assertTrue(BookDataConverterModel.getInstance().isConversionAllowedForTXT(inputFileName, outputFileExtn));

        String fileNames[] = inputFileName.split(CommonConstants.SEPARATOR_DOT);
        String inputFileExtn = fileNames[fileNames.length - 1];
        String fileContent = BookDataConverterModel.getInstance().gettingFileContent(inputFileExtn, inputFileName);
        assertTrue(BookDataConverterModel.getInstance().creatingOutputFile(inputFileExtn, outputFileExtn, inputFileExtn + "-to-" + outputFileExtn + "." + outputFileExtn, fileContent));
    }

    public void tearDown() throws Exception{
        super.tearDown();
    }
}
