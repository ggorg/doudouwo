package com.gen.framework.common.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
	protected Object determineCurrentLookupKey() {
	      return DatabaseContextHolder.getDatabaseType();
	}
}
