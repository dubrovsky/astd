package com.isc.astd.repository;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author p.dzeviarylin
 */
public interface FileRepositoryCustom {
	<T> T searchFiles(Long rootCatalogId, Integer start, Integer limit, String sort, String filters, boolean isCount) throws JsonProcessingException;
}
