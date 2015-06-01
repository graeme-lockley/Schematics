package za.co.no9.draw

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class BlockShapeTest extends FlatSpec with PropertyChecks with Matchers {
	val theta = 0.001

	"Given a block with no nested shape" should "still be able to layout without errors" in {
		val shape = new BlockShape(layoutPoint = _ => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))))
		val ls = new TX(rotation = 30.0, scale = 2.0, translation = Point(0, 0))

		val layedOutShape = shape.layout(initialLayedOutShape, ls)

		assert(pointEquals(layedOutShape.realBoundedRectangle.topLeft, Point(0.0, 0.0), theta))
		assert(pointEquals(layedOutShape.realBoundedRectangle.bottomRight, Point(0.0, 0.0), theta))
	}

	"Given a nested shape" should "translate the layout correctly" in {
		val shape = new BlockShape(
			List(
				new BoxShape(layoutPoint = _ => LayoutPoint(West(), At(Point(50, 30), Point(0, 0))), width = 100, height = 20),
				new BoxShape(layoutPoint = p => LayoutPoint(West(), At(p.last(0).get.grips.east, Point(20, 0))), width = 100, height = 20)),
			layoutPoint = (previous: LaidOutShape) => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))))
		val ls = new TX(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		val layedOutShape = shape.layout(initialLayedOutShape, ls)

		layedOutShape.realBoundedRectangle.topLeft.x should equal(50.0)
		layedOutShape.realBoundedRectangle.topLeft.y should equal(20.0)
		layedOutShape.realBoundedRectangle.bottomRight.x should equal(270.0)
		layedOutShape.realBoundedRectangle.bottomRight.y should equal(40.0)
	}

	"Given a nested shape with a relative position for the bounding rectangle with a 30 degree rotation around absolute (0,0) and a scale of 2" should "translate the layout correctly" in {
		val shape = new BlockShape(
			List(
				new BoxShape(layoutPoint = _ => LayoutPoint(West(), At(Point(50, 30), Point(0, 0))), width = 100, height = 20, name = "boxA"),
				new BoxShape(layoutPoint = p => LayoutPoint(West(), At(p.last(0).get.grips.east, Point(20, 0)))
					, width = 100, height = 20, name = "boxB")),
			layoutPoint = (previous: LaidOutShape) => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))),
			name = "block")
		val ls = new TX(rotation = 30.0, scale = 2.0, translation = Point(0, 0))

		val layedOutShape = shape.layout(initialLayedOutShape, ls)

		assert(pointEquals(layedOutShape.realBoundedRectangle.topLeft, Point(50.0 - 2.0 * 10.0 * Math.sin(Math.toRadians(30.0)), 30.0 - 2.0 * 10.0 * Math.cos(Math.toRadians(30.0))), theta))
		assert(pointEquals(layedOutShape.nestedShapes(0).grips.nw, Point(50.0 + 2.0 * 10.0 * Math.sin(Math.toRadians(30.0)), 30.0 - 2.0 * 10.0 * Math.cos(Math.toRadians(30.0))), theta))
		assert(pointEquals(layedOutShape.nestedShapes(1).grips.nw, Point(50.0 + 2.0 * 10.0 * Math.sin(Math.toRadians(30.0)) + 2.0 * (100.0 + 20.0) * Math.cos(Math.toRadians(30.0)), 30.0 - 2.0 * 10.0 * Math.cos(Math.toRadians(30.0)) + 2.0 * (100.0 + 20.0) * Math.sin(Math.toRadians(30))), theta))
		assert(pointEquals(layedOutShape.realBoundedRectangle.bottomRight, Point(50.0 + 20.0 * Math.sin(Math.toRadians(30.0)) + 440.0 * Math.cos(Math.toRadians(30.0)), 30.0 + 20.0 * Math.cos(Math.toRadians(30.0)) + 440.0 * Math.sin(Math.toRadians(30.0))), theta))
	}
}
