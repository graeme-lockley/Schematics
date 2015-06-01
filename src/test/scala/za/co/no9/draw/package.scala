package za.co.no9

package object draw {
	def rectangleEquals(rectangleA: Rectangle, rectangleB: Rectangle, theta: Double): Boolean = pointEquals(rectangleA.topLeft, rectangleB.topLeft, theta) && pointEquals(rectangleA.bottomRight, rectangleB.bottomRight, theta)

	def pointEquals(p1: Point, p2: Point, theta: Double): Boolean = {
		val result = doubleEquals(p1.x, p2.x, theta) && doubleEquals(p1.y, p2.y, theta)

		if (!result) {
			println(s"pointEquals failed: $p1 $p2")
		}
		result
	}

	def doubleEquals(x1: Double, x2: Double, theta: Double): Boolean = Math.abs(x1 - x2) < theta

	def initialLayedOutShape: LayedOutShape = new LayedOutShape with UsableLayedOutShape {
		override def __ls = new LayoutState(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		override def realBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

		override def relativeBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

		override def grips: Grips = new RectangleGrips(Point(0, 0), Point(0, 0))

		override def render(canvas: Canvas): Unit = ()

		override def nestedShapes: List[LayedOutShape] = List()

		override val name: String = "_test"
		override val previous: Option[LayedOutShape] = Option.empty
	}
}
