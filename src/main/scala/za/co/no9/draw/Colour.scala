package za.co.no9.draw

case class Colour(r: Int, g: Int, b: Int)

object Colour {
	val WHITE = Colour(255, 255, 255)
	val BLACK = Colour(0, 0, 0)
	val RED = Colour(255, 0, 0)
	val GREEN = Colour(0, 255, 0)
	val BLUE = Colour(0, 0, 255)
}
