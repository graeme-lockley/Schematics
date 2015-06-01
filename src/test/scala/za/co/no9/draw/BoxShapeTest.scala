package za.co.no9.draw

import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class BoxShapeTest extends FlatSpec with PropertyChecks with Matchers {
	val theta = 0.001

	"Given a default layout state and a box with fixed width and height, no rotation and scale of 1" should "layout the box at absolute(0, 0)" in {
		val boxShape = new BoxShape(List(), layoutPoint = (previous: LaidOutShape) => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))), width = 100, height = 20)
		val ls = new LayoutState(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		val layedOutShape = boxShape.layout(initialLayedOutShape, ls)

		layedOutShape.realBoundedRectangle.topLeft should equal(Point(0, -10))
		layedOutShape.realBoundedRectangle.bottomRight should equal(Point(100, 10))

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
		val boxShape = new BoxShape(layoutPoint = (previous: LaidOutShape) => LayoutPoint(West(), At(Point(50, 50), Point(0, 0))), width = 100, height = 20)
		val ls = new LayoutState(rotation = 45.0, scale = 2.0, translation = Point(0, 0))

		val layedOutShape = boxShape.layout(initialLayedOutShape, ls)

		assert(pointEquals(layedOutShape.realBoundedRectangle.topLeft, Point(50.0 - 20.0 * Math.sin(Math.toRadians(45.0)), 50.0 - 20.0 * Math.cos(Math.toRadians(45.0))), theta))
		assert(pointEquals(layedOutShape.realBoundedRectangle.bottomRight, Point(155.5634 + 50.0, 155.5634 + 50.0), theta))
	}

	"Given a box and a second box which I layout in terms of the first box" should "layout the boxes correctly" in {
		val boxA = new BoxShape(List(), layoutPoint = (previous: LaidOutShape) => LayoutPoint(West(), At(Point(50, 50), Point(0, 0))), width = 100, height = 20)
		val boxB = new BoxShape(List(), layoutPoint = (previous: LaidOutShape) => LayoutPoint(West(), At(previous.last(0).get.grips.east, Point(20, 0))), width = 100, height = 20)

		val ls = new LayoutState(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		val boxALayedOut = boxA.layout(initialLayedOutShape, ls)
		val boxBLayedOut = boxB.layout(boxALayedOut, ls)

		assert(rectangleEquals(boxALayedOut.realBoundedRectangle, BoundedRectangle(Point(50.0, 40.0), Point(150.0, 60.0)), theta))
		assert(rectangleEquals(boxBLayedOut.realBoundedRectangle, BoundedRectangle(Point(170.0, 40.0), Point(270.0, 60.0)), theta))
	}

	"Given a box within a block and a second box within a block where the blocks are layed out relative to each other" should "the boxes be correctly layed out" in {
		val innerBlockShapeA = new BlockShape(
			List(new BoxShape(layoutPoint = p => LayoutPoint(North(), At(p.grips.north, Point(0, 0))), width = 10, height = 10, name = "boxA")),
			layoutPoint = p => LayoutPoint(North(), At(p.grips.north, Point(0, 0))),
			name = "innerBlockShapeA"
		)
		val innerBlockShapeB = new BlockShape(
			List(new BoxShape(layoutPoint = p => LayoutPoint(North(), At(p.grips.north, Point(0, 0))), width = 10, height = 10, name = "boxB")),
			layoutPoint = p => LayoutPoint(North(), At(p.grips.south, Point(0, 10))),
			name = "innerBlockShapeB"
		)
		val outerBlock = new BlockShape(List(innerBlockShapeA, innerBlockShapeB), layoutPoint = p => LayoutPoint(North(), At(Point(0, 0), Point(0, 0))), name = "outerBlock")
		val ls = new LayoutState(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		val layedOutShape = outerBlock.layout(initialLayedOutShape, ls)

		assert(rectangleEquals(layedOutShape.nestedShapes(0).realBoundedRectangle, BoundedRectangle(Point(-5, 0), Point(5, 10)), theta))
		assert(rectangleEquals(layedOutShape.nestedShapes(1).realBoundedRectangle, BoundedRectangle(Point(-5, 20), Point(5, 30)), theta))
	}

	"Given blocks with boxes that have been layed out the previous" should "reference the correct shape irrespective of the nesting" in {
		val innerBlockShapeA = new BlockShape(
			List(
				new BoxShape(
					List(
						new BoxShape(layoutPoint = p => LayoutPoint(North(), At(p.grips.north, Point(0, 0))), width = 10, height = 10, name = "boxA.1.1"),
						new BoxShape(layoutPoint = p => LayoutPoint(North(), At(p.grips.north, Point(0, 0))), width = 10, height = 10, name = "boxA.1.2")),
					layoutPoint = p => LayoutPoint(North(), At(p.grips.north, Point(0, 0))), width = 10, height = 10, name = "boxA.1"),
				new BoxShape(layoutPoint = p => LayoutPoint(North(), At(p.grips.north, Point(0, 0))), width = 10, height = 10, name = "boxA.2")),
			layoutPoint = p => LayoutPoint(North(), At(p.grips.north, Point(0, 0))),
			name = "innerBlockShapeA"
		)
		val innerBlockShapeB = new BlockShape(
			List(
				new BoxShape(layoutPoint = p => LayoutPoint(North(), At(p.grips.north, Point(0, 0))), width = 10, height = 10, name = "boxB.1"),
				new BoxShape(layoutPoint = p => LayoutPoint(North(), At(p.grips.north, Point(0, 0))), width = 10, height = 10, name = "boxB.2")),
			layoutPoint = p => LayoutPoint(North(), At(p.grips.south, Point(0, 10))),
			name = "innerBlockShapeB"
		)
		val outerBlock = new BlockShape(List(innerBlockShapeA, innerBlockShapeB), layoutPoint = p => LayoutPoint(North(), At(Point(0, 0), Point(0, 0))), name = "outerBlock")
		val ls = new LayoutState(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		val layedOutShape = outerBlock.layout(initialLayedOutShape, ls)

		assertLayoutShape(layedOutShape, 2, List("outerBlock", "_outerBlock", "_test"), List("_outerBlock", "_test"))
		assertLayoutShape(layedOutShape.nestedShapes(0), 2, List("innerBlockShapeA", "_innerBlockShapeA", "_outerBlock", "_test"), List("_innerBlockShapeA", "_outerBlock", "_test"))
		assertLayoutShape(layedOutShape.nestedShapes(0).nestedShapes(0), 2, List("boxA.1", "_boxA.1", "_innerBlockShapeA", "_outerBlock", "_test"), List("_boxA.1", "_innerBlockShapeA", "_outerBlock", "_test"))
		assertLayoutShape(layedOutShape.nestedShapes(0).nestedShapes(0).nestedShapes(0), 0, List("boxA.1.1", "_boxA.1.1", "_boxA.1", "_innerBlockShapeA", "_outerBlock", "_test"), List("_boxA.1.1", "_boxA.1", "_innerBlockShapeA", "_outerBlock", "_test"))
		assertLayoutShape(layedOutShape.nestedShapes(0).nestedShapes(0).nestedShapes(1), 0, List("boxA.1.2", "_boxA.1.2", "boxA.1.1", "_boxA.1.1", "_boxA.1", "_innerBlockShapeA", "_outerBlock", "_test"), List("_boxA.1.2", "_boxA.1.1", "_boxA.1", "_innerBlockShapeA", "_outerBlock", "_test"))
		assertLayoutShape(layedOutShape.nestedShapes(0).nestedShapes(1), 0, List("boxA.2", "_boxA.2", "boxA.1", "_boxA.1", "_innerBlockShapeA", "_outerBlock", "_test"), List("_boxA.2", "_boxA.1", "_innerBlockShapeA", "_outerBlock", "_test"))
		assertLayoutShape(layedOutShape.nestedShapes(1), 2, List("innerBlockShapeB", "_innerBlockShapeB", "innerBlockShapeA", "_innerBlockShapeA", "_outerBlock", "_test"), List("_innerBlockShapeB", "_innerBlockShapeA", "_outerBlock", "_test"))
		assertLayoutShape(layedOutShape.nestedShapes(1).nestedShapes(0), 0, List("boxB.1", "_boxB.1", "_innerBlockShapeB", "innerBlockShapeA", "_innerBlockShapeA", "_outerBlock", "_test"), List("_boxB.1", "_innerBlockShapeB", "_innerBlockShapeA", "_outerBlock", "_test"))
		assertLayoutShape(layedOutShape.nestedShapes(1).nestedShapes(1), 0, List("boxB.2", "_boxB.2", "boxB.1", "_boxB.1", "_innerBlockShapeB", "innerBlockShapeA", "_innerBlockShapeA", "_outerBlock", "_test"), List("_boxB.2", "_boxB.1", "_innerBlockShapeB", "_innerBlockShapeA", "_outerBlock", "_test"))
	}

	def assertLayoutShape(layedOutShape: LaidOutShape, nestedShapeLength: Int, previousNames: List[String], lastNames: List[String]): Unit = {
		assert(layedOutShape.nestedShapes.length === nestedShapeLength)
		assertPreviousNames(layedOutShape, previousNames)
		assertLastNames(layedOutShape, lastNames)
	}

	def assertPreviousNames(layedOutShape: LaidOutShape, names: List[String]): Unit = names match {
		case Nil => ()
		case n :: Nil =>
			assert(layedOutShape.name === n)
		case n :: ns =>
			assert(layedOutShape.name === n)
			assertPreviousNames(layedOutShape.previous.get, ns)
	}

	def assertLastNames(layedOutShape: LaidOutShape, names: List[String]) = {
		def assertName(n: Int, names: List[String]): Unit = names match {
			case Nil =>
				assert(layedOutShape.last(n).isEmpty)
			case x :: xs =>
				assert(layedOutShape.last(n).get.name === x)
				assertName(n + 1, xs)
		}
		assertName(0, names)
	}
}
