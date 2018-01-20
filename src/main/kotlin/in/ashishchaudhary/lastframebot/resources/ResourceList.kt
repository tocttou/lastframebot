package `in`.ashishchaudhary.lastframebot.resources

import java.net.URL

sealed class ResourceList {
    data class ImgurGif(val url: URL) : ResourceList()
    data class ImgurGifv(val url: URL) : ResourceList()
    data class ImgurMP4(val url: URL) : ResourceList()
    data class ImgurWebm(val url: URL) : ResourceList()
    data class ImgurUnknown(val url: URL) : ResourceList()
    data class GfycatWebm(val url: URL) : ResourceList()
    data class GfycatMP4(val url: URL) : ResourceList()
}
