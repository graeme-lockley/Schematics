package za.co.no9

import java.awt.Graphics2D

package object draw {
	def rectangleEquals(rectangleA: Rectangle, rectangleB: Rectangle, theta: Double): Boolean = pointEquals(rectangleA.topLeft, rectangleB.topLeft, theta) && pointEquals(rectangleA.bottomRight, rectangleB.bottomRight, theta)

	def pointEquals(p1: Point, p2: Point, theta: Double): Boolean = {
		//		println(s"pointEquals: $p1 $p2")
		doubleEquals(p1.x, p2.x, theta) && doubleEquals(p1.y, p2.y, theta)
	}

	def doubleEquals(x1: Double, x2: Double, theta: Double): Boolean = Math.abs(x1 - x2) < theta

	def initialLayedOutShape: LayedOutShape = new LayedOutShape {
		override def boundingRectangle: Rectangle = PointRectangle(Point(0, 0))

		override def grips: Grips = new RectangleGrips(Point(0, 0), Point(0, 0))

		override def render(g: Graphics2D): Unit = ()

		override def nestedShapes: List[LayedOutShape] = List()

		override val name: Option[String] = Option.empty
		override val previous: Option[LayedOutShape] = Option.empty
	}
}
