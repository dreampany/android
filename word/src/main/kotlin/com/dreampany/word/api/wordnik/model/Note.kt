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
 * @param appliesTo
 * @param noteType
 * @param pos
 * @param value
 */
data class Note(
        val appliesTo: Array<String>? = null,
        val noteType: String? = null,
        val pos: Int? = null,
        val value: String? = null
)

