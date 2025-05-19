package network

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import network.mockresponse.APITestData

const val CONTENT_TYPE_JSON = "application/json"

val topFiftyChartsMockEngine = MockEngine { _ ->
    respond(
        content = ByteReadChannel(APITestData.topFiftyCharts),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, CONTENT_TYPE_JSON)
    )
}

val newReleasedAlbumsMockEngine = MockEngine { _ ->
    respond(
        content = ByteReadChannel(APITestData.newReleases),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, CONTENT_TYPE_JSON)
    )
}

val featuredPlaylistsMockEngine = MockEngine { _ ->
    respond(
        content = ByteReadChannel(APITestData.featuredPlaylists),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, CONTENT_TYPE_JSON)
    )
}