package com.isc.astd.service.mapper;

import com.isc.astd.domain.*;
import com.isc.astd.service.dto.*;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

/**
 * @author p.dzeviarylin
 */
@Component
public class Mapper extends ConfigurableMapper {

    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(Catalog.class, CatalogDTO.class)
                .field("parentCatalog.id", "parentCatalogId")
                .field("name", "text")
                .field("name", "name")
                .fieldAToB("childCatalogs", "children")
                .fieldAToB("id", "id")
                .fieldAToB("id", "realId")
                .byDefault()
                .register();

        factory.classMap(File.class, FileDTO.class)
                .field("doc.id", "docId")
                .fieldAToB("route.id", "routeId")
                .field("nextSignPosition.id", "nextSignPositionId")
                .fieldBToA("file.contentType", "contentType")
                .fieldBToA("file.size", "size")
                .fieldBToA("file.originalFilename", "name")
                .fieldAToB("id", "id")
                .byDefault()
                .register();

        factory.classMap(File.class, FileBaseDTO.class)
                .field("doc.id", "docId")
                .field("nextSignPosition.id", "nextSignPositionId")
                .fieldAToB("route.id", "routeId")
                .fieldAToB("route.name", "routeName")
                .fieldAToB("route.position.id", "routePositionId")
                .fieldAToB("id", "id")
                .byDefault()
                .register();

        factory.classMap(File.class, FileCopyDTO.class)
                .field("doc.id", "docId")
                .fieldAToB("route.id", "routeId")
                .field("nextSignPosition.id", "nextSignPositionId")
                .fieldAToB("id", "id")
                .byDefault()
                .register();

        factory.classMap(Doc.class, DocDTO.class)
                .field("catalog.id", "catalogId")
                .field("rootCatalog.id", "rootCatalogId")
                .fieldAToB("id", "id")
                .byDefault()
                .register();

        factory.classMap(User.class, UserDTO.class)
                .field("position.name", "positionName")
                .field("position.id", "positionId")
                .field("rootCatalog.id", "rootCatalogId")
                .fieldAToB("id", "id")
                .byDefault()
                .register();

        factory.classMap(Audit.class, AuditDTO.class)
                .exclude("createdBy")
                .exclude("action")
                .byDefault()
                .register();
    }
}
