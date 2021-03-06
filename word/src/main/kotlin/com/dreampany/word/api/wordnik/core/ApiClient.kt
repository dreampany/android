package com.dreampany.word.api.wordnik.core

import com.dreampany.framework.util.Json
import okhttp3.*
import timber.log.Timber
import java.io.File

/**
 * Created by roman on 2019-06-08
 * Copyright (c) 2019 bjit. All rights reserved.
 * hawladar.roman@bjitgroup.com
 * Last modified $file.lastModified
 */
open class ApiClient(val baseUrl: String) {

    companion object {
        @JvmStatic
        val ApiKey = "api_key"
        val ContentType = "Content-Type"
        val Accept = "Accept"
        val JsonMediaType = "application/json"
        val FormDataMediaType = "multipart/form-data"
        val XmlMediaType = "application/xml"

        @JvmStatic
        val client: OkHttpClient = OkHttpClient()

        @JvmStatic
        var defaultHeaders: Map<String, String> by ApplicationDelegates.setOnce(
            mapOf(
                ContentType to JsonMediaType,
                Accept to JsonMediaType
            )
        )

        @JvmStatic
        val jsonHeaders: Map<String, String> =
            mapOf(ContentType to JsonMediaType, Accept to JsonMediaType)
    }

    var keyOfApi: String? = null

/*    fun addHeader(key:String, value:String) {
        jsonHeaders.plus(key to value)
    }*/

    inline protected fun <reified T> requestBody(
        content: T,
        mediaType: String = JsonMediaType
    ): RequestBody {
        when {
            content is File -> return RequestBody.create(
                MediaType.parse(mediaType), content
            )
            mediaType == FormDataMediaType -> {
                var builder = FormBody.Builder()
                // content's type *must* be Map<String, Any>
                @Suppress("UNCHECKED_CAST")
                (content as Map<String, String>).forEach { key, value ->
                    builder = builder.add(key, value)
                }
                return builder.build()
            }
            mediaType == JsonMediaType -> return RequestBody.create(
                MediaType.parse(mediaType), Serializer.moshi.adapter(T::class.java).toJson(content)
            )
            mediaType == XmlMediaType -> TODO("xml not currently supported.")
        }

        // TODO: this should be extended with other serializers

        // TODO: this should be extended with other serializers
        TODO("requestBody currently only supports JSON body and File body.")
    }

    inline protected fun <reified T : Any?> responseBody(
        body: ResponseBody?,
        mediaType: String = JsonMediaType
    ): T? {
        if (body == null) return null
        return when (mediaType) {
            JsonMediaType -> Serializer.moshi.adapter(T::class.java).fromJson(body.source())
            else -> TODO()
        }
    }

    inline protected fun <reified T : Any?> responseBodyOfList(
        body: ResponseBody?,
        mediaType: String = JsonMediaType
    ): List<T>? {
        if (body == null) return null

        val data = body.string()
        Timber.v("Wordnking Server Response %s", data)
        when (mediaType) {
            JsonMediaType -> {
                /*               val type = Types.newParameterizedType(List::class.java, T::class.java)
                               val adapter: JsonAdapter<List<T>> = Serializer.moshi.adapter(type)
                               return adapter.fromJson(data)*/

                val result = Json.parseList<T>(data)
                return result
            }
            else -> TODO()
        }
    }

