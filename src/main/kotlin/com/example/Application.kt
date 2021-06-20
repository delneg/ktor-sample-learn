package com.example

import com.example.models.client
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.vertx.kotlin.sqlclient.executeAwait
import io.vertx.kotlin.sqlclient.getConnectionAwait
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val c = client.getConnectionAwait();
        c.query(
            """create table if not exists customers
                (
                id          serial not null
                    constraint customers_pk
                    primary key,
                "firstName" text   not null,
                "lastName"  text   not null,
                email       text   not null
            ) ;
        """
        ).executeAwait()
        c.close()
    }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
        configureRouting()
    }.start(wait = true)
}
