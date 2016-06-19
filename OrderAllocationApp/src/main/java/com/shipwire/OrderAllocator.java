package com.shipwire;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.shipwire.data.OrderDao;
import com.shipwire.model.Line;
import com.shipwire.model.Order;

/**
 * OrderAllocator class is responsible for processing order requests.
 * 
 * @author Andy.Paladino
 * @version 6/19/2016
 */
public class OrderAllocator {

	private OrderDao orderDao;
	private int streamId = 0;
	
	protected static final String NEW_LINE_STR = "\n";
	protected static final String SUCCESS_RESPONSE_STR = "";
	
	
	public OrderAllocator(){
		orderDao = new OrderDao();
	}
	
	
	public OrderDao getOrderDao() {
		return orderDao;
	}


	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}


	public int getStreamId() {
		return streamId;
	}


	public void setStreamId(int streamId) {
		this.streamId = streamId;
	}


	public String processOrderRequest(String orderJson) {
		
		String processingResult = SUCCESS_RESPONSE_STR;
		
		try{
			Order order = deserializeOrderRequest(orderJson);
			validateOrderRequest(order);
			
			order.setStreamId(++streamId);
			processingResult = placeOrder(order);
			
			
		}catch(IllegalArgumentException|JsonSyntaxException ie){
			// Bad Request
			return String.format("Invalid Request: %s)", ie.getMessage());
		}catch(Exception e){
			return String.format("Unexpected Error occurred. Error: %s", e.getMessage());
		}
		
		return processingResult;
	}

	private String placeOrder(Order order) {

		List<Line> productLines = order.getLines();
		
		for(Line line : productLines){
			String product = line.getProduct();
			int quantity = line.getQuantity();
			
			if(orderDao.hasProductInStock(product, quantity)){
				orderDao.allocateProduct(product, quantity);
			}else{
				orderDao.addToBacklog(product, quantity);
			}
						
		}
		
		orderDao.addOrderEvent(order);
		
		if(orderDao.isInventoryEmpty()){
			return printOrderStats();
		}
		
		return SUCCESS_RESPONSE_STR;
	}

	

	private String printOrderStats() {
		List<String> orderEvents = orderDao.getOrderEvents();
		StringBuilder output = new StringBuilder();
		
		for(String orderEvent : orderEvents){
			output.append(orderEvent)
				.append(NEW_LINE_STR);
		}
		
		return output.toString();
	}

	private void validateOrderRequest(Order order) {
		
		StringBuilder errorMsg = new StringBuilder();
		
		if(order.getHeader() == null)
			errorMsg.append("Missing Header.\n");
		
		if(order.getHeader().intValue() < 0)
			errorMsg.append("Invalid Header value.\n");
		
		if(order.getLines().isEmpty())
			errorMsg.append("Missing product lines.\n");
		
		List<Line> lines = order.getLines();
		
		for(Line productLine : lines){
			String product = productLine.getProduct();
			int quantity = productLine.getQuantity();
			
			if(product == null || product.isEmpty()){
				errorMsg.append("Invalid product line. Missing Product.\n");
				break;
			}
			
			if(quantity < 0 || quantity > 5){
				errorMsg.append("Invalid product line quantity.\n");
				break;
			}
		}
		
		assert errorMsg.length() == 0 : errorMsg;
			
	}

	private Order deserializeOrderRequest(String orderJson) {
		return new Gson().fromJson(orderJson, Order.class);
	}
	
	

}
