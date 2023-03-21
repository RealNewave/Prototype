import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.math.*
import kotlin.math.*

//        if (Key.LEFT.isPressed()) player.accelerate(0)
//        if (Key.RIGHT.isPressed()) player.accelerate(1)
//        if (Key.UP.isPressed()) player.accelerate(2)
//        if (Key.DOWN.isPressed()) player.accelerate(3)

class OldCode : Container() {
    private val player: Circle = circle(radius = 10.0, fill = Colors.DEEPSKYBLUE).position(500, 500)
    private val playerMaxSpeed = 10
    private val movementValues = arrayOf(0.0, 0.0, 0.0, 0.0)

    fun accelerate(index: Int) {
        if (movementValues[index] < playerMaxSpeed) movementValues[index] += 2.0
    }

    fun decelerate() {
        movementValues.forEachIndexed { index, item ->
            if (item < 0.5) movementValues[index] = 0.0
            else if (item > 0) movementValues[index] -= 0.5
        }
    }

    fun move() {
        if (movementValues.filter { it == 0.0 }.size != 4) {
            val xDirection = movementValues[1] - movementValues[0]
            val yDirection = movementValues[3] - movementValues[2]
            val (directionX, directionY) = calculateMovement(xDirection, yDirection)
            player.position(player.x + directionX, player.y + directionY)
        }
    }

    fun handleCollision(enemies: List<View>) {
        player.onCollision(filter = { enemies.contains(it) }) {
            player.color = Colors.YELLOW
        }

        player.onCollisionExit(filter = { enemies.contains(it) }) {
            player.color = Colors.DEEPSKYBLUE
        }
    }


    private fun calculateMovement(xDirection: Double, yDirection: Double): Pair<Double, Double> {
        val vector = sqrt(abs(xDirection).pow(2) + abs(yDirection).pow(2))
        var xDirectionNormalized = xDirection.div(vector)
        var yDirectionNormalized = yDirection.div(vector)
        if (yDirectionNormalized.isNanOrInfinite()) yDirectionNormalized = 0.0
        if (xDirectionNormalized.isNanOrInfinite()) xDirectionNormalized = 0.0

        return Pair(xDirectionNormalized * abs(xDirection), yDirectionNormalized * abs(yDirection))
    }

}


//class ChasingEnemy(player: Container, coroutineContext: CoroutineContext) : Container() {
//    val view = circle(radius = 8.0, fill = Colors.MEDIUMVIOLETRED).position(Random.nextInt(10, 1440), Random.nextInt(10, 900))
//    private val movementSpeed = Random.nextInt(500, 2000)
//    private var lastPlayerX = player.x
//    private var lastPlayerY = player.y
//
//    init {
//        this.addFixedUpdater(60.timesPerSecond) {
//            launchImmediately(coroutineContext) {
//                if (lastPlayerX != player.x || lastPlayerY != player.y) {
//                    view.tween(
//                        view::x[view.x, (player.x - 60 / movementSpeed)],
//                        view::y[view.y, (player.y - 60 / movementSpeed)],
//                        time = movementSpeed.milliseconds,
//                        easing = Easing.EASE_OUT_BACK
//                    )
//                }
//            }
//        }
//        this.addFixedUpdater(2.timesPerSecond) {
//            val bullet = Bullet(view.x, view.y, player.x, player.y)
//            this.addChild(bullet)
//
//            bullet.onCollision(filter = { it == player }) {
//                this@ChasingEnemy.removeChild(bullet)
//            }
//
//            bullet.onCollisionExit(filter = { it == stage }) {
//                this@ChasingEnemy.removeChild(bullet)
//            }
//        }
//    }
//
//}
