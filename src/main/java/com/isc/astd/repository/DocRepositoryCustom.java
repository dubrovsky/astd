package com.isc.astd.repository;

import com.isc.astd.service.dto.MoreRejectedDTO;
import com.isc.astd.service.dto.MoreSignsDTO;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
public interface DocRepositoryCustom {
	List<MoreSignsDTO> findDocsWithFilesToSign(long nextSignPositionId, String userId, Long rootCatalogId);

	List<MoreRejectedDTO> findDocsWithRejectedFiles(long nextSignPositionId, String userId, Long rootCatalogId);
}
