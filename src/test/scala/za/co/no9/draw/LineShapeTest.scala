package za.co.no9.draw

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class LineShapeTest extends FlatSpec with PropertyChecks with Matchers {
	val theta = 0.001

	"Given a line between absolute points" should "correctly calculate the absolute points" in {
		val line = new LineShape(_ => List(At(Point(0, 0), Point(0, 0)), At(Point(100, 0), Point(0, 0))))
		val ls = new TX(rotation = 30.0, scale = 2.0, translation = Point(0, 0))

		val layedOutShape: LineLaidOutShape = line.layout(initialLayedOutShape, ls).asInstanceOf[LineLaidOutShape]

		layedOutShape.realBoundedRectangle.topLeft should equal(Point(0.0, 0.0))
		layedOutShape.realBoundedRectangle.bottomRight should equal(Point(100.0, 0.0))

		layedOutShape.absolutePoints.length should equal(2)
		layedOutShape.absolutePoints(0) should equal(Point(0.0, 0.0))
		layedOutShape.absolutePoints(1) should equal(Point(100.0, 0.0))
	}

	"Given a line between absolute points with a relative offset and scale" should "correctly calculate the absolute points" in {
		val line = new LineShape(_ => List(At(Point(0, 0), Point(10, 0)), At(Point(100, 0), Point(15, 5))))
		val ls = new TX(rotation = 0.0, scale = 2.0, translation = Point(0, 0))

		val layedOutShape: LineLaidOutShape = line.layout(initialLayedOutShape, ls).asInstanceOf[LineLaidOutShape]

		layedOutShape.realBoundedRectangle.topLeft should equal(Point(20.0, 0.0))
		layedOutShape.realBoundedRectangle.bottomRight should equal(Point(130.0, 10.0))

		layedOutShape.absolutePoints.length should equal(2)
		layedOutShape.absolutePoints(0) should equal(Point(20.0, 0.0))
		layedOutShape.absolutePoints(1) should equal(Point(130.0, 10.0))
	}

	"Given a line between absolute points with a relative offset, scale and rotation" should "correctly calculate the absolute points" in {
		val line = new LineShape(_ => List(At(Point(0, 0), Point(10, 0)), At(Point(100, 0), Point(15, 5))))
		val ls = new TX(rotation = 90.0, scale = 2.0, translation = Point(0, 0))

		val layedOutShape: LineLaidOutShape = line.layout(initialLayedOutShape, ls).asInstanceOf[LineLaidOutShape]

		layedOutShape.realBoundedRectangle.topLeft should equal(Point(0.0, 20.0))
		layedOutShape.realBoundedRectangle.bottomRight should equal(Point(90.0, 30.0))

		layedOutShape.absolutePoints.length should equal(2)
		layedOutShape.absolutePoints(0) should equal(Point(0.0, 20.0))
		layedOutShape.absolutePoints(1) should equal(Point(90.0, 30.0))
	}
}
