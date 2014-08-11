
package es.eucm.lostinspace.core.parsers;

import com.badlogic.gdx.Gdx;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.tools.xml.XMLNode;

import java.util.HashMap;
import java.util.Map;

public class Parser {

	public static String IDREF = "idref";

	public static final String[] EMPTY = new String[] {};
	public static final String[] BOOLEAN_TYPE = new String[] {"true", "false"};

	/** Maps relating tags and its required attributes */
	protected Map<String, String[]> requiredAtts;

	/** Maps relating tags and its required attributes */
	protected Map<String, String[]> impliedAtts;

	/** Valid values for the attributes */
	protected Map<String, String[]> attsValues;

	public Parser () {
		requiredAtts = new HashMap<String, String[]>();
		impliedAtts = new HashMap<String, String[]>();
		attsValues = new HashMap<String, String[]>();
	}

	/** Checks the required and implied attributes for the given node
	 * 
	 * @param n the node
	 * @param strict if all the attributes in the node must be required or implied
	 * @return if the attributes are valid */
	public boolean checkAttributes (XMLNode n, boolean strict) {
		String[] requiredAtt = requiredAtts.get(n.getNodeName());
		String[] impliedAtt = impliedAtts.get(n.getNodeName());
		for (String att : n.getAttributes().keySet()) {
			if (!checkAttributeValue(n, att)) {
				error(PhaseScreen.i18n("Invalid value for attribute " + att));
				if (strict) {
					return false;
				}
			}
		}
		return checkAttributes(n, requiredAtt == null ? EMPTY : requiredAtt, impliedAtt == null ? EMPTY : impliedAtt, strict);
	}

	/** @param node the node
	 * @param att the attribute name
	 * @return if Checks if the attribute has an admitted value */
	public boolean checkAttributeValue (XMLNode node, String att) {
		String[] validValues = attsValues.get(att);
		if (validValues == null) {
			return true;
		}
		return contains(getAttribute(node, att), validValues) >= 0;
	}

	/** Checks if the node has the correct attributes
	 * 
	 * @param n the node
	 * @param requiredAtt the required attributes for the tag
	 * @param impliedAtt the implied attributes for the tag
	 * @return if the node has the correct attributes */
	public boolean checkAttributes (XMLNode n, String[] requiredAtt, String[] impliedAtt) {
		return checkAttributes(n, requiredAtt, impliedAtt, true);
	}

	/** Checks if the node has the correct attributes
	 * 
	 * @param n the node
	 * @param requiredAtt the required attributes for the tag
	 * @param impliedAtt the implied attributes for the tag
	 * @param strict if all the attributes in the node must be required or implied
	 * @return if the node has the correct attributes */
	public boolean checkAttributes (XMLNode n, String[] requiredAtt, String[] impliedAtt, boolean strict) {
		boolean ok = true;
		int required = 0;
		Map<String, String> att = n.getAttributes();
		if (att != null) {
			for (Map.Entry<String, String> e : att.entrySet()) {
				if (contains(e.getKey(), requiredAtt) >= 0) {
					required++;
				} else if (strict && contains(e.getKey(), impliedAtt) == -1) {
					error(e.getKey() + PhaseScreen.i18n(" is not a valid attribute name for node ") + n.getNodeName());
					ok = false;
				}
			}

			if (required < requiredAtt.length) {
				ok = false;
				error(n.getNodeName() + PhaseScreen.i18n(" is missing some required attributes."));
			}
		}

		return ok;
	}

