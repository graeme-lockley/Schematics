package za.co.no9.draw.examples

import org.scalatest.FlatSpec
import za.co.no9.draw._

class LinesTest extends FlatSpec {
	val home: LaidOutShape => LayoutPoint = p => LayoutPoint(NorthWest(), At(Point(0, 0), Point(0, 0)))

	def south(steps: Int = 0): LaidOutShape => LayoutPoint = p => LayoutPoint(North(), At(p.last(0).get.grips.south, Point(0, steps)))

	def west(steps: Int = 0): LaidOutShape => LayoutPoint = p => LayoutPoint(West(), At(p.last(0).get.grips.east, Point(steps, 0)))

	"Given 2 boxes connected with a line" should "render the image" in {
		Image(List(
			new BoxShape(List(), home, width = 50, height = 40),
			new BoxShape(List(), west(30), width = 50, height = 40),
			new LineShape(p => List(At(p.last(1).get.grips.east, Point(0, 0)), At(p.last(0).get.grips.west, Point(0, 0))))
		)).draw("target/boxes-with-lines.png")
	}
}
