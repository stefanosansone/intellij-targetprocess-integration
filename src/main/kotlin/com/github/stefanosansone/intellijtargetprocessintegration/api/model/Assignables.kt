package com.github.stefanosansone.intellijtargetprocessintegration.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Assignables(
    val items: List<Item>
){
    @Serializable
    data class Item(
        val assignedUser: AssignedUser,
        val effort: Double,
        val entityState: EntityState,
        val id: Int,
        val name: String,
        val resourceType: String,
        val description: String? = null,
        val tags: String
    ){
        @Serializable
        data class AssignedUser(
            val items: List<Item>
        ){
            @Serializable
            data class Item(
                val firstName: String,
                val fullName: String,
                val id: Int,
                val kind: String,
                val lastName: String,
                val login: String,
                val resourceType: String
            )
        }

        @Serializable
        data class EntityState(
            val id: Int,
            val name: String,
            val numericPriority: Double,
            val resourceType: String
        )
    }
}