package com.sprata.i4u.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBasic is a Querydsl query type for Basic
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBasic extends EntityPathBase<Basic> {

    private static final long serialVersionUID = -1887636934L;

    public static final QBasic basic = new QBasic("basic");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> createdBy = createComparable("createdBy", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> deletedBy = createComparable("deletedBy", java.util.UUID.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> updatedBy = createComparable("updatedBy", java.util.UUID.class);

    public QBasic(String variable) {
        super(Basic.class, forVariable(variable));
    }

    public QBasic(Path<? extends Basic> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBasic(PathMetadata metadata) {
        super(Basic.class, metadata);
    }

}

