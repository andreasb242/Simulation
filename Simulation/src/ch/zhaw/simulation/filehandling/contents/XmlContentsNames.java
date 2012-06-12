package ch.zhaw.simulation.filehandling.contents;

public interface XmlContentsNames {
	public static String XML_ROOT = "simulation";
	
	public static String XML_MODEL = "model";

	public static String XML_MODEL_ATTRIBUTES = "attributes";
	
	public static String XML_MODEL_TYPE = "type";
	public static String XML_MODEL_TYPE_FLOW = "flow";
	public static String XML_MODEL_TYPE_XY = "xy";

	public static String XML_MODEL_XY_GRID = "grid";
	public static String XML_MODEL_XY_SHOW_GRID = "show-grid";
	public static String XML_MODEL_XY_DENSITY_VIEW_TYPE = "densityViewType";
	public static String XML_MODEL_XY_ZERO_X = "zerox";
	public static String XML_MODEL_XY_ZERO_Y = "zeroy";
	public static String XML_MODEL_XY_WIDTH = "width";
	public static String XML_MODEL_XY_HEIGHT = "height";

	public static String XML_MODEL_XY_ELEMENT_DENSITY = "density";
	
	public static String XML_ELEMENT_PARAMETER = "parameter";
	public static String XML_ELEMENT_DENSITY_CONTAINER = "density";
	public static String XML_ELEMENT_CONTAINER = "container";
	public static String XML_ELEMENT_GLOBAL = "global";

	public static String XML_ELEMENT_MESO = "meso";
	
	public static String XML_ELEMENT_CONNECTOR = "connector";
	public static String XML_ELEMENT_VALVE = "valve";
	public static String XML_ELEMENT_TARGET = "target";
	public static String XML_ELEMENT_SOURCE = "source";
	public static String XML_ELEMENT_INFINITE = "infinite";
	
	public static String XML_ELEMENT_HELPER_POINT = "helperpoint";
	public static String XML_ELEMENT_TEXT = "text";
	public static String XML_ELEMENT_FLOW_CONNECTOR = "flowConnector";

	public static String XML_ELEMENT_ATTRIB_X = "x";
	public static String XML_ELEMENT_ATTRIB_Y = "y";
	public static String XML_ELEMENT_ATTRIB_NAME = "name";
	public static String XML_ELEMENT_ATTRIB_VALUE = "value";
	public static String XML_ELEMENT_ATTRIB_TEXT = "text";
	public static String XML_ELEMENT_ATTRIB_LOG = "log";

	public static String XML_ELEMENT_ATTRIB_CONNECTOR = "connector";

	public static String XML_ELEMENT_ATTRIB_WIDTH = "width";
	public static String XML_ELEMENT_ATTRIB_HEIGHT = "height";

	public static String XML_ELEMENT_ATTRIB_TO = "to";
	public static String XML_ELEMENT_ATTRIB_FROM = "from";
	
	public static String XML_ELEMENT_ATTRIB_MESO_X = "directionx";
	public static String XML_ELEMENT_ATTRIB_MESO_Y = "directiony";
	public static String XML_ELEMENT_ATTRIB_MESO_DERIVATIVE = "derivative";
	
}
