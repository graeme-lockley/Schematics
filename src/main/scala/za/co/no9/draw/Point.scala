package za.co.no9.draw

case class Point(x: Double, y: Double) {
	def newX(newX: Double): Point = Point(newX, y)

	def newY(newY: Double): Point = Point(x, newY)

	def add(deltaX: Double, deltaY: Double): Point = Point(x + deltaX, y + deltaY)

	def midPoint(other: Point): Point = Point((x + other.x) / 2, (y + other.y) / 2)

	def topLeft(other: Point): Point = Point(Math.min(x, other.x), Math.min(y, other.y))

	def bottomRight(other: Point): Point = Point(Math.max(x, other.x), Math.max(y, other.y))

	def invert(): Point = Point(-x, -y)
}
