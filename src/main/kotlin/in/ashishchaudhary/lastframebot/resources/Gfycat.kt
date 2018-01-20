package `in`.ashishchaudhary.lastframebot.resources

object Gfycat {
    val gfycatDomainRegex = Regex("^((giant|www)\\.)?gfycat.com\$", RegexOption.MULTILINE)
    val gfycatGiantRegex = Regex("^/([a-zA-Z0-9]{1,}).(webm|mp4)\$", RegexOption.MULTILINE)
    val gfycatNoPrefixRegex = Regex("/([a-zA-Z0-9]{1,})", RegexOption.MULTILINE)
    val gfycatGifsDetailRegex = Regex("/gifs/detail/([a-zA-Z0-9]{1,})", RegexOption.MULTILINE)

    fun getGiantMP4FromSlug(slug: String) = "https://giant.gfycat.com/$slug.mp4"
}