package com.gk.aws.dynamoDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
/**
 * Author: Gaurav Karale
 * Topic : Amazon DynamoDB
 * Desc: Sample java code for DynamoDB table creation and data read operation using AWS SDK 
 * low level API and DynamoDBMapper
 * */
public class AWSDynamoDBDemo {

	static DynamoDBMapper mapper;
	public static void main(String[] args) {

		AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
		client.setRegion(Region.getRegion(Regions.US_WEST_2));

		ArrayList<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("ID").withAttributeType("N"));
		        
		CreateTableRequest request = new CreateTableRequest()
		        .withTableName("Employee")
		        .withKeySchema(new KeySchemaElement().withAttributeName("ID").withKeyType(KeyType.HASH))
		        .withAttributeDefinitions(attributeDefinitions)
		        .withProvisionedThroughput(new ProvisionedThroughput()
		            .withReadCapacityUnits(1L)
		            .withWriteCapacityUnits(1L));

		TableUtils.createTableIfNotExists(client, request);
		
		DescribeTableRequest descTableRequest = new DescribeTableRequest().withTableName("Employee");
		
		TableDescription descTable= client.describeTable(descTableRequest).getTable();
		System.out.println("Table : "+descTable);
		
		createEmployee(client);
		mapper=new DynamoDBMapper(client);
		getEmployeeDetails(mapper);
	}

	/**
	 * Method creates insert data in employee table
	 * @param client
	 * */
	public static void createEmployee(AmazonDynamoDBClient client){
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("ID", new AttributeValue().withN("1"));
		item.put("name", new AttributeValue().withS("XYZ"));
		item.put("department", new AttributeValue().withS("IT"));
		item.put("title", new AttributeValue().withS("Software Engineer"));
		client.putItem("Employee", item);
	}
	
	/**
	 * Method retrieves employee data from table using
	 * DynamoDBQueryExpression
	 * @param mapper
	 * */
	public static Employee getEmployeeDetails(DynamoDBMapper mapper){
		Employee emp1= new Employee();
		emp1.setID(1);
		DynamoDBQueryExpression<Employee> queryExpression= new DynamoDBQueryExpression<Employee>().withHashKeyValues(emp1);
		
		List<Employee> itemList = mapper.query(Employee.class, queryExpression);
		for (int i = 0; i < itemList.size(); i++) {
			emp1=itemList.get(i);
			System.out.println("Employee ID : "+emp1.getID());
			System.out.println("Employee Department : "+emp1.getDepartment());
			System.out.println("Employee Title : "+emp1.getName());
			System.out.println("Employee Name : "+emp1.getName());
		}
		return emp1;
	}
}
