package com.example.models

import io.vertx.kotlin.pgclient.pgConnectOptionsOf
import io.vertx.kotlin.sqlclient.getConnectionAwait
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.pgclient.PgPool


var connectOptions = pgConnectOptionsOf(
    port = 5432,
    database = "vertx_test_db",
    user = "postgres"
)

// Pool options
var poolOptions = poolOptionsOf(maxSize = 5)

var client = PgPool.pool(
    connectOptions, poolOptions
)
