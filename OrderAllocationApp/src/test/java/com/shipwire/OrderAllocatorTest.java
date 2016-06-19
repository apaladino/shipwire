package com.shipwire;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.shipwire.data.OrderDao;

/* TODO:  Add more tests and test cases */
public class OrderAllocatorTest {

	private OrderAllocator allocator;
	private OrderDao orderDao;
	
	@Before
	public void setUp(){
		orderDao = new OrderDao();
		allocator = new OrderAllocator();
		allocator.setOrderDao(orderDao);
	}
	
	@Test
	public void testValidOrder(){
		String validOrderJson = "{\"Header\": 1, \"Lines\": [{\"Product\": \"A\", \"Quantity\": \"1\"},{\"Product\": \"C\", \"Quantity\": \"4\"}]}";
		
		int initialStreamId = allocator.getStreamId();
		
		String output = allocator.processOrderRequest(validOrderJson);
		assertEquals("Should return empty string for success case", allocator.SUCCESS_RESPONSE_STR, output);
		
		// validate streamid
		assertEquals("Should have incremented the streamId", (initialStreamId + 1), allocator.getStreamId());
		
		// validate product A
		assertEquals("Should have updated inventory for product: A", (150-1), orderDao.getInventoryQuantityForProduct("A"));
		assertEquals("Should have allocated inventory for product: A", 1, orderDao.getAllocatedQuantityForProduct("A"));
		assertEquals("Should not have backOrdered for product: A", 0, orderDao.getBackOrderedQuantityForProduct("A"));
		

		// validate product C
		assertEquals("Should have updated inventory for product: C", (100-4), orderDao.getInventoryQuantityForProduct("C"));
		assertEquals("Should have allocated inventory for product: C", 4, orderDao.getAllocatedQuantityForProduct("C"));
		assertEquals("Should not have backOrdered for product: C", 0, orderDao.getBackOrderedQuantityForProduct("C"));
	
		// validate order event generated
		List<String> events = orderDao.getOrderEvents();
		assertEquals("Should have generated 1 order event", 1, events.size());
		assertEquals("Invalid event format", "1:1,0,4,0,0::1,0,4,0,0::0,0,0,0,0", events.get(0));
	}
	
	@Test
	public void testValidOrderStreamWithExhaustedInventory(){
		
		Map<String,Integer> inv = genMockInventory();
		orderDao.setInventory(inv);
		
		String[] inputs = new String[] {
				"{\"Header\": 1, \"Lines\": [{\"Product\": \"A\", \"Quantity\": \"1\"},{\"Product\": \"C\", \"Quantity\": \"1\"}]}",
				"{\"Header\": 2, \"Lines\": [{\"Product\": \"E\", \"Quantity\": \"5\"}]}",
				"{\"Header\": 3, \"Lines\": [{\"Product\": \"D\", \"Quantity\": \"4\"}]}",
				"{\"Header\": 4, \"Lines\": [{\"Product\": \"A\", \"Quantity\": \"1\"},{\"Product\": \"C\", \"Quantity\": \"1\"}]}",
				"{\"Header\": 5, \"Lines\": [{\"Product\": \"B\", \"Quantity\": \"3\"}]}",
				"{\"Header\": 6, \"Lines\": [{\"Product\": \"D\", \"Quantity\": \"4\"}]}"
		};
		
		for(int i=0; i < inputs.length; i++){
			String input = inputs[i];
			String result = allocator.processOrderRequest(input);
			
			if(i < 4)
				assertEquals("Should return empty string for success case: " + i, "", result);
			else
				assertTrue("Invalid stats printed:",
						result.startsWith("1:1,0,1,0,0::"));
		}
	}
	
	@Test
	public void testInvalidOrder(){
		// TODO: Implement
	}

	private Map<String, Integer> genMockInventory() {
		Map<String,Integer> inv = new HashMap<>();
		
		inv.put("A", 2);
		inv.put("B", 3);
		inv.put("C", 1);
		inv.put("D", 0);
		inv.put("E", 0);
		return inv;
	}
}
