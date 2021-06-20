package com.example.models

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.sqlclient.*
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.Transaction
import io.vertx.sqlclient.Tuple

class CustomerRepository(private val pool: PgPool) {
    private fun mapper(row: Row): Customer {
        return Customer(
            row.getInteger("id"),
            row.getString("firstName"),
            row.getString("lastName"),
            row.getString("email")
        )
    }

    suspend fun findById(id: Int): Customer? {
        val conn = pool.connection.await()
        try {
            val q = """select "id", "firstName", "lastName", "email" from customers where "id" = $id"""
            val res = conn
                .query(q)
                .execute()
                .await()
                .map { mapper(it) }
                .singleOrNull()

            return res
        } finally {
            conn.close().await()
        }
    }

    suspend fun findAll(): List<Customer> {
        val conn = pool.connection.await()
        try {
            return conn.query("""select "id", "firstName", "lastName", "email" from customers""")
                .execute()
                .await()
                .map { mapper(it) }

        } finally {
            conn.close().await()
        }
    }

    suspend fun createOrUpdate(customer: Customer): Result<Customer, CustomerError> {
        val conn = pool.connection.await()
        try {
            if (customer.id == null) {
                val q =
                    """insert into customers ("firstName", "lastName", "email") VALUES ($1, $2, $3) RETURNING id;"""
                val prep = pool.connection.await().prepare(q).await()
                val qq = prep.query().execute(Tuple.of(customer.firstName, customer.lastName, customer.email)).await()
                conn.close().await()
                return Ok(customer.copy(id = qq.iterator().next().getInteger("id")))
            } else {
                conn.close().await()
                return Err(
                    CustomerError(
                        CustomerErrorType.CUSTOMER_DOESNT_EXIST,
                        "No customer exists with id ${customer.id}"
                    )
                )
            }
        } finally {
            conn.close().await()
        }

//        return conn.transactionallyAwait {
//            val existsQ = """select 1 from customers where id = ${customer.id}"""
//            val exists = it
//                .(existsQ)
//                .executeAwait()
//                .rowCount() > 0
//            if (!exists) {
//
//                Err(
//                    CustomerError(
//                        CustomerErrorType.CUSTOMER_DOESNT_EXIST,
//                        "No customer exists with id ${customer.id}"
//                    )
//                )
//            } else {
//                val q =
//                    """update customers set "firstName" = ${customer.firstName}, "lastName" = ${customer.lastName}, email = ${customer.email} where id = ${customer.id};"""
//                it.preparedQuery(q).executeAwait()
//                Ok(customer)
//            }
//        }
    }
}

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
