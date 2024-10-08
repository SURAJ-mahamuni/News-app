package com.study.news.api


import com.study.news.helper.Constants
import com.study.news.api.endPointHelper.EndPointType
import com.study.news.api.endPointHelper.HttpMethod


sealed class EndPointItems(param : String? = null) : EndPointType {
    data object TopHeadLines : EndPointItems()
    data object Health : EndPointItems()
    data object Sports : EndPointItems()
    data object Finance : EndPointItems()
    data class Search(val query : String) : EndPointItems()

    override val baseURL: String
        get() = Constants.BASE_URI

    override var path: String = ""
        get() = when (this) {
            is TopHeadLines -> "top-headlines?apiKey=aa1533b294c64992a8ed380b91b45a0e&country=us"
            is Health -> "top-headlines?country=us&category=health&apiKey=aa1533b294c64992a8ed380b91b45a0e"
            is Sports -> "top-headlines?country=us&category=sports&apiKey=aa1533b294c64992a8ed380b91b45a0e"
            is Finance -> "top-headlines?country=us&category=business&apiKey=aa1533b294c64992a8ed380b91b45a0e"
            is Search -> "everything?q=$query&apiKey=aa1533b294c64992a8ed380b91b45a0e"
//            is Posts -> "api/login${param}"
            else -> ""
        }

    override val httpMethod: HttpMethod
        get() = when (this) {
            is TopHeadLines -> HttpMethod.GET
            is Health -> HttpMethod.GET
            is Sports -> HttpMethod.GET
            is Finance -> HttpMethod.GET
            is Search -> HttpMethod.GET
//            is Posts -> HttpMethod.MULTIPART_POST
//            is Posts -> HttpMethod.PUT
//            is Posts -> HttpMethod.POST
        }


    override val header: Map<String, String>
        get() {
            return when (this) {

                else -> {
                    mapOf(
                        "Content-Type" to "application/json"
                    )
                }
            }
        }
}
