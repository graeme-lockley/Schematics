package za.co.no9.draw.examples

import org.scalatest.FlatSpec
import za.co.no9.draw._

class BoxesTest extends FlatSpec {
	val home: LaidOutShape => LayoutPoint = p => LayoutPoint(NorthWest(), At(Point(0, 0), Point(0, 0)))

	def south(steps: Int = 0): LaidOutShape => LayoutPoint = p => LayoutPoint(North(), At(p.last(0).get.grips.south, Point(0, steps)))

	def west(steps: Int = 0): LaidOutShape => LayoutPoint = p => LayoutPoint(West(), At(p.last(0).get.grips.east, Point(steps, 0)))

	"Given a description of layered boxes with centered titles within each box and subtitles rotated left" should "render into layered-boxes-with-subtitles" in {

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

	"Given a box with a subtitle and boxes within" should "render into layered-boxes-with-subtitles-and-enclosed-boxes" in {
		Image(List(
			new BoxShape(List(
				new BoxShape(List(), p => LayoutPoint(North(), At(p.last(0).get.grips.west, Point(5, 0))), width = 30, height = 9, rotation = 270, lineStyle = None, text = Some(Text("Subtitle"))),
				new BoxShape(List(), p => LayoutPoint(West(), At(p.last(1).get.grips.west, Point(40, 0))), width = 50, height = 34, text = Some(Text("System A"))),
				new BoxShape(List(), west(10), width = 50, height = 34, text = Some(Text("System B"))),
				new BoxShape(List(), west(10), width = 50, height = 34, text = Some(Text("System C")))
			), home, width = 550, height = 40),
			new BoxShape(List(
				new BoxShape(List(), p => LayoutPoint(North(), At(p.last(0).get.grips.west, Point(5, 0))), width = 30, height = 9, rotation = 270, lineStyle = None, text = Some(Text("Subtitle"))),
				new BoxShape(List(), p => LayoutPoint(West(), At(p.last(1).get.grips.west, Point(40, 0))), width = 170, height = 34, text = Some(Text("System A")))
			), south(10), width = 550, height = 40),
			new BoxShape(List(
				new BoxShape(List(), p => LayoutPoint(North(), At(p.last(0).get.grips.west, Point(5, 0))), width = 30, height = 9, rotation = 270, lineStyle = None, text = Some(Text("Subtitle"))),
				new BlockShape(List(
					new BoxShape(List(), p => LayoutPoint(West(), At(p.last(0).get.grips.west, Point(0, 0))), width = 50, height = 34, text = Some(Text("System A"))),
					new BoxShape(List(), west(10), width = 50, height = 34, text = Some(Text("System B"))),
					new BoxShape(List(), west(10), width = 50, height = 34, text = Some(Text("System C")))
				), p => LayoutPoint(Centre(), At(p.last(1).get.grips.centre, Point(0, 0))))
			), south(10), width = 550, height = 40)
		)).draw("target/render into layered-boxes-with-subtitles-and-enclosed-boxes.png")
	}

	def lastList(p: LaidOutShape): List[String] =
		p.last(1) match {
			case None => Nil
			case Some(pp) => p.name :: lastList(pp)
		}
}
