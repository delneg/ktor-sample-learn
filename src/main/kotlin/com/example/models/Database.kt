package com.example.models

import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.pgclient.pgConnectOptionsOf
import io.vertx.kotlin.sqlclient.getConnectionAwait
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.Transaction


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

suspend fun <T> PgPool.withConnection(applier: suspend (SqlConnection) -> T): T {
    var connection: SqlConnection? = null
    try {
        connection = this.connection.await()
        val result = applier(connection)
        connection.close().await()
        return result
    } finally {
        connection?.close()?.await()
    }
}

suspend fun <T> SqlConnection.transactionallyAwait(applier: suspend (Transaction) -> T): T {
    var transaction: Transaction? = null
    try {
        transaction = this.begin().await()
        val result = applier(transaction)
        transaction.commit()
        return result
    }
    finally {
        transaction?.completion()?.await()
    }
}