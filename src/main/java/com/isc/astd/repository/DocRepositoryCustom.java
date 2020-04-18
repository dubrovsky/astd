package com.isc.astd.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.isc.astd.service.dto.DocSearchDTO;
import com.isc.astd.service.dto.FilterDTO;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
public interface DocRepositoryCustom {
	<T> T findDocsWithRejectedFiles(Long positionId, Long rootCatalogId, Integer start, Integer limit, Sort sort, boolean isCount);

	<T> T findDocsWithApprovedFiles(Long rootCatalogId, Integer start, Integer limit, Sort sort, boolean isCount);

	<T> T findDocsWithFilesToSign(Long nextSignPositionId, Long rootCatalogId, Integer start, Integer limit, Sort sort, boolean isCount);

	<T> T findDocsWithFilesAssureToSign(Long nextSignPositionId, Long rootCatalogId, Integer start, Integer limit, Sort sort, boolean isCount);

	<T> T searchDocs(Long rootCatalogId, Integer start, Integer limit, String sort, String filters, boolean isCount) throws JsonProcessingException;
}
