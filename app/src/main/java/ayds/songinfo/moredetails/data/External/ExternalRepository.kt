package ayds.songinfo.moredetails.data.External

import ayds.songinfo.moredetails.domain.entities.Artist.ArtistBiography
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import java.io.IOException


private const val LASTFM_BASE_URL = "https://ws.audioscrobbler.com/2.0/"


interface ExternalRepository {

    fun getSongFromService(artistName: String): Response<String>?

    // fun getArtistBioFromExternalData(serviceData: String?,artistName: String): ArtistBiography

    // fun getArticleFromService(artistName: String): ArtistBiography

}

internal class ExternalRepositoryImp(private val lastFMAPI: LastFMAPI): ExternalRepository {

        
    override fun getSongFromService(artistName: String): Response<String> =
        lastFMAPI.getArtistInfo(artistName).execute()


    fun getArtistBioFromExternalData(
        serviceData: String?,
        artistName: String
    ): ArtistBiography {
        val gson = Gson()
        val jobj = gson.fromJson(serviceData, JsonObject::class.java)

        val artist = jobj["artist"].getAsJsonObject()
        val bio = artist["bio"].getAsJsonObject()
        val extract = bio["content"]
        val url = artist["url"]
        val text = extract?.asString ?: "No Results"

        return ArtistBiography(artistName, text, url.asString)
    }

    fun getArticleFromService(artistName: String): ArtistBiography {

        var artistBiography = ArtistBiography(artistName, "", "")
        try {
            val callResponse = getSongFromService(artistName)
            artistBiography = getArtistBioFromExternalData(callResponse.body(), artistName)
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return artistBiography
    }
}