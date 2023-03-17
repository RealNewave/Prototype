import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.math.*
import kotlin.math.*

//        if (Key.LEFT.isPressed()) player.accelerate(0)
//        if (Key.RIGHT.isPressed()) player.accelerate(1)
//        if (Key.UP.isPressed()) player.accelerate(2)
//        if (Key.DOWN.isPressed()) player.accelerate(3)

class Player8Move : Container() {
    val player: Circle = circle(radius = 10.0, fill = Colors.DEEPSKYBLUE).position(500, 500)

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
