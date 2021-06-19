package com.example.plugins

import com.example.models.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*


fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("/customer") {
            get {
                if (customerStorage.isNotEmpty()) {
                    call.respond(customerStorage.toList())
                } else {
                    call.respondText("No customers found", status = HttpStatusCode.NotFound)
                }
            }
            get("{id}") {
                val id = call.parameters["id"] ?: return@get call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )
                val customer =
                    customerStorage.find { it.id == id } ?: return@get call.respondText(
                        "No customer with id $id",
                        status = HttpStatusCode.NotFound
                    )
                call.respond(customer)
            }
            post {
                val customer = call.receive<Customer>()
                customerStorage.add(customer)
                call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
            }
            delete("{id}") {
                val id = call.parameters["id"] ?: return@delete call.respondText(
                    "Missing or malformed id",
                    status = HttpStatusCode.BadRequest
                )

                val removed = customerStorage.removeIf { it.id == id }
                if (!removed) {
                    call.respondText(
                        "No customer with id $id",
                        status = HttpStatusCode.NotFound
                    )
                } else {
                    call.respondText("Deleted", status = HttpStatusCode.Accepted)
                }
            }
        }
    }

}
