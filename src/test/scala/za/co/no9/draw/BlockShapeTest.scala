package za.co.no9.draw

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class BlockShapeTest extends FlatSpec with PropertyChecks with Matchers {
	val theta = 0.001

	"Given a block with no nested shape" should "still be able to layout without errors" in {
		val shape = new BlockShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))))
		val ls = new LayoutState(rotation = 30.0, scale = 2.0, translation = Point(0, 0))

		val layedOutShape = shape.layout(initialLayedOutShape, ls)

		assert(pointEquals(layedOutShape.boundingRectangle.topLeft, Point(0.0, 0.0), theta))
		assert(pointEquals(layedOutShape.boundingRectangle.bottomRight, Point(0.0, 0.0), theta))
	}

	"Given a nested shape" should "translate the layout correctly" in {
		val shape = new BlockShape(
			List(
				new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(50, 30), Point(0, 0))), width = 100, height = 20),
				new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(previous.last(0).get.grips.east, Point(20, 0))), width = 100, height = 20)),
			layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))))
		val ls = new LayoutState(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		val layedOutShape = shape.layout(initialLayedOutShape, ls)

		layedOutShape.boundingRectangle.topLeft.x should equal(50.0)
		layedOutShape.boundingRectangle.topLeft.y should equal(20.0)
		layedOutShape.boundingRectangle.bottomRight.x should equal(270.0)
		layedOutShape.boundingRectangle.bottomRight.y should equal(40.0)
	}

	"Given a nested shape with a relative position for the bounding rectangle with a 30 degree rotation around absolute (0,0) and a scale of 2" should "translate the layout correctly" in {
		val shape = new BlockShape(
			List(
				new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(50, 30), Point(0, 0))), width = 100, height = 20),
				new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(previous.last(0).get.grips.east, Point(20, 0))), width = 100, height = 20)),
			layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))))
		val ls = new LayoutState(rotation = 30.0, scale = 2.0, translation = Point(0, 0))

		val layedOutShape = shape.layout(initialLayedOutShape, ls)

		assert(pointEquals(layedOutShape.boundingRectangle.topLeft, Point(40.0, 12.6795), theta))
		assert(pointEquals(layedOutShape.boundingRectangle.bottomRight, Point(50.0 + 20.0 * Math.sin(Math.toRadians(30.0)) + 440.0 * Math.cos(Math.toRadians(30.0)), 30.0 + 20.0 * Math.cos(Math.toRadians(30.0)) + 440.0 * Math.sin(Math.toRadians(30.0))), theta))
	}

	"Given a block with nested blocks" should "be able to locate and orientate based on last names" in {
		val shape = new BlockShape(
			List(
				new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(NorthWest(), At(previous.last("top").get.grips.nw, Point(0, 0))), width = 100, height = 20, name = "boxA"),
				new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(previous.last("boxA").get.grips.east, Point(20, 0))), width = 100, height = 20, name = "boxB")),
			layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))),
			name = "top")
		val ls = new LayoutState(rotation = 30.0, scale = 2.0, translation = Point(0, 0))
		shape.layout(initialLayedOutShape, ls)
	}
}
