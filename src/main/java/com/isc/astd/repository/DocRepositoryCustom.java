package com.isc.astd.repository;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author p.dzeviarylin
 */
public interface DocRepositoryCustom {
	<T> T findDocsWithRejectedFiles(Long positionId, Long rootCatalogId, Integer start, Integer limit, String sort, boolean isCount) throws JsonProcessingException;

	<T> T findDocsWithApprovedFiles(Long rootCatalogId, Integer start, Integer limit, String sort, boolean isCount) throws JsonProcessingException;

	<T> T findDocsWithFilesToSign(Long nextSignPositionId, Long rootCatalogId, Integer start, Integer limit, String sort, boolean isCount) throws JsonProcessingException;

	<T> T findDocsWithFilesAssureToSign(Long nextSignPositionId, Long rootCatalogId, Integer start, Integer limit, String sort, boolean isCount) throws JsonProcessingException;

	<T> T searchDocs(Long rootCatalogId, Integer start, Integer limit, String sort, String filters, boolean isCount) throws JsonProcessingException;
}
