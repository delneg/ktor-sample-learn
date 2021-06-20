package com.example.models

import kotlinx.serialization.Serializable

val customerStorage = mutableListOf<Customer>()

enum class CustomerErrorType {
    CUSTOMER_DOESNT_EXIST
}

@Serializable
class CustomerError(val error: CustomerErrorType, val message: String?)

@Serializable
data class Customer(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val email: String
)


@Serializable
data class CustomerReq(
    val firstName: String,
    val lastName: String,
    val email: String
)

