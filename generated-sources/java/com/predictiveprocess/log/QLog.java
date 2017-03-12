package com.predictiveprocess.log;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QLog is a Querydsl query type for Log
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLog extends EntityPathBase<Log> {

    private static final long serialVersionUID = -621964401L;

    public static final QLog log = new QLog("log");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath path = createString("path");

    public QLog(String variable) {
        super(Log.class, forVariable(variable));
    }

    public QLog(Path<? extends Log> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLog(PathMetadata<?> metadata) {
        super(Log.class, metadata);
    }

}

