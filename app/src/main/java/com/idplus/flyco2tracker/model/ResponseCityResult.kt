package com.idplus.flyco2tracker.model

/**
 * Example of response from the wikipedia request
{
    "pages": [
        {
            "id": 22989,
            "key": "Paris",
            "title": "Paris",
            "excerpt": "Paris",
            "matched_title": null,
            "description": "Capital and largest city of France",
            "thumbnail": {
                "mimetype": "image/jpeg",
                "size": null,
                "width": 60,
                "height": 38,
                "duration": null,
                "url": "//upload.wikimedia.org/wikipedia/commons/thumb/4/4b/La_Tour_Eiffel_vue_de_la_Tour_Saint-Jacques%2C_Paris_ao%C3%BBt_2014_%282%29.jpg/60px-La_Tour_Eiffel_vue_de_la_Tour_Saint-Jacques%2C_Paris_ao%C3%BBt_2014_%282%29.jpg"
            }
        }
    ]
}
 */

data class CityPictureUrl(
    val url: String
)

data class CityResult(
    val thumbnail: CityPictureUrl
)

data class ResponseCityResult(
    val pages: List<CityResult>
)