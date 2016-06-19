package com.shipwire.model;

import com.google.gson.annotations.SerializedName;

/**
 * Object representing a product line.
 * @author Andy.Paladino
 *
 */
public class Line {

	@SerializedName("Product")
	private String product;
	
	@SerializedName("Quantity")
	private int quantity;
	
	public Line(String p, int q){
		this.product = p;
		this.quantity = q;
	}
	
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
}
