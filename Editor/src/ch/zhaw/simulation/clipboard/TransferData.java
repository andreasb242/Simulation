package ch.zhaw.simulation.clipboard;

import java.awt.Point;
import java.util.HashMap;

/**
 * This is a single object copied to the clipboard
 * 
 * @author Andreas Butti
 */
public class TransferData {
	private int x;
	private int y;
	private Type type;
	private int id;
	private String name;
	private String formula;
	private int source;
	private int target;
	private Point point;

	private HashMap<String, Object> additionalData = new HashMap<String, Object>();

	public enum Type {
		InfiniteSymbol, Container, MesoCompartment, Parameter, Global, Text, Flow, Connector
	};

	public TransferData(int id, int x, int y, Type type, String name, String formula, int source, int target, Point point) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.type = type;
		this.name = name;
		this.formula = formula;

		this.source = source;
		this.target = target;

		this.point = point;
	}

	public HashMap<String, Object> getAdditionalData() {
		return additionalData;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Type getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getFormula() {
		return formula;
	}

	public int getSource() {
		return source;
	}

	public int getTarget() {
		return target;
	}

	public Point getPoint() {
		return point;
	}
}
