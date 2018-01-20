package `in`.ashishchaudhary.lastframebot.resources

object Imgur {
    val imgurDomainRegex = Regex("^((i|www)\\.)?imgur.com$", RegexOption.MULTILINE)
    val imgurFileRegex = Regex("^/([a-zA-Z0-9]{1,}).(gifv?|webm|mp4)$", RegexOption.MULTILINE)
    val imgurGalleryRegex = Regex("^/gallery([/a-zA-Z0-9]*)$", RegexOption.MULTILINE)
    val imgurNoPrefixRegex = Regex("^/(?!gallery/)([a-zA-Z0-9]{1,})$", RegexOption.MULTILINE)
}