package io.github.aloussase.aurorabutler

import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.components.Service
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


@Service
class AuroraService {

    private val httpClient = OkHttpClient.Builder()
        .build()

    fun fetchConfigurationVariables(): List<ConfigurationVariable> {
        val state = AppSettings.getInstance().state ?: return emptyList()

        val apiUrl = state.apiBaseUrl ?: return emptyList()
        val bluePrint = state.bluePrintName ?: return emptyList()
        val authToken = state.authToken ?: return emptyList()
        val deploymentId = state.deploymentId ?: return emptyList()
        val environment = state.environment ?: return emptyList()

        val requestBody = """
            {
                "query": {
                    "combinator": "and",
                    "rules": [
                        {
                            "combinator": "and",
                             "rules": [
                                {
                                    "property": "deployment_id",
                                     "operator": "=",
                                     "value": "$deploymentId"
                                },
                                {
                                     "property": "entorno_name",
                                     "operator": "=",
                                     "value": "$environment"
                                }
                             ]
                        }
                    ]
                },
                "include": ["${'$'}title", "value"]
            }
        """.trimMargin()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .post(requestBody)
            .url("$apiUrl/v1/blueprints/$bluePrint/entities/search")
            .header("Authorization", "Bearer $authToken")
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            return emptyList()
        }

        response.body.byteStream().use { inputStream ->
            val rootNode = ObjectMapper().readTree(inputStream)
            return rootNode["entities"]
                .map {
                    ConfigurationVariable(
                        name = it["title"].asText(),
                        value = it["properties"]["value"].asText()
                    )
                }
        }
    }

}
