package za.co.no9.draw

class BoxShape(override val nestedShapes: List[Shape], override val layoutPoint: LayedOutShape => LayoutPoint, val width: Double, val height: Double) extends BlockShape(nestedShapes, layoutPoint) {
	override def shapeGrips(nestedLayedOutShapes: List[LayedOutShape], ls: LayoutState): DiscreteGrips = {
		DiscreteGrips(
			ls.transform(Point(0, 0)), ls.transform(Point(width / 2, 0)), ls.transform(Point(width, 0)),
			ls.transform(Point(0, height / 2)), ls.transform(Point(width / 2, height / 2)), ls.transform(Point(width, height / 2)),
			ls.transform(Point(0, height)), ls.transform(Point(width / 2, height)), ls.transform(Point(width, height)))
	}
}
