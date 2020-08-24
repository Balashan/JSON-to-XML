package converter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Scanner;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class JSONtoXMLConverter {

	public static void main(String[] args) throws JSONException, DocumentException, IOException {

		// Input files
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the input Json File");
		String input = sc.nextLine();
		File jsonFile = new File(input);
		String json = new String(Files.readAllBytes(jsonFile.toPath()));

		// This method converts json object to xml string
		String xml = convert(json); 
		// Converting String to xml
		Document doc = DocumentHelper.parseText(xml);
		StringWriter sw = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xw = new XMLWriter(sw, format);
		xw.write(doc);
		String result = sw.toString();

		// Writing xml into file
		convertStringToDocument(result);

	}

	/*
	 * convertStringToDocument is used to write xml into a file
	 * 
	 */
	private static Document convertStringToDocument(String xmlStr) {
		try {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter the output xml File path");
			String output = sc.nextLine();
			FileWriter fw = new FileWriter(output);
			fw.write(xmlStr);
			fw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println("Success...");
		return null;
	}

	/*
	 * convert method is used to convert json string to xml string
	 */
	public static String convert(String json) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		String xml = JSONtoXMLConverter.toString(jsonObject);
		return xml;
	}

	public static String toString(Object object) throws JSONException {
		return toString(object, null);
	}

	public static String toString(Object object, String tagName) throws JSONException {
		StringBuffer sb = new StringBuffer();
		int i;
		JSONArray ja;
		JSONObject jo;
		String key;
		Iterator keys;
		int length;
		Integer integer = null;
		String string;
		Object value;
		boolean bln;
		Double dble = null;
		if (object instanceof JSONObject) {

			if (tagName != null) {
				sb.append('<');
				sb.append("Object ");
				sb.append("name =");
				sb.append(tagName);
				sb.append('>');
			} else {
				sb.append('<');
				sb.append("Object");
				sb.append('>');
			}

			jo = (JSONObject) object;
			keys = jo.keys();
			while (keys.hasNext()) {
				key = keys.next().toString();
				value = jo.opt(key);
				if (value == null) {
					value = "";
				}
				if (value instanceof String) {
					string = (String) value;
				} else if (value instanceof Integer) {
					integer = (Integer) value;
				} else if (value instanceof Boolean) {
					bln = (Boolean) value;
				}
				if (value instanceof JSONArray) {
					sb.append("<");
					sb.append("array name = ");
					sb.append("\"" + key + "\"");
					sb.append(">");
					ja = (JSONArray) value;
					length = ja.length();
					for (i = 0; i < length; i += 1) {
						value = ja.get(i);
						if (value instanceof JSONArray) {
							sb.append('<');
							sb.append("array");
							sb.append('>');
							sb.append(JSONtoXMLConverter.toString(value));
							sb.append("</");
							sb.append("array");
							sb.append('>');
						} else {
							sb.append(toString(value, null));
						}
					}
					sb.append("<");
					sb.append("/array");
					sb.append(">");
				} else if ("".equals(value)) {
					sb.append('<');
					sb.append("\"" + key + "\"");
					sb.append("/>");
				} else {
					sb.append(toString(value, "\"" + key + "\""));
				}
			}

			sb.append("</");
			sb.append("Object");
			sb.append('>');
			return sb.toString();

		} else {
			// Array 
			if (object instanceof JSONArray) {
				ja = (JSONArray) object;
				length = ja.length();
				for (i = 0; i < length; i += 1) {
					sb.append(toString(ja.opt(i), tagName));
				}
				return sb.toString();
			}
			// Integer datatype
			if (object instanceof Integer) {
				integer = (object == null) ? 0 : (int) object;
				return (tagName == null) ? "<" + "number >" + integer + "</" + "number" + ">"
				: (integer == 0) ? "<" + tagName + "/>"
						: "<" + "number name = " + tagName + ">" + integer + "</" + "number" + ">";
			}
			// Double datatype
			if(object instanceof Double) {
				dble = (object == null) ? 0 : (double) object;
				return (tagName == null) ? "<" + "number >" + dble + "</" + "number" + ">"
				: "<" + "number name = " + tagName + ">" + dble + "</" + "number" + ">";
			}
			// Boolean datatype
			if (object instanceof Boolean) {
				bln = (object == null) ? Boolean.FALSE : (boolean) object;
				return (tagName == null) ? "<" + "boolean >" + bln + "</" + "boolean" + ">"
				: "<" + "boolean name = " + tagName + ">" + bln + "</" + "boolean" + ">";
			} else {
				// String datatype
				if (object instanceof String) {
					string = object.toString();
					return (tagName == null) ? "<String>" + string + "</" + "String>"
							: "<" + "String name = " + tagName + ">" + string + "</" + "String" + ">";
				} else {
					return (tagName == null) ?  "<null/>" :  "<null name=" + tagName + "/>" ;
				}
			}
		}
	}
}