import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.Circle
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import kotlin.random.*

suspend fun main() = Korge(width = 1440, height = 900, bgcolor = Colors.WHITE) {
    val player = Player()
    val chasingEnemy1 = ChasingEnemy(player)
    val chasingEnemy2 = ChasingEnemy(player)
    val chasingEnemy3 = ChasingEnemy(player)

    val enemies = listOf(chasingEnemy1, chasingEnemy2, chasingEnemy3)

    this.addChild(player)
    this.addChildren(enemies)

    fun Key.isPressed(): Boolean = stage.views.input.keys[this]

    player.addFixedUpdater(60.timesPerSecond) {
        if (Key.LEFT.isPressed()) player.rotateLeft()
        if (Key.RIGHT.isPressed()) player.rotateRight()
        if (Key.UP.isPressed()) player.accelerate(0.5)
        if (Key.DOWN.isPressed()) player.accelerate(-0.5)

        move()
        handleCollision(listOf(chasingEnemy1.view,chasingEnemy2.view, chasingEnemy3.view))
        decelerate()
    }

}


class Player : Container() {
    val mainView: Circle = circle(radius = 10.0, fill = Colors.DEEPSKYBLUE).position(500, 500)
    private val tailView: SolidRect = solidRect(30.0, 10.0, Colors.DEEPSKYBLUE)

    private var currentSpeed = 0.0
    private var maxSpeed = 5.0

    init {
        tailView.addTo(mainView)
        tailView.position(5, 5)


        this.addFixedUpdater(10.timesPerSecond) {
            val smoke = Smoke(this.mainView.x, this.mainView.y)
            this.addChild(smoke)
            if (this.numChildren > 5) this.removeChildAt(1)
        }
    }

    fun accelerate(speed: Double) {
        if (currentSpeed < maxSpeed && currentSpeed > maxSpeed.unaryMinus()) {
            currentSpeed += speed
        }
    }

    fun decelerate() {
        if (currentSpeed > 0) currentSpeed -= 0.1
        else if (currentSpeed < 0) currentSpeed += 0.1
    }

    fun move() {
        val x = mainView.rotation.cosine
        val y = mainView.rotation.sine
        mainView.position(mainView.x + x.unaryMinus() * currentSpeed, mainView.y + y.unaryMinus() * currentSpeed)
    }

    fun handleCollision(objects: List<View>) {
        mainView.onCollision(filter = { objects.contains(it) }) {
            mainView.color = Colors.YELLOW
        }

        mainView.onCollisionExit(filter = { objects.contains(it) }) {
            mainView.color = Colors.DEEPSKYBLUE
        }
    }

    fun rotateLeft() {
        mainView.rotation(Angle.fromDegrees(mainView.rotation.degrees - 3.0))
    }

    fun rotateRight() {
        mainView.rotation(Angle.fromDegrees(mainView.rotation.degrees + 3.0))
    }
}

class Smoke(x: Double, y: Double) : Container() {
    private val view: SolidRect = solidRect(10.0, 10.0, Colors.PURPLE).position(x, y)

    init {

        this.addFixedUpdater(60.timesPerSecond) {
            view.color = RGBA.unclamped(view.color.r + 5, view.color.g + 5, view.color.b + 5, view.color.a + 5)
        }
    }
}

//launchingEnemy
//waitingEnemy
//
class ChasingEnemy(player: Player) : Container() {
    val view = circle(radius = 8.0, fill = Colors.MEDIUMVIOLETRED).position(Random.nextInt(10,  1440), Random.nextInt(10,  900))
    private val movementSpeed = Random.nextInt(1,6)

    init {
        this.addFixedUpdater(60.timesPerSecond) {
            val playerX = player.mainView.x
            val playerY = player.mainView.y
            moveTo(playerX, playerY)
        }

        this.addFixedUpdater(2.timesPerSecond) {
            val playerX = player.mainView.x
            val playerY = player.mainView.y
            val enemyX = view.x
            val enemyY = view.y
            val bullet = Bullet(enemyX, enemyY, playerX, playerY)
            this.addChild(bullet)

            bullet.onCollision(filter = { it == player.mainView }) {
                this@ChasingEnemy.removeChild(bullet)
            }

            bullet.onCollisionExit(filter = { it == stage }) {
                this@ChasingEnemy.removeChild(bullet)
            }
        }
    }
    private fun moveTo(x: Double, y: Double) {
        view.rotation(Angle.between(x, y, view.x, view.y))
        val newX = view.rotation.cosine.unaryMinus()
        val newY = view.rotation.sine.unaryMinus()
        view.position(view.x + newX * movementSpeed, view.y + newY * movementSpeed)
    }
}

class Bullet(fromX: Double, fromY: Double, toX: Double, toY: Double) : Container() {
    private val view = solidRect(3.0, 3.0, Colors.BLACK).position(fromX, fromY)
    private val flySpeed = 7.0
    init {
        view.rotation(Angle.between(toX, toY, fromX, fromY))
        view.addFixedUpdater(60.timesPerSecond) {
            val x = rotation.cosine.unaryMinus()
            val y = rotation.sine.unaryMinus()
            position(view.x + x * flySpeed, view.y + y * flySpeed)
        }
    }
}







