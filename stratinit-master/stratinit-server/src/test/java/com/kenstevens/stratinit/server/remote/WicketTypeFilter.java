package com.kenstevens.stratinit.server.remote;

import java.io.IOException;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

public class WicketTypeFilter implements TypeFilter {

	@Override
	public boolean match(MetadataReader metadataReader,
			MetadataReaderFactory metadataReaderFactory) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

}
