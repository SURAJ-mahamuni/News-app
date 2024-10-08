package com.study.news.api.endPointHelper

import java.net.URL

interface EndPointType {
    val baseURL: String
    var path: String
    val url: URL?
        get() = URL("$baseURL$path")
    val httpMethod: HttpMethod
    val header: Map<String, String>?
}