    inline protected fun <reified T : Any?> request(
        requestConfig: RequestConfig,
        body: Any? = null
    ): ApiResponse<T?> {
        val httpUrl = HttpUrl.parse(baseUrl) ?: throw IllegalStateException("baseUrl is invalid.")

        var urlBuilder = httpUrl.newBuilder()
            .addPathSegments(requestConfig.path.trimStart('/'))

        requestConfig.query.forEach { query ->
            query.value.forEach { queryValue ->
                urlBuilder = urlBuilder.addQueryParameter(query.key, queryValue)
            }
        }

        val url = urlBuilder.build()
        val headers = requestConfig.headers + defaultHeaders

        if (headers[ContentType] ?: "" == "") {
            throw IllegalStateException("Missing Content-Type header. This is required.")
        }

        if (headers[Accept] ?: "" == "") {
            throw IllegalStateException("Missing Accept header. This is required.")
        }

        // TODO: support multiple contentType,accept options here.
        val contentType = (headers[ContentType] as String).substringBefore(";").toLowerCase()
        val accept = (headers[Accept] as String).substringBefore(";").toLowerCase()

        var request: Request.Builder = when (requestConfig.method) {
            RequestMethod.DELETE -> Request.Builder().url(url).delete()
            RequestMethod.GET -> Request.Builder().url(url)
            RequestMethod.HEAD -> Request.Builder().url(url).head()
            RequestMethod.PATCH -> Request.Builder().url(url).patch(
                requestBody(
                    body!!,
                    contentType
                )
            )
            RequestMethod.PUT -> Request.Builder().url(url).put(requestBody(body!!, contentType))
            RequestMethod.POST -> Request.Builder().url(url).post(requestBody(body!!, contentType))
            RequestMethod.OPTIONS -> Request.Builder().url(url).method("OPTIONS", null)
        }

        headers.forEach { header -> request = request.addHeader(header.key, header.value) }

        val realRequest = request.build()
        val response = client.newCall(realRequest).execute()

        // TODO: handle specific mapping types. e.g. Map<int, Class<?>>
        when {
            response.isRedirect -> return Redirection(
                response.code(),
                response.headers().toMultimap()
            )
            response.isInformational -> return Informational(
                response.message(),
                response.code(),
                response.headers().toMultimap()
            )
            response.isSuccessful -> return Success(
                responseBody(response.body(), accept),
                response.code(),
                response.headers().toMultimap()
            )
            response.isClientError -> return ClientError(
                response.body()?.string(),
                response.code(),
                response.headers().toMultimap()
            )
            else -> return ServerError(
                null,
                response.body()?.string(),
                response.code(),
                response.headers().toMultimap()
            )
        }
    }

    inline protected fun <reified T : Any?> requestOfList(
        requestConfig: RequestConfig,
        body: Any? = null
    ): ApiResponse<List<T>?> {
        val httpUrl = HttpUrl.parse(baseUrl) ?: throw IllegalStateException("baseUrl is invalid.")

        var urlBuilder = httpUrl.newBuilder()
            .addPathSegments(requestConfig.path.trimStart('/'))

        requestConfig.query.forEach { query ->
            query.value.forEach { queryValue ->
                urlBuilder = urlBuilder.addQueryParameter(query.key, queryValue)
            }
        }

        val url = urlBuilder.build()
        val headers = requestConfig.headers + defaultHeaders

        if (headers[ContentType] ?: "" == "") {
            throw IllegalStateException("Missing Content-Type header. This is required.")
        }

        if (headers[Accept] ?: "" == "") {
            throw IllegalStateException("Missing Accept header. This is required.")
        }

        // TODO: support multiple contentType,accept options here.
        val contentType = (headers[ContentType] as String).substringBefore(";").toLowerCase()
        val accept = (headers[Accept] as String).substringBefore(";").toLowerCase()

        var request: Request.Builder = when (requestConfig.method) {
            RequestMethod.DELETE -> Request.Builder().url(url).delete()
            RequestMethod.GET -> Request.Builder().url(url)
            RequestMethod.HEAD -> Request.Builder().url(url).head()
            RequestMethod.PATCH -> Request.Builder().url(url).patch(
                requestBody(
                    body!!,
                    contentType
                )
            )
            RequestMethod.PUT -> Request.Builder().url(url).put(requestBody(body!!, contentType))
            RequestMethod.POST -> Request.Builder().url(url).post(requestBody(body!!, contentType))
            RequestMethod.OPTIONS -> Request.Builder().url(url).method("OPTIONS", null)
        }

        headers.forEach { header -> request = request.addHeader(header.key, header.value) }

        val realRequest = request.build()
        val response = client.newCall(realRequest).execute()

        // TODO: handle specific mapping types. e.g. Map<int, Class<?>>
        when {
            response.isRedirect -> return Redirection(
                response.code(),
                response.headers().toMultimap()
            )
            response.isInformational -> return Informational(
                response.message(),
                response.code(),
                response.headers().toMultimap()
            )
            response.isSuccessful -> return Success(
                responseBodyOfList(response.body(), accept),
                response.code(),
                response.headers().toMultimap()
            )
            response.isClientError -> return ClientError(
                response.body()?.string(),
                response.code(),
                response.headers().toMultimap()
            )
            else -> return ServerError(
                null,
                response.body()?.string(),
                response.code(),
                response.headers().toMultimap()
            )
        }
    }
}