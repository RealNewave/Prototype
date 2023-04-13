import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.math.*

interface Move : Accelerate, Decelerate {

    override var currentSpeed: Double
    override val maxSpeed: Double
    fun move(view: Container) {
        val newX = view.rotation.cosine.unaryMinus()
        val newY = view.rotation.sine.unaryMinus()
        view.position(view.x + newX * currentSpeed, view.y + newY * currentSpeed)
    }
}
interface Accelerate {
    var currentSpeed: Double
    val maxSpeed: Double
    fun accelerate(speed: Double) {
        if (currentSpeed < maxSpeed && currentSpeed > maxSpeed.unaryMinus()) {
            currentSpeed += speed
        }
    }
}

interface Decelerate {
    var currentSpeed: Double
    fun decelerate() {
        if (!currentSpeed.isAlmostEquals(0.0)) currentSpeed *= 0.9
    }
}

interface Rotate {
    fun rotate(view: Container, player: Container) {
        view.rotation(Angle.between(player.x, player.y, view.x, view.y))
    }
}
