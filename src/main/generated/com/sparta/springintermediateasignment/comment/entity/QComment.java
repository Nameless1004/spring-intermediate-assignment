package com.sparta.springintermediateasignment.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = -1953894207L;


    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final com.sparta.springintermediateasignment.common.QBaseTimeEntity _super = new com.sparta.springintermediateasignment.common.QBaseTimeEntity(this);

    public final StringPath contents = createString("contents");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final com.sparta.springintermediateasignment.schedule.entity.QSchedule schedule;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(Path<? extends Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComment(PathMetadata metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.schedule = inits.isInitialized("schedule") ? new com.sparta.springintermediateasignment.schedule.entity.QSchedule(forProperty("schedule"), inits.get("schedule")) : null;
    }

}

