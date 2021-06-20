package com.example.plugins

import com.example.models.*
import com.github.michaelbull.result.mapBoth
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.serialization.*
import io.vertx.kotlin.sqlclient.getConnectionAwait
import io.vertx.sqlclient.SqlConnection

fun Application.module(){
    install(ContentNegotiation) {
        json()
    }
}
fun Application.configureRouting() {


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("/customer") {
            get {
                val repo = CustomerRepository(client)
                val customers = repo.findAll().toList()
                if (customers.isNotEmpty()) {
                    call.respond(customers)
                } else {
                    call.respondText("No customers found", status = HttpStatusCode.NotFound)
                }
            }
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )
                val repo = CustomerRepository(client)
                val customer =
                    repo.findById(id) ?: return@get call.respondText(
                        "No customer with id $id",
                        status = HttpStatusCode.NotFound
                    )
                call.respond(customer)
            }
            post {
                val req = call.receive<CustomerReq>()
                val repo = CustomerRepository(client)
                repo.createOrUpdate(Customer(null,req.firstName, req.lastName, req.email))
                    .mapBoth(
                        { cust -> call.respond(status = HttpStatusCode.Created, cust) },
                        { err -> call.respond(status = HttpStatusCode.BadRequest, err) })
            }
            post("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )
                val repo = CustomerRepository(client)
                val req = call.receive<CustomerReq>()
                repo.createOrUpdate(Customer(id,req.firstName, req.lastName, req.email))
                    .mapBoth(
                        { cust -> call.respond(status = HttpStatusCode.Accepted, cust) },
                        { err -> call.respond(status = HttpStatusCode.BadRequest, err) })
            }
//            delete("{id}") {
//                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText(
//                    "Missing or malformed id",
//                    status = HttpStatusCode.BadRequest
//                )
//
//                val removed = customerStorage.removeIf { it.id == id }
//                if (!removed) {
//                    call.respondText(
//                        "No customer with id $id",
//                        status = HttpStatusCode.NotFound
//                    )
//                } else {
//                    call.respondText("Deleted", status = HttpStatusCode.Accepted)
//                }
//            }
        }
    }

}
