package za.co.no9.draw

object Tester {
	def main(args: Array[String]) {
		Image(
			List(
				new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(Point(0, 0), Point(0, 0))), width = 100, height = 20),
				new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(previous.last(0).get.grips.east, Point(0, 0))), width = 20, height = 5),
				new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(West(), At(previous.last(0).get.grips.east, Point(0, 0))), width = 100, height = 20),
				new BoxShape(List(), layoutPoint = (previous: LayedOutShape) => LayoutPoint(North(), At(previous.last(1).get.grips.east, Point(2, 0))), width = 96, height = 15, rotation = 275, text = Some(Text("Hello World", fontStyle = Italic()))))
		).draw("bob.png")


		//		layoutPoint = (previous: LayedOutShape) => LayoutPoint(NorthWest(), At(Point(0, 0), Point(0, 0))))


		//		val i = new Image()
		//
		//
		//
		//		i.up() // 0
		//		i.box(title = "A\nB", width = 100.0) // 1
		//		i.right() // 2
		//		i.arrow() // 3
		//
		//		val b = i.block() // 4
		//		// b.point // 4.0
		//		b.up() // 4.1
		//		b.box(title = "B", width = 100.0) // 4.2
		//		b.arrow() // 4.3
		//		b.box(title = "C", width = 100.0) // 4.4
		//
		//		i.right() // 5
		//		i.arrow() // 6
		//		i.box(title = "D") // 7
		//
		//		i.draw()
	}

	/*
		box "Text within body" width=500 height=30 [
			box "Small text" fontsize=5 rotate=270
		];
		down;
		box "Text within body" width=500 height=30 [
			box "Small text" fontsize=5 rotate=270
		];
		down;
		box "Text within body" width=500 height=30 [
			box "Small text" fontsize=5 rotate=270
		];

		val i = new Image()
		i.box(title = "Text within body", width=500, height=30 elements=(b:Box) => List(b.box(text = "Small text", fontsize = 9, rotate=270)))
		i.down()
		i.box(title = "Text within body", width=500, height=30 elements=(b:Box) => List(b.box(text = "Small text", fontsize = 9, rotate=270)))
		i.down()
		i.box(title = "Text within body", width=500, height=30 elements=(b:Box) => List(b.box(text = "Small text", fontsize = 9, rotate=270)))

		i.draw()
	 */
}
