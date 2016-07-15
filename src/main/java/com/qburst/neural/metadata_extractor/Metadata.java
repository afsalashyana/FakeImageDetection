package com.qburst.neural.metadata_extractor;


import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.util.Iterator;

public class Metadata {
    String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MetaData>";

    public String readAndDisplayMetadata(String fileName) {
        try {

            File file = new File(fileName);
            ImageInputStream iis = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {

                // pick the first available ImageReader
                ImageReader reader = readers.next();

                // attach source to the reader
                reader.setInput(iis, true);

                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);

                String[] names = metadata.getMetadataFormatNames();
                int length = names.length;
                for (int i = 0; i < length; i++) {
                    System.out.println("Format name: " + names[i]);
//                    data+="Format name: " + names[i] + "\n";
                    displayMetadata(metadata.getAsTree(names[i]));
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return data + "</MetaData>";
    }

    void displayMetadata(Node root) {
        displayMetadata(root, 0);
    }

    void indent(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("    ");
            data += "    ";
        }
    }

    void displayMetadata(Node node, int level) {
        // print open tag of element
        indent(level);
        System.out.print("<" + node.getNodeName());
        data += "<" + node.getNodeName();

        NamedNodeMap map = node.getAttributes();
        if (map != null) {

            // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                System.out.print(" " + attr.getNodeName()
                        + "=\"" + attr.getNodeValue() + "\"");
                data += " " + attr.getNodeName()
                        + "=\"" + attr.getNodeValue() + "\"";
            }
        }

        Node child = node.getFirstChild();
        if (child == null) {
            // no children, so close element and return
            System.out.println("/>");
            data += "/>" + "\n";
            return;
        }

        // children, so close current tag
        System.out.println(">");
        data += ">" + "\n";
        while (child != null) {
            // print children recursively
            displayMetadata(child, level + 1);
            child = child.getNextSibling();
        }

        // print close tag of element
        indent(level);
        data += "</" + node.getNodeName() + ">" + "\n";
        System.out.println("</" + node.getNodeName() + ">");
    }
}
