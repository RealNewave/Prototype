import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import kotlin.random.*

open class Enemy(radius: Double = 8.0, fill: RGBA, private val player: Container) : Container(), Move {
    val view = circle(radius = radius, fill = fill).position(Random.nextInt(10, 1440), Random.nextInt(10, 900))
    private var isMoving = false
    override val maxSpeed = Random.nextDouble(3.0, 8.0)
    override var currentSpeed = 0.0

    init {
        this.addFixedUpdater(60.timesPerSecond) {
            if (view.distanceTo(player.posOpt) > 5) {
                isMoving = true
                move(view)
            } else isMoving = false
            if (isMoving) accelerate(0.1) else decelerate()
        }
    }
}

class LaunchingEnemy(player: Container, radius: Double = 8.0, fill: RGBA = Colors.PURPLE) : Enemy(radius, fill, player), Rotate {
    private var state = 1

    init {
        this.addFixedUpdater(1.timesPerSecond) {
            if (state == 0) state++ else state--
        }
        this.addFixedUpdater(60.timesPerSecond) {
            when (state) {
                0 -> rotate(view, player)
                1 -> move(view)
            }
        }
    }
}

class WaitingEnemy(player: Container, radius: Double = 15.0, fill: RGBA = Colors.YELLOW) : Enemy(radius, fill, player) {
    private val aggroRange = circle(radius = 128.0, fill = Colors.PINK).position(view.anchorX, view.anchorY).zIndex(1)
    private var startingPosition: Point
    private var engage = false
    private var onStart = true

    init {
        view.zIndex(2)
        view.centerOn(aggroRange)
        startingPosition = view.posOpt
        aggroRange.onCollision(filter = { it == player }) {
            engage = true
        }

        aggroRange.onCollisionExit(filter = { it == player }) {
            engage = false
        }

        this.addFixedUpdater(60.timesPerSecond) {
            if (engage) {
                onStart = false
                move(view)
            } else if (Point(view.x, view.y).distanceTo(startingPosition) >= 5) {
                view.rotation(Angle.between(Point(view.x, view.y), startingPosition).plus(Angle.HALF))
                move(view)
            }
        }
    }
}


class ChasingEnemy(player: Container, radius: Double = 6.0, fill: RGBA = Colors.RED) : Enemy(radius, fill, player), Rotate {
    init {
        this.addFixedUpdater(60.timesPerSecond) {
            rotate(view, player)
            move(view)
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


