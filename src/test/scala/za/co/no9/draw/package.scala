package za.co.no9

package object draw {
	def rectangleEquals(rectangleA: Rectangle, rectangleB: Rectangle, theta: Double): Boolean = pointEquals(rectangleA.topLeft, rectangleB.topLeft, theta) && pointEquals(rectangleA.bottomRight, rectangleB.bottomRight, theta)

	def pointEquals(p1: Point, p2: Point, theta: Double): Boolean = {
		doubleEquals(p1.x, p2.x, theta) && doubleEquals(p1.y, p2.y, theta)
	}

	def doubleEquals(x1: Double, x2: Double, theta: Double): Boolean = Math.abs(x1 - x2) < theta

	def initialLayedOutShape: LayedOutShape = new LayedOutShape {
		override def boundingRectangle: Rectangle = PointRectangle(Point(0, 0))

		override def normalisedBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

		override def grips: Grips = new RectangleGrips(Point(0, 0), Point(0, 0))

		override def render(canvas: Canvas): Unit = ()

		override def nestedShapes: List[LayedOutShape] = List()

		override val name: String = "_"
		override val previous: Option[LayedOutShape] = Option.empty
	}
}
