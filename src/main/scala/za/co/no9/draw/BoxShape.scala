package za.co.no9.draw

class BoxShape(override val nestedShapes: List[Shape] = List(), override val layoutPoint: LaidOutShape => LayoutPoint, val width: Double, val height: Double, override val rotation: Double = 0.0, override val text: Option[Text] = None, override val lineStyle: Option[LineStyle] = Some(LineStyle(width = 1.0f, colour = Colour(0, 0, 0))), override val fillStyle: Option[FillStyle] = None, override val name: String = "_box") extends BlockShape(nestedShapes, layoutPoint, rotation, text, lineStyle, fillStyle, name) {
	override def shapeGrips(nestedLaidOutShapes: List[LaidOutShape], ls: TX): Grips = ls.transform(BoundedRectangle(Point(0, 0), Point(width, height)).grips)
}
