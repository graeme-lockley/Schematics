package za.co.no9.schematic

object Tester {
	def main(args: Array[String]) {
		val i = new Image()

		i.down()
		i.box(title = "B", width = 100.0)
		i.right()
		i.arrow()

		val b = i.block()
		b.down()
		b.box("C", width = 100.0)
		b.arrow()
		b.box("D", width = 100.0)

		i.right()
		i.arrow()
		i.box("E")

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
}
