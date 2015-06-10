package za.co.no9.draw

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class LineShapeTest extends FlatSpec with PropertyChecks with Matchers {
	val theta = 0.001

	"Given a line between absolute points with a relative offset" should "correctly calculate the absolute points" in {
		val line = new LineShape(_ => List(At(Point(0, 0), Point(0, 0)), At(Point(100, 0), Point(0, 0))))
		val ls = new TX(rotation = 30.0, scale = 2.0, translation = Point(0, 0))

		val layedOutShape = line.layout(initialLayedOutShape, ls)

		assert(pointEquals(layedOutShape.realBoundedRectangle.topLeft, Point(0.0, 0.0), theta))
		assert(pointEquals(layedOutShape.realBoundedRectangle.bottomRight, Point(100.0, 0.0), theta))
	}
}
