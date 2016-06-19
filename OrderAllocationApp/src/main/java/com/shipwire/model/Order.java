package com.shipwire.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Object representing a given Order stream request. 
 * 
 * @author Andy.Paladino
 * @version 6/19/2016
 */
public class Order {

	@SerializedName("Header")	
	private Integer header;
	
	@Expose(serialize = false, deserialize = false)
	private int streamId;

	@SerializedName("Lines")
	private List<Line> lines;
	
	//"{\"Header\": 1, \"Lines\": {\"Product\": \"A\", \"Quantity\": \"1\"},{\"Product\": \"C\", \"Quantity\": \"4\"}}";
	
	public Order(Integer headerId, List<Line> l){
		this.header = headerId;
		this.lines = l;
	}
	
	public int getStreamId() {
		return streamId;
	}

	public void setStreamId(int streamId) {
		this.streamId = streamId;
	}

	public Integer getHeader() {
		return header;
	}

	public void setHeader(Integer header) {
		this.header = header;
	}

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}
	
}
