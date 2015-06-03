package za.co.no9.draw.examples

import org.scalatest.FlatSpec
import za.co.no9.draw._

class BoxesTest extends FlatSpec {
	"Given a description of layered boxes with centered titles within each box and subtitles rotated left" should "render into layered-boxes-with-subtitles" in {
		val home: LaidOutShape => LayoutPoint = p => LayoutPoint(North(), At(Point(0, 0), Point(0, 0)))
		def south(steps: Int = 0): LaidOutShape => LayoutPoint = p => LayoutPoint(North(), At(p.last(0).get.grips.south, Point(0, steps)))

		val boxFillStyle = Some(GradientFillStyle(fromColour = Colour(195, 215, 232), toColour = Colour(255, 255, 255)))
		def box(names: List[String], layoutPoint: LaidOutShape => LayoutPoint, text: String) = {
			val firstLayout: LaidOutShape => LayoutPoint = p => LayoutPoint(North(), At(p.last(0).get.grips.west, Point(5, 0)))
			def nestedName(name: String, layout: LaidOutShape => LayoutPoint) = new BoxShape(layoutPoint = layout, height = 9, width = 30, rotation = 270, lineStyle = None, text = Some(Text(name, TextStyle(name = "Arial", size = 8, style = Bold()))))
			val nestedShapes: List[Shape] = if (names.isEmpty) List() else nestedName(names.head, firstLayout) :: names.tail.map(nestedName(_, south()))

			new BoxShape(nestedShapes, layoutPoint = layoutPoint, width = 550, height = 40, text = Some(Text(text, style = TextStyle(size = 11))), fillStyle = boxFillStyle)
		}

		Image(List(
			box(List("Multiple", "Line", "Subtitle"), home, "The top layer"),
			box(List("Subtitle"), south(10), "A description for the layer"),
			box(List("Subtitle"), south(10), "A description for the layer"),
			box(List("Subtitle"), south(10), "A description for the layer"),
			box(List("Double", "Subtitle"), south(10), "A description for the layer"),
			box(List("Double", "Subtitle"), south(10), "A description for the layer"),
			box(List("Double", "Subtitle"), south(10), "A description for the layer"))
		).draw("target/layered-boxes-with-subtitles.png")
	}
}
