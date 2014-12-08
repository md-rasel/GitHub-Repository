package service;

import common.CommonConstants;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * Created by Md. Rasel on 12/6/14.
 */
public class BookDataConverterService {

    private static BookDataConverterService instance = null;

    protected BookDataConverterService() {
        // Exists only to defeat instantiation.
    }

    public static BookDataConverterService getInstance() {
        if(instance == null) {
            synchronized (BookDataConverterService.class) {
                instance = new BookDataConverterService();
            }
        }
        return instance;
    }

    public boolean isFileFound(String filename)
    {
        boolean isValid = true;
        File file = new File(filename);
        try {
            FileReader reader = new FileReader(file);
            reader.close();
        } catch (IOException e) {
            isValid = false;
        }
        return isValid;
    }

    public static boolean createTXTFile(String fileName, String fileContent) {
        boolean isCreated = true;
        try {
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(fileContent);
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            isCreated = false;
        }
        return isCreated;
    }

    public String readTextFile(String filename)
    {
        String content = null;
        File file = new File(filename);
        try {
            FileReader reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static boolean creatingXMLFile(String outputFileName, String fileContent) {

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("book");
            doc.appendChild(rootElement);

            Attr attr = doc.createAttribute("xmlns:b");
            attr.setValue("http://example.com/programming/test/book");
            rootElement.setAttributeNode(attr);

            String fileContentArr[] = fileContent.split(CommonConstants.SEPARATOR_NEW_LINE_RETURN);
            String element = "", nodeValue = "";
            String eachRowArr[];
            Element firstname = null, childName = null;

            for (String eachRow : fileContentArr) {
                eachRowArr = eachRow.split(CommonConstants.SEPARATOR_COLON);
                element = eachRowArr[0];
                nodeValue = eachRowArr[1];

                if (nodeValue.split(CommonConstants.SEPARATOR_COMMA).length > 1) {
                    firstname = doc.createElement(element);
                    eachRowArr = nodeValue.split(CommonConstants.SEPARATOR_COMMA);
                    for (String eachRow1 : eachRowArr)
                    {
                        nodeValue = eachRow1;
                        childName = doc.createElement(element.substring(0, element.length() - 1));
                        childName.appendChild(doc.createTextNode(nodeValue));
                        firstname.appendChild(childName);
                    }
                    rootElement.appendChild(firstname);
                } else {
                    firstname = doc.createElement(element);
                    firstname.appendChild(doc.createTextNode(nodeValue));
                    rootElement.appendChild(firstname);
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(outputFileName));

            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            return false;
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
            return false;
        }

        return true;
    }

    public static String readXMLFile(String fileName, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(fileName));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean convertingXMLtoTXTFile (String outputFileName, String fileContent) {

        String convertedFileContent = "", authorConcat = "";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(fileContent)));

            doc.getDocumentElement().normalize();

            convertedFileContent += "name : " + doc.getElementsByTagName("name").item(0).getTextContent() + CommonConstants.SEPARATOR_NEW_LINE_RETURN;

            NodeList nList = doc.getElementsByTagName("authors");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) nNode;
//                    authorConcat += eElement.getElementsByTagName("author").item(0).getTextContent() + CommonConstants.SEPARATOR_COMMA;

                    Element fstElmnt = (Element) nNode;
                    NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("author");
                    Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                    NodeList fstNm = fstNmElmnt.getChildNodes();

                    NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("author");
                    Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
                    NodeList lstNm = lstNmElmnt.getChildNodes();

                    authorConcat += ((Node) fstNm.item(0)).getNodeValue() + CommonConstants.SEPARATOR_COMMA + ((Node) lstNm.item(0)).getNodeValue();
                }
            }

            convertedFileContent += "authors : " + authorConcat.substring(0, authorConcat.length() - 1) + CommonConstants.SEPARATOR_NEW_LINE_RETURN;
            convertedFileContent += "published-date : " + doc.getElementsByTagName("published-date").item(0).getTextContent() + CommonConstants.SEPARATOR_NEW_LINE_RETURN;
            if (doc.getElementsByTagName("isbn").item(0) != null) {
                convertedFileContent += "isbn : " + doc.getElementsByTagName("isbn").item(0).getTextContent() + CommonConstants.SEPARATOR_NEW_LINE_RETURN;
            }

            createTXTFile(outputFileName, convertedFileContent);

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

}
