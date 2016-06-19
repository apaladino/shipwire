package com.shipwire.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.shipwire.model.Line;
import com.shipwire.model.Order;

/**
 * Data access object for maintaining order data store.
 * 
 * @author Andy.Paladino
 * @version 6/19/2016
 */
public class OrderDao {

	private Map<String,Integer> inventory;
	private Map<String,Integer> allocated;
	private Map<String,Integer> backOrder;
	private List<String> orderEvents;
	
	private final String[] products = { "A", "B", "C", "D", "E"};
	
	public OrderDao(){
		inventory = new HashMap<>();
		allocated = new HashMap<>();
		backOrder = new HashMap<>();
		orderEvents = new LinkedList<>();
		
		populateInitialInventory();
		populateInitialAllocated();
		populateInitialBackOrder();
	}
	
	private void populateInitialBackOrder() {
		for(String product : products){
			this.backOrder.put(product, 0);
		}
	}

	// HELPER METHODS
	/**
	 * Method to add an order event. Used to track order requests.
	 * 
	 * @param order
	 */
	public void addOrderEvent(Order order) {
		StringBuilder event = new StringBuilder();
		String outputDelim = ":";
		
		event.append(order.getHeader())
			.append(outputDelim)
			.append(getOrderProducts(order))
			.append(outputDelim)
			.append(outputDelim)
			.append(printMapValues(allocated))
			.append(outputDelim)
			.append(outputDelim)
			.append(printMapValues(backOrder));
		
		this.orderEvents.add(event.toString());
	}
	
	/**
	 * Builds a comma separates list of product quantities defined in order. 
	 * 
	 * @param order
	 * @return
	 */
	protected String getOrderProducts(Order order) {
		
		Map<String, Integer> productMap = initProductMap();
		
		List<Line> productLines = order.getLines();
		
		for(Line productLine : productLines){
			productMap.put(productLine.getProduct(), productLine.getQuantity());
		}
		
		StringBuilder productData = printMapValues(productMap);
		
		return productData.toString();
	}

	protected StringBuilder printMapValues(Map<String, Integer> productMap) {
		StringBuilder productData = new StringBuilder();
		
		for(String product : this.products){
			if(productData.length() > 0){
				productData.append(",");
			}
			
			productData.append(productMap.get(product));
		}
		return productData;
	}

	protected Map<String, Integer> initProductMap() {
		Map<String,Integer> productMap =  new HashMap<String,Integer>();
		
		for(String product : products){
			productMap.put(product, 0);
		}
		
		return productMap;
	}

	private void populateInitialAllocated() {
		for(String product : products){
			allocated.put(product, 0);
		}
	}

	public boolean hasProductInStock(String product, int newQuantity) {
		
		Integer existingQuantity = getInventoryQuantityForProduct(product);
		
		return (existingQuantity != null && existingQuantity.intValue() >= newQuantity);
	}
	
	public int getInventoryQuantityForProduct(String product){
		return inventory.get(product);
	}
	
	public void allocateProduct(String product, int newQuantity) {
		// update inventory
		Integer existingQuantity = inventory.get(product);
		Integer adjustedQuantity = existingQuantity.intValue() - newQuantity;
		inventory.put(product, adjustedQuantity);
		
		// update allocated 
		existingQuantity = this.allocated.get(product);
		adjustedQuantity = existingQuantity.intValue() + newQuantity;
		allocated.put(product, adjustedQuantity);
		
	}
	
	public void addToBacklog(String product, int newQuantity) {
		Integer existingQuantity = this.backOrder.get(product);
		Integer adjustedQuanity = existingQuantity.intValue() + newQuantity;
		backOrder.put(product, adjustedQuanity);
	}
	
	private void populateInitialInventory() {
		inventory.put("A", 150);
		inventory.put("B", 150);
		inventory.put("C", 100);
		inventory.put("D", 100);
		inventory.put("E", 200);
	}
	
	public boolean isInventoryEmpty() {
		
		for(String product : products){
			Integer existingQuantity = inventory.get(product);
			if(existingQuantity.intValue() > 0)
				return false;
		}
		
		return true;
	}

	public int getAllocatedQuantityForProduct(String product) {
		return this.allocated.get(product);
	}
	
	public int getBackOrderedQuantityForProduct(String product) {
		return this.backOrder.get(product);
	}
	
	// GETTERS/SETTERS
	public Map<String, Integer> getInventory() {
		return inventory;
	}
	public void setInventory(Map<String, Integer> inventory) {
		this.inventory = inventory;
	}
	public Map<String, Integer> getAllocated() {
		return allocated;
	}
	public void setAllocated(Map<String, Integer> allocated) {
		this.allocated = allocated;
	}
	public Map<String, Integer> getBackOrder() {
		return backOrder;
	}
	public void setBackOrder(Map<String, Integer> backOrder) {
		this.backOrder = backOrder;
	}
	public List<String> getOrderEvents() {
		return orderEvents;
	}
	public void setOrderEvents(List<String> orderEvents) {
		this.orderEvents = orderEvents;
	}

}
