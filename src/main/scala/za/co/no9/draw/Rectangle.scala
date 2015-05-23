package za.co.no9.draw

trait Rectangle {
	val topLeft: Point
	val bottomRight: Point

	def height = bottomRight.y - topLeft.y

	def width = bottomRight.x - topLeft.x

	def union(rectangle: Rectangle): Rectangle = BoundedRectangle(topLeft.topLeft(rectangle.topLeft), bottomRight.bottomRight(rectangle.bottomRight))

	def grips: Grips = {
		val leftX = topLeft.x
		val middleX = topLeft.midPoint(bottomRight).x
		val rightX = bottomRight.x
		val topY = topLeft.y
		val middleY = topLeft.midPoint(bottomRight).y
		val bottomY = bottomRight.y

		DiscreteGrips(
			Point(leftX, topY), Point(middleX, topY), Point(rightX, topY),
			Point(leftX, middleY), Point(middleX, middleY), Point(rightX, middleY),
			Point(leftX, bottomY), Point(middleX, bottomY), Point(rightX, bottomY))
	}
}

case class PointRectangle(point: Point) extends Rectangle {
	val topLeft = point
	val bottomRight = point
}

case class BoundedRectangle(topLeft: Point, bottomRight: Point) extends Rectangle {
}

