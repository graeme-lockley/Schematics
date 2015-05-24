package za.co.no9.draw

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class BoxShapeTest extends FlatSpec with PropertyChecks with Matchers {
	val theta = 0.001

	"Given a default layout state and a box with fixed width and height, no rotation and scale of 1" should "layout the box at absolute(0, 0)" in {
		val boxShape = new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))), width = 100, height = 20)
		val ls = new LayoutState(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		val layedOutShape = boxShape.layout(initialLayedOutShape, ls)

		layedOutShape.boundingRectangle.topLeft should equal(Point(0, -10))
		layedOutShape.boundingRectangle.bottomRight should equal(Point(100, 10))

		layedOutShape.grips.centre should equal(Point(50, 0))
		layedOutShape.grips.north should equal(Point(50, -10))
		layedOutShape.grips.south should equal(Point(50, 10))
		layedOutShape.grips.west should equal(Point(0, 0))
		layedOutShape.grips.east should equal(Point(100, 0))

		layedOutShape.grips.nw should equal(Point(0, -10))
		layedOutShape.grips.ne should equal(Point(100, -10))
		layedOutShape.grips.sw should equal(Point(0, 10))
		layedOutShape.grips.se should equal(Point(100, 10))
	}

	"Given a default layout state and a box with fixed width and height, 45 degree rotation around absolute (0,0) and a scale of 2" should "layout the box at absolute (50, 50)" in {
		val boxShape = new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(50, 50), Point(0, 0))), width = 100, height = 20)
		val ls = new LayoutState(rotation = 45.0, scale = 2.0, translation = Point(0, 0))

		val layedOutShape = boxShape.layout(initialLayedOutShape, ls)

		assert(pointEquals(layedOutShape.boundingRectangle.topLeft, Point(-14.1421 + 50.0, -14.1421 + 50.0), theta))
		assert(pointEquals(layedOutShape.boundingRectangle.bottomRight, Point(155.5634 + 50.0, 155.5634 + 50.0), theta))
	}

	"Given a box and a second box which I layout in terms of the first box" should "layout the boxes correctly" in {
		val boxA = new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(50, 50), Point(0, 0))), width = 100, height = 20)
		val boxB = new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(previous.last(0).get.grips.east, Point(20, 0))), width = 100, height = 20)

		val ls = new LayoutState(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		val boxALayedOut = boxA.layout(initialLayedOutShape, ls)
		val boxBLayedOut = boxB.layout(boxALayedOut, ls)

		assert(rectangleEquals(boxALayedOut.boundingRectangle, BoundedRectangle(Point(50.0, 40.0), Point(150.0, 60.0)), theta))
		assert(rectangleEquals(boxBLayedOut.boundingRectangle, BoundedRectangle(Point(170.0, 40.0), Point(270.0, 60.0)), theta))
	}
}
