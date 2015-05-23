package za.co.no9.schematic

object Tester {
	def main(args: Array[String]) {
		val i = new Image()

		i.up() // 0
		i.box(title = "A\nB", width = 100.0) // 1
		i.right() // 2
		i.arrow() // 3

		val b = i.block() // 4
		// b.point // 4.0
		b.up() // 4.1
		b.box(title = "B", width = 100.0) // 4.2
		b.arrow() // 4.3
		b.box(title = "C", width = 100.0) // 4.4

		i.right() // 5
		i.arrow() // 6
		i.box(title = "D") // 7

		i.draw()
	}

	def originalMain(args: Array[String]) {
		val i = new Image()
		i.up()
		i.box(title = "gap")
		i.right()
		i.box(title = "Hello", width = 150.0)
		i.arrow()

		val b = i.block()
		b.box("in", width = 75.0)
		b.arrow()
		b.up()
		b.box("the", width = 150.0)
		b.arrow()
		b.box("box", width = 75.0)

		i.right()
		i.arrow()
		i.box("World")
		i.up()
		i.arrow()
		i.box("Bye bye love")

		i.draw()
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
