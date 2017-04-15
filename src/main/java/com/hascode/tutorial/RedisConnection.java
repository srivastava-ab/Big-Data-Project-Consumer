package com.hascode.tutorial;

import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnection {
	private Jedis jedis;
	private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost", 6377);

	public Jedis getRedisConnection() {
		// Jedis jedis = null;
		try {
			jedis = pool.getResource();

		} finally {

		}
		return jedis;
	}
	
	public JSONObject getObjectFromRedis(String id){
		JSONObject parentJson = new JSONObject();
		
		JSONObject returnJSON = (JSONObject) index_test(id, parentJson);
		return returnJSON;
		
	}
	
	
	

	public Object index_test(String id, Object object) {

		// Jedis jedis = getRedisConnection();
		Map<String, String> jsonReconstruct = null;
		Set jsonList = null;
		JSONObject parentJson = null;
		JSONArray parentArray = null;

		if (object instanceof JSONArray) {
			jsonList = jedis.smembers(id);
			parentArray = (JSONArray) object;

			for (Object entry : jsonList) {
				// System.out.println(stock);

				if (jedis.type(entry.toString()).equals("hash")) {
					System.out.println("id at this stage" + id);
					System.out.println("Jedis object type=" + jedis.type(entry.toString()));
					JSONObject childJson = new JSONObject();
					parentArray.add(index_test(entry.toString(), childJson));

					System.out.println("object");
				} else if (jedis.type(entry.toString()).equals("set")) {
					System.out.println("id at this stage" + id);
					System.out.println("Jedis object type=" + jedis.type(entry.toString()));

					JSONArray jsonArray = new JSONArray();
					parentArray.add(index_test(entry.toString(), jsonArray));

					System.out.println("Array");
				} else {
					System.out.println("id at this stage" + id);
					parentArray.add(entry.toString());
				}

			}
			return parentArray;

		} else if (object instanceof JSONObject) {
			jsonReconstruct = jedis.hgetAll(id);
			parentJson = (JSONObject) object;

			for (Map.Entry<String, String> entry : jsonReconstruct.entrySet()) {

				// if (entry.getKey().equals("_id") &&
				// entry.getValue().equals(parentID)) {
				if (entry.getKey().equals("id")) {
					System.out.println("id at this stage" + id);
					parentJson.put(entry.getKey(), entry.getValue());
				} else {
					System.out.println("id at this stage" + id);
					System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());

					if (jedis.type(entry.getValue().toString()).equals("hash")) {
						System.out.println("Jedis object type=" + jedis.type(entry.getValue().toString()));
						JSONObject childJson = new JSONObject();

						parentJson.put(entry.getKey(), index_test(entry.getValue(), childJson));
						System.out.println("object");
					} else if (jedis.type(entry.getValue().toString()).equals("set")) {
						System.out.println("id at this stage" + id);
						System.out.println("Jedis object type=" + jedis.type(entry.getValue().toString()));

						// parentJson
						JSONArray jsonArray = new JSONArray();
						parentJson.put(entry.getKey(), index_test(entry.getValue(), jsonArray));
						// JSONObject childJson = new JSONObject();
						// childJson=reconstructJson(entry.getValue(),
						// childJson);
						// jsonArray.add(childJson);
						// parentJson.put(entry.getKey(),jsonArray );

						System.out.println("Array");
					} else {
						System.out.println("id at this stage" + id);
						parentJson.put(entry.getKey(), entry.getValue());
					}

					// jsonObject.put(entry.getKey(), entry.getValue());
				}

			}
			return parentJson;

		} else {

		}
		return parentJson;

		// jsonObject.putAll(jsonReconstruct);

	}

	
	
}
