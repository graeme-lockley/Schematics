package za.co.no9.draw

case class Image(nestedShapes: List[Shape]) {
	def draw(fileName: String): Unit = {
		val shape = new BlockShape(nestedShapes, layoutPoint = (previous: LaidOutShape) => LayoutPoint(NorthWest(), At(Point(0, 0), Point(0, 0))), name = "top")
		val tx = new TX(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		val laidOutImage = shape.layout(initialLayedOutShape, tx)
		val canvas = new BufferedImage(laidOutImage.realBoundedRectangle)

		laidOutImage.render(canvas)

		canvas.write("PNG", fileName)
	}

	def initialLayedOutShape: LaidOutShape = new LaidOutShape with UsableLaidOutShape {
		override def realBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

		override def relativeBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

		override def grips: Grips = new RectangleGrips(Point(0, 0), Point(0, 0))

		override def render(canvas: Canvas): Unit = ()

		override def nestedShapes: List[LaidOutShape] = List()

		override val name: String = "_image"
		override val previous: Option[LaidOutShape] = Option.empty
	}
}
