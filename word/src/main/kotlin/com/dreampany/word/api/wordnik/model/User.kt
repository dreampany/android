/**
 * api.wordnik.com
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 4.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package com.dreampany.word.api.wordnik.model


/**
 *
 * @param displayName
 * @param email
 * @param faceBookId
 * @param id
 * @param password
 * @param status
 * @param userName
 * @param username
 */
data class User(
        val displayName: String? = null,
        val email: String? = null,
        val faceBookId: String? = null,
        val id: Long? = null,
        val password: String? = null,
        val status: Int? = null,
        val userName: String? = null,
        val username: String? = null
)

