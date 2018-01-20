package `in`.ashishchaudhary.lastframebot

class Bot {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val latestParentChildPair = Reddit.getLatestParentChildPair("testingground4bots").map {
                Reddit.getURLCommentPair(it)
            }.filterNot { it.first == null }

            for (pcp in latestParentChildPair) {
                val resource = Reddit.transformURLToResource(pcp.first!!)
                if (resource != null) {
                    println(resource)
                    println(pcp.second.body)
                }
            }
        }
    }
}