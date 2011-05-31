package com.widen.valet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTest
{
	private final Logger log = LoggerFactory.getLogger(IntegrationTest.class);

	private Route53Driver driver;

	public static void main(String[] args)
	{
		new IntegrationTest().run();
	}

	private void run()
	{
		setupDriver();

		final Zone zone = createZone(String.format("valet-test-zone-%s.net.", System.currentTimeMillis()));

		addResources(zone);

		addRoundRobinResources(zone);

		deleteZone(zone);
	}

	private void addRoundRobinResources(Zone zone)
	{
		List<ZoneUpdateAction> actions = new ArrayList<ZoneUpdateAction>();

		actions.add(ZoneUpdateAction.createRoundRobinAction("wwwrw." + zone.getName(), RecordType.A, 600, "set1", 1, "10.0.0.1"));

		actions.add(ZoneUpdateAction.createRoundRobinAction("wwwrw." + zone.getName(), RecordType.A, 600, "set2", 2, "10.0.0.2"));

		actions.add(ZoneUpdateAction.createRoundRobinAction("wwwrw." + zone.getName(), RecordType.A, 600, "set3", 0, "10.0.0.3"));

		final ZoneChangeStatus status = driver.updateZone(zone, "add rr resources", actions);

		driver.waitForSync(status);
	}

	private void addResources(Zone zone)
	{
		List<ZoneUpdateAction> actions = new ArrayList<ZoneUpdateAction>();

		actions.add(ZoneUpdateAction.createAction("www." + zone.getName(), RecordType.A, 600, "127.0.0.1"));

		actions.add(ZoneUpdateAction.createAction(zone.getName(), RecordType.MX, 600, "10 mail10.example.com", "20 mail20.example.com", "30 mail30.example.com"));

		final ZoneChangeStatus status = driver.updateZone(zone, "add resources", actions);

		driver.waitForSync(status);
	}

	private Zone createZone(String domain)
	{
		final ZoneChangeStatus status = driver.createZone(domain, "Valet integration test on " + new Date());

		driver.waitForSync(status);

		return driver.zoneDetails(status.getZoneId());
	}

	private void deleteZone(Zone zone)
	{
		List<RecordType> keepTypes = Arrays.asList(RecordType.SOA, RecordType.NS);

		final List<ZoneResource> resources = driver.listZoneRecords(zone);

		List<ZoneUpdateAction> deleteActions = new ArrayList<ZoneUpdateAction>();

		for (ZoneResource resource : resources)
		{
			if (!keepTypes.contains(resource.getRecordType()))
			{
				deleteActions.add(ZoneUpdateAction.deleteAction(resource));
			}
		}

		ZoneChangeStatus status = driver.updateZone(zone, "Delete all resources for zone deletion", deleteActions);

		driver.waitForSync(status);

		status = driver.deleteZone(zone, "Delete integration test zone");

		driver.waitForSync(status);
	}

	private void setupDriver()
	{
		final Properties properties = new Properties();

		try
		{
			properties.load(getClass().getResourceAsStream("IntegrationTest.properties"));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		driver = new Route53Driver(properties.getProperty("aws-access-key"), properties.getProperty("aws-secret-key"));
	}

}
