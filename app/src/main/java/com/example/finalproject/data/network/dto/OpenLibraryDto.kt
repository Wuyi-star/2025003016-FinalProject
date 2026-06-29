package com.example.finalproject.data.network.dto

import com.google.gson.annotations.SerializedName

data class OpenLibrarySearchResponse(
    @SerializedName("numFound") val numFound: Int = 0,
    @SerializedName("docs") val docs: List<OpenLibraryDoc> = emptyList()
)

data class OpenLibraryDoc(
    @SerializedName("key") val key: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("author_name") val authorName: List<String>? = null,
    @SerializedName("first_publish_year") val firstPublishYear: Int? = null,
    @SerializedName("publisher") val publisher: List<String>? = null,
    @SerializedName("isbn") val isbn: List<String>? = null,
    @SerializedName("number_of_pages_median") val numberOfPages: Int? = null,
    @SerializedName("cover_i") val coverId: Long? = null,
    @SerializedName("subject") val subjects: List<String>? = null,
    @SerializedName("ratings_average") val ratingsAverage: Double? = null
)