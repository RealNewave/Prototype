import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.math.*
import kotlin.math.*

const val playerMaxSpeed = 10.0

suspend fun main() = Korge(width = 1440, height = 900, bgcolor = Colors.WHITE) {
    val player = Player()
    val enemy = Enemy()

    this.addChildren(listOf(player, enemy))

//    val logText = text(text = "nothing yet", color = Colors.BLACK).xy(200, 200)

    fun Key.isPressed(): Boolean = stage.views.input.keys[this]


    player.addFixedUpdater(60.timesPerSecond) {
        if (Key.LEFT.isPressed()) player.accelerate(0)
        if (Key.RIGHT.isPressed()) player.accelerate(1)
        if (Key.UP.isPressed()) player.accelerate(2)
        if (Key.DOWN.isPressed()) player.accelerate(3)

        move()
        handleCollision(listOf(enemy.enemy))
        decelerate()
    }

    enemy.addFixedUpdater(60.timesPerSecond) {
        val playerX = player.player.x
        val playerY = player.player.y
        moveTo(playerX, playerY)
    }

    enemy.addFixedUpdater(1.timesPerSecond) {
        val playerX = player.player.x
        val playerY = player.player.y
        val enemyX = enemy.enemy.x
        val enemyY = enemy.enemy.y
        val bullet = Bullet(enemyX, enemyY, playerX, playerY)
        this.addChild(bullet)

        bullet.onCollision(filter = { it == player.player }) {
            enemy.removeChild(bullet)
        }

        bullet.onCollisionExit(filter = { it == stage }) {
            enemy.removeChild(bullet)
        }
    }
}


class Player : Container() {
    val player: Circle = circle(radius = 5.0, fill = Colors.DEEPSKYBLUE).position(500, 500)
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

class Enemy : Container() {
    val enemy = circle(radius = 8.0, fill = Colors.MEDIUMVIOLETRED).position(10, 10)


    fun moveTo(x: Double, y: Double) {
        val isPlayerRight = x.compareTo(enemy.x)
        val isPlayerUnder = y.compareTo(enemy.y)
        enemy.position(enemy.x + movePosition(isPlayerRight), enemy.y + movePosition(isPlayerUnder))
    }

    private fun movePosition(isUnderOrRight: Int, speed: Double = 3.0): Double {
        return when (isUnderOrRight) {
            1 -> speed
            -1 -> speed.unaryMinus()
            else -> 0.0
        }
    }
}

class Bullet(fromX: Double, fromY: Double, toX: Double, toY: Double) : Container() {
    private val bullet = solidRect(2.0, 2.0, Colors.BLACK).position(fromX, fromY)

    init {
        val vector = sqrt(abs(toX).pow(2) + abs(toY).pow(2))
        val extendX = calculateLine(vector, toX, fromX, windowBounds.left, windowBounds.right)
        val extendY = calculateLine(vector, toY, fromY, windowBounds.top, windowBounds.bottom)

        bullet.addFixedUpdater(60.timesPerSecond) {
            bullet.position(
                x + extendX / 120,
                y + extendY / 120
            )
        }
    }


    private fun calculateLine(vector: Double, a: Double, b: Double, boundsLow: Double, boundsHigh: Double): Double {
        return if (a > b) {
            val vectorIncreaseY = a - boundsLow
            a + (vector + vectorIncreaseY)
        } else if (a < b) {
            val vectorIncreaseY = a - boundsHigh
            a - (vector + vectorIncreaseY)
        } else {
            0.0
        }
    }
}







