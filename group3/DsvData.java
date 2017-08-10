package group3;

import org.treez.javafxd3.d3.time.JsDate;
import org.treez.javafxd3.d3.wrapper.JavaScriptObject;

import org.treez.javafxd3.d3.core.JsEngine;
import org.treez.javafxd3.d3.core.JsObject;

public class DsvData extends JavaScriptObject {

	//#region ATTRIBUTES

	private final String symbol;

	private final int date;

	private final double price;
	
	//#end region

	//#region CONSTRUCTORS	
	
	public DsvData(JsEngine engine, JsObject wrappedJsObject) {
		super(engine, wrappedJsObject);		
		//JsObject jsDate = (JsObject) wrappedJsObject.getMember("date");
		//this.date = new JsDate(engine, jsDate);
		
		//this.date = (JsObject) wrappedJsObject.getMember("date");
		this.date = (int) wrappedJsObject.getMember("date");
		this.price =Double.parseDouble(wrappedJsObject.getMember("price").toString());
		this.symbol = (String) wrappedJsObject.getMember("symbol");
		
	}
	
	public DsvData(JsEngine engine, final String symbol, final int date, final double price) {
		super(engine);		
		this.date = date;
		this.price = price;
		this.symbol = symbol;
				
		String command = "new Object()";
		JsObject jsObject = (JsObject) engine.executeScript(command);		
		//JsObject jsDate = date.getJsObject();
		
		jsObject.setMember("date", date/*jsDate*/);
		jsObject.setMember("price", price);
		jsObject.setMember("symbol", symbol);
				
		setJsObject(jsObject);				
	}

	//#end region

	//#region METHODS

	/*@Override
	public String toString() {
		return "Data [date=" + date.getTime() + ", price=" + price + "]";
	}*/
	
	//#end region

	//#region ACCESSORS

	public String getSymbol() {
		return symbol;
	}

	public int getDate() {
		return date;
	}

	public double getPrice() {
		return price;
	}

	//#end region
}