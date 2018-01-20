package `in`.ashishchaudhary.lastframebot

import `in`.ashishchaudhary.lastframebot.resources.Gfycat.getGiantMP4FromSlug
import `in`.ashishchaudhary.lastframebot.resources.Gfycat.gfycatDomainRegex
import `in`.ashishchaudhary.lastframebot.resources.Gfycat.gfycatGiantRegex
import `in`.ashishchaudhary.lastframebot.resources.Gfycat.gfycatGifsDetailRegex
import `in`.ashishchaudhary.lastframebot.resources.Gfycat.gfycatNoPrefixRegex
import `in`.ashishchaudhary.lastframebot.resources.Imgur.imgurDomainRegex
import `in`.ashishchaudhary.lastframebot.resources.Imgur.imgurFileRegex
import `in`.ashishchaudhary.lastframebot.resources.Imgur.imgurGalleryRegex
import `in`.ashishchaudhary.lastframebot.resources.Imgur.imgurNoPrefixRegex
import `in`.ashishchaudhary.lastframebot.resources.ResourceList
import android.util.Patterns
import net.dean.jraw.models.Comment
import net.dean.jraw.models.Submission
import org.apache.logging.log4j.LogManager
import java.net.MalformedURLException
import java.net.URL

object Reddit {
    private val reddit = Auth.redditClient
    val logger = LogManager.getLogger("lastframebot")

    fun getLatestParentChildPair(subreddit: String): List<Pair<String, Comment>> {
        val comments = reddit.subreddit(subreddit).comments().limit(50).build().iterator().next()
        val parents = reddit.lookup(comments.map { it.parentFullName }).map {
            when (it) {
                is Comment -> it.body
                else -> (it as Submission).url
            }
        }
        return parents.zip(comments)
    }

    private fun getFirstURLFromText(string: String): URL? {
        val matches = mutableListOf<String>()
        val matcher = Patterns.AUTOLINK_WEB_URL.matcher(string)
        while (matcher.find()) matches.add(matcher.group())
        if (matches.size != 0) {
            try {
                return URL(matches[0])
            } catch (e: MalformedURLException) {
            }
        }
        return null
    }

    fun transformURLToResource(url: URL): ResourceList? {
        return when {
            imgurDomainRegex.matches(url.host) -> {
                val path = url.path.trimEnd('/')
                return when {
                    imgurFileRegex.matches(path) -> {
                        val groupValues = imgurFileRegex.find(path)!!.groupValues
                        val extension = groupValues.last()
                        val id = groupValues[groupValues.size - 2]
                        val downloadURL = "https://imgur.com/download/$id"
                        when (extension) {
                            "gif" -> ResourceList.ImgurGif(URL(downloadURL))
                            "gifv" -> ResourceList.ImgurGifv(URL(downloadURL))
                            "webm" -> ResourceList.ImgurWebm(URL(downloadURL))
                            else -> ResourceList.ImgurMP4(URL(downloadURL)) // mp4
                        }
                    }
                    imgurGalleryRegex.matches(path) -> null
                    imgurNoPrefixRegex.matches(path) -> {
                        val groupValues = imgurNoPrefixRegex.find(path)!!.groupValues
                        val id = groupValues.last()
                        val downloadURL = "https://imgur.com/download/$id"
                        ResourceList.ImgurUnknown(URL(downloadURL))
                    }
                    else -> {
                        logger.info("No value matched any Imgur regex for url - $url")
                        null
                    }
                }
            }
            gfycatDomainRegex.matches(url.host) -> {
                val path = url.path.trimEnd('/')
                return when {
                    gfycatGiantRegex.matches(path) -> {
                        when (gfycatGiantRegex.find(path)!!.groupValues.last()) {
                            "webm" -> ResourceList.GfycatWebm(url)
                            else -> ResourceList.GfycatMP4(url) //mp4
                        }
                    }
                    gfycatNoPrefixRegex.matches(path) -> {
                        val slug = gfycatNoPrefixRegex.find(path)!!.groupValues.last()
                        ResourceList.GfycatMP4(URL(getGiantMP4FromSlug(slug)))
                    }
                    gfycatGifsDetailRegex.matches(path) -> {
                        val slug = gfycatNoPrefixRegex.find(path)!!.groupValues.last()
                        ResourceList.GfycatMP4(URL(getGiantMP4FromSlug(slug)))
                    }
                    else -> {
                        logger.info("No value matched any Gfycat regex for url - $url")
                        null
                    }
                }
            }
            else -> null
        }
    }

    fun getURLCommentPair(pair: Pair<String, Comment>): Pair<URL?, Comment> {
        val firstURLFromText = getFirstURLFromText(pair.first)
        return when (firstURLFromText) {
            is URL -> Pair(firstURLFromText, pair.second)
            else -> Pair(null, pair.second)
        }
    }
}