	/** Checks if the given string array contains the given string
	 * 
	 * @param s the string
	 * @param strings the array with strings
	 * @return the position of the string in the array; -1 of it's not contained by the array, -2 if the value is null */
	public int contains (String s, String[] strings) {
		if (s == null) {
			return -2;
		}
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].equals(s)) {
				return i;
			}
		}
		// error(s + PhaseScreen.i18n(" is not contained in ") + Arrays.toString(strings) );
		return -1;
	}

	/** Checks if the node has children
	 * 
	 * @param n the node
	 * @return true if the node has no children */
	public boolean checkNoChildren (XMLNode n) {
		if (n.getChildren().size() > 0) {
			error(n.getNodeName() + PhaseScreen.i18n(" node can't contain any child node."));
			return false;
		}
		return true;
	}

	/** @param node the node
	 * @param attName the attribute name
	 * @return Checks if the given attribute in the given node is required */
	private boolean isRequiredAttribute (XMLNode node, String attName) {
		if (this.requiredAtts.get(node.getNodeName()) != null && contains(attName, this.requiredAtts.get(node.getNodeName())) >= 0) {
			error(attName + PhaseScreen.i18n(" is a required attribute in node ") + node.getNodeName());
			return true;
		} else {
			return false;
		}
	}

	/** Returns an integer for an attribute, between a max and a min. If some exception happens, returns null. If the value is null
	 * and it's a required attribute, null is returned, min otherwise
	 * 
	 * @param node the node
	 * @param attName the attribute name
	 * @param min the minimum value for the integer
	 * @param max the maximum value for the integer
	 * @return the attribute value */
	public Integer getInteger (XMLNode node, String attName, int min, int max) {
		String value = getAttribute(node, attName);

		if (value == null) {
			return isRequiredAttribute(node, attName) ? null : min;
		}
		try {
			Integer i = Integer.parseInt(value);
			if (i < min || i > max) {
				error(PhaseScreen.i18n("Attribute ") + attName + PhaseScreen.i18n(" in ") + node.getNodeName()
					+ PhaseScreen.i18n(" nodes must be between ") + min + PhaseScreen.i18n(" and ") + max);
				return null;
			}
			return i;
		} catch (Exception e) {
			return null;
		}
	}

	/** Returns an integer for an attribute, between a max and a min. If some exception happens or the value is null, returns null
	 * 
	 * @param node the node nome
	 * @param attName the attribute name
	 * @param min the minimum value for the integer
	 * @param max the maximum value for the integer
	 * @return the attribute value */
	public Float getFloat (XMLNode node, String attName, float min, float max) {
		String value = getAttribute(node, attName);
		if (value == null) {
			return isRequiredAttribute(node, attName) ? null : min;
		}
		try {
			Float i = Float.parseFloat(value);
			if (i < min || i > max) {
				error(PhaseScreen.i18n("Attribute ") + attName + PhaseScreen.i18n(" in ") + node.getNodeName()
					+ PhaseScreen.i18n(" nodes must be between ") + min + PhaseScreen.i18n(" and ") + max);
				return null;
			}
			return i;
		} catch (Exception e) {
			return null;
		}
	}

	/** Returns a boolean for an attribute. If some exception happens or the value is null, null is returned. If the value is null,
	 * false is returned if it's not a required attrbitue, null otherwise
	 * 
	 * @param node the node name
	 * @param attName the attribute name
	 * @return the boolean value */
	public Boolean getBoolean (XMLNode node, String attName) {
		String value = getAttribute(node, attName);
		if (value == null) {
			return isRequiredAttribute(node, attName) ? null : false;
		}

		if (value.equals("true")) {
			return true;
		} else if (value.equals("false")) {
			return false;
		} else {
			error(attName + PhaseScreen.i18n(" attribute value in node ") + node.getNodeName()
				+ PhaseScreen.i18n(" should be 'true' or 'false'"));
			return null;
		}
	}

	/** Returns the value for the attribute
	 * 
	 * @param node the node
	 * @param attribute the attribute name
	 * @return the value of the attribute */
	public String getAttribute (XMLNode node, String attribute) {
		try {
			return node.getAttributeValue(attribute);
		} catch (Exception e) {
			return null;
		}
	}

	/** Reports an error
	 * 
	 * @param error the error message */
	public void error (String error) {
		Gdx.app.log("Parser", error);
	}
}
