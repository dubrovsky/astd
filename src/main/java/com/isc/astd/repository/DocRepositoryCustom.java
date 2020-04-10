package com.isc.astd.repository;

import org.springframework.data.domain.Sort;

/**
 * @author p.dzeviarylin
 */
public interface DocRepositoryCustom {
	<T> T findDocsWithRejectedFiles(Long positionId, Long rootCatalogId, int start, int limit, Sort sort, boolean isCount);

	<T> T findDocsWithApprovedFiles(Long rootCatalogId, Integer start, Integer limit, Sort sort, boolean isCount);

	<T> T findDocsWithFilesToSign(Long nextSignPositionId, Long rootCatalogId, Integer start, Integer limit, Sort sort, boolean isCount);

	<T> T findDocsWithFilesAssureToSign(Long nextSignPositionId, Long rootCatalogId, Integer start, Integer limit, Sort sort, boolean isCount);
}
