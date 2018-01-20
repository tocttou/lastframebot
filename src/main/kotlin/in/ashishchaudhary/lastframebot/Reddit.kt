package `in`.ashishchaudhary.lastframebot

import android.util.Patterns
import net.dean.jraw.models.Comment
import net.dean.jraw.models.Submission
import org.apache.logging.log4j.LogManager
import java.net.MalformedURLException
import java.net.URL

object Reddit {
    private val reddit = Auth.redditClient
    val logger = LogManager.getLogger("lastframebot")
    private val imgurDomainRegex = Regex("^((i|www)\\.)?imgur.com$", RegexOption.MULTILINE)
    private val imgurFileRegex = Regex("^/([a-zA-Z0-9]*).(gifv?|webm|mp4)$", RegexOption.MULTILINE)
    private val imgurGalleryRegex = Regex("^/gallery([/a-zA-Z0-9]*)$", RegexOption.MULTILINE)
    private val imgurNoPrefixRegex =
        Regex("^/(?!gallery/)([a-zA-Z0-9]{1,})$", RegexOption.MULTILINE)

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

    sealed class Resource {
        data class ImgurGif(val url: URL) : Resource()
        data class ImgurGifv(val url: URL) : Resource()
        data class ImgurMP4(val url: URL) : Resource()
        data class ImgurWebm(val url: URL) : Resource()
        data class ImgurUnknown(val url: URL) : Resource()
    }

    fun transformURLToResource(url: URL): Resource? {
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
                            "gif" -> Resource.ImgurGif(URL(downloadURL))
                            "gifv" -> Resource.ImgurGifv(URL(downloadURL))
                            "webm" -> Resource.ImgurWebm(URL(downloadURL))
                            else -> Resource.ImgurMP4(URL(downloadURL))
                        }
                    }
                    imgurGalleryRegex.matches(path) -> null
                    imgurNoPrefixRegex.matches(path) -> {
                        val groupValues = imgurNoPrefixRegex.find(path)!!.groupValues
                        val id = groupValues.last()
                        val downloadURL = "https://imgur.com/download/$id"
                        Resource.ImgurUnknown(URL(downloadURL))
                    }
                    else -> {
                        logger.info("No value matched any Imgur regex for url - $url")
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