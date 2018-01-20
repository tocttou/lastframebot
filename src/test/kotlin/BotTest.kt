import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

object BotTest : Spek({
    given("a bot instance") {
        on("running it") {
            it("should do something lol") {
                assertEquals(true, true)
            }
        }
    }
})