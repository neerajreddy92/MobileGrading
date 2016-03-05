package com.mobile.bolt.Parser;

import android.util.Log;

import com.google.zxing.qrcode.encoder.QRCode;
import com.mobile.bolt.Model.QrCode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Neeraj on 3/4/2016.
 */
public class XMLParser {
    private String TAG= "MobileGrading";
    public List<QrCode> read(File inputFile) throws Exception{
        List<QrCode> qrCodes=null;
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList gList = doc.getElementsByTagName("Question");
            qrCodes = new ArrayList<QrCode>();
            for (int temp = 0; temp < gList.getLength(); temp++) {
                Node nNode = gList.item(temp);
                QrCode qrCode = new QrCode();
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    qrCode.setQUESTION(eElement.getElementsByTagName("QuestionName").item(0).getTextContent());
                    qrCode.setVALUES(getValues(eElement));
                    qrCodes.add(qrCode);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, "XML Parser read: exception");
        }
        return qrCodes;
    }

    private String getValues(Element eElement){
        NodeList kList = eElement.getElementsByTagName("tag");
        String values=null;
        for (int temp = 0; temp < kList.getLength(); temp++) {
            Node kNode = kList.item(temp);
            if (kNode.getNodeType() == Node.ELEMENT_NODE) {
                Element bElement = (Element) kNode;
                values=values+""+bElement.getElementsByTagName("TagName").item(0).getTextContent()+":";
                values=values+""+bElement.getElementsByTagName("TagWeight").item(0).getTextContent()+";";
            }
        }
        return values;
    }
}
