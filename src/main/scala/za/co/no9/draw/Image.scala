package za.co.no9.draw

case class Image(nestedShapes: List[Shape]) {
	def draw(fileName: String): Unit = {
		val shape = new BlockShape(nestedShapes, layoutPoint = (previous: LayedOutShape) => LayoutPoint(NorthWest(), At(Point(0, 0), Point(0, 0))), name = "top")
		val ls = new LayoutState(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

		val layedOutShape = shape.layout(initialLayedOutShape, ls)
		val shapeBoundedRectangle = layedOutShape.boundingRectangle

		val canvas = new BufferedImage(shapeBoundedRectangle)

		layedOutShape.render(canvas)

		canvas.write("PNG", fileName)
	}

	def initialLayedOutShape: LayedOutShape = new LayedOutShape {
		override def boundingRectangle: Rectangle = PointRectangle(Point(0, 0))

		override def normalisedBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

		override def grips: Grips = new RectangleGrips(Point(0, 0), Point(0, 0))

		override def render(canvas: Canvas): Unit = ()

		override def nestedShapes: List[LayedOutShape] = List()

		override val name: String = "_"
		override val previous: Option[LayedOutShape] = Option.empty
	}
}
