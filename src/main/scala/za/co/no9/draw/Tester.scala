package za.co.no9.draw

object Tester {
	def main(args: Array[String]) {
		Image(
			List(
				new BoxShape(layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))), width = 100, height = 20),
				new BoxShape(layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(previous.last(0).get.grips.east, Point(0, 0))), width = 20, height = 5),
				new BoxShape(layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(previous.last(0).get.grips.east, Point(0, 0))), width = 100, height = 20, lineStyle = Some(LineStyle(width = 0.2f, colour = RED)), text = Some(Text("Cheerio", TextStyle(name = "Arial", size = 10, style = Plain(), colour = GREEN))), fillStyle = Some(GradientFillStyle(BLUE, WHITE))),
				new BoxShape(layoutPoint = (previous: LayedOutShape) => LayoutPoint(North(), At(previous.last(1).get.grips.east, Point(2, 0))), width = 96, height = 15, rotation = 290, text = Some(Text("Hello World", TextStyle(size = 6, style = Bold())))))
		).draw("bob.png")
	}
}
