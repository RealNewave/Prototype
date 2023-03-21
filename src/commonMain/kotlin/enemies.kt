import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import kotlin.random.*

class LaunchingEnemy(player: Container) : Container() {
    val view =
        circle(radius = 8.0, fill = Colors.DARKGOLDENROD).position(Random.nextInt(10, 1440), Random.nextInt(10, 900))

    private var state = 1

    init {
        this.addFixedUpdater(1.timesPerSecond) {
            if (state == 0) state++ else state--
        }
        this.addFixedUpdater(60.timesPerSecond) {
            when (state) {
                0 -> view.rotation(Angle.between(player.x, player.y, view.x, view.y))
                1 -> {
                    val newX = view.rotation.cosine.unaryMinus()
                    val newY = view.rotation.sine.unaryMinus()
                    view.position(view.x + newX * 10, view.y + newY * 10)
                }
            }
        }
    }

}

class WaitingEnemy(player: Container) : Container() {
    private val startingPosition = Point(1440/2, 900/2)
    private val aggroRange =
        circle(radius = 128.0, fill = Colors.PINK).position(startingPosition).zIndex(1)
    val view = circle(radius = 8.0, fill = Colors.SALMON).position(startingPosition).zIndex(2)

    private var engage = false

    init {
        view.centerOn(aggroRange)
        aggroRange.onCollision(filter = { it == player }) {
            engage = true
        }

        aggroRange.onCollisionExit(filter = { it == player }) {
            engage = false
        }

        this.addFixedUpdater(60.timesPerSecond) {
            if (engage) {
                view.rotation(Angle.between(player.x, player.y, view.x, view.y))
                val newX = view.rotation.cosine.unaryMinus()
                val newY = view.rotation.sine.unaryMinus()
                view.position(view.x + newX * 10, view.y + newY * 10)
            } else if(Point(view.x, view.y) != startingPosition){
                view.rotation(Angle.between(Point(view.x, view.y), startingPosition))
                val newX = view.rotation.cosine
                val newY = view.rotation.sine
                view.position(view.x + newX * 5, view.y + newY * 5)
            }
        }
    }
}



class ChasingEnemy(player: Container) : Container() {
    val view =
        circle(radius = 8.0, fill = Colors.MEDIUMVIOLETRED).position(Random.nextInt(10, 1440), Random.nextInt(10, 900))
    private val maxMovementSpeed = Random.nextDouble(1.0, 3.0)
    private var movementSpeed = 0.0

    init {
        this.addFixedUpdater(60.timesPerSecond) {
            if (movementSpeed < maxMovementSpeed) {
                movementSpeed += 0.2
            } else if (movementSpeed > 0) {
                movementSpeed -= 0.1
            }
            moveTo(view, player.x, player.y)
        }

        this.addFixedUpdater(2.timesPerSecond) {
            val bullet = Bullet(view.x, view.y, player.x, player.y)
            this.addChild(bullet)

            bullet.onCollision(filter = { it == player }) {
                this@ChasingEnemy.removeChild(bullet)
            }

            bullet.onCollisionExit(filter = { it == stage }) {
                this@ChasingEnemy.removeChild(bullet)
            }
        }
    }

    private fun moveTo(view: Container, x: Double, y: Double) {
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


