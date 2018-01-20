package `in`.ashishchaudhary.lastframebot

import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper

object Auth {
    private val userAgent = UserAgent("bot", "in.ashishchaudhary.lastframebot", "v1.0", "t0ctt0u")
    private val credentials = Credentials.script(
        System.getenv("username"),
        System.getenv("password"),
        System.getenv("clientId"),
        System.getenv("clientSecret")
    )
    private val adapter = OkHttpNetworkAdapter(userAgent)
    val redditClient = OAuthHelper.automatic(adapter, credentials)
}