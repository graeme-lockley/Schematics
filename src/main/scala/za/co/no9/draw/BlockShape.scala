package za.co.no9.draw

import scala.collection.mutable

class BlockShape(val nestedShapes: List[Shape] = List(), val layoutPoint: LayedOutShape => LayoutPoint, val rotation: Double = 0.0, val text: Option[Text] = None, val lineStyle: Option[LineStyle] = None, val fillStyle: Option[FillStyle] = None, val name: String = "_") extends Shape {
	override def layout(previous: LayedOutShape, layoutState: LayoutState): LayedOutShape = {
		val ls = layoutState.absoluteTranslate(Point(0, 0)).absoluteRotation(0.0)

		val calculatedLayout = layoutShapeContent(previous, ls)
		val calculatedGrips = calculatedLayout.grips

		val atPosition: LayoutPoint = layoutPoint(previous)
		val atLayoutState: LayoutState = layoutState.absoluteTranslate(atPosition.at.position).translate(atPosition.at.relative).rotate(rotation)

		val targetLayoutState: LayoutState = atPosition.grip.translate(atLayoutState, calculatedGrips)

		layoutShapeContent(previous, targetLayoutState, calculatedGrips.boundedRectangle)
	}

	protected def layoutShapeContent(previous: LayedOutShape, ls: LayoutState, normalisedBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))): LayedOutShape = {
		val nestedLayedOutShapes = mutable.MutableList[LayedOutShape]()

		var previousLayoutOutShape = initialLayoutOutShape

		nestedShapes foreach { shape =>
			val nextLayoutOutShape = shape.layout(previousLayoutOutShape, ls)
			nestedLayedOutShapes += nextLayoutOutShape
			previousLayoutOutShape = nextLayoutOutShape
		}

		val layedOutShapeGrips = shapeGrips(nestedLayedOutShapes.toList, ls)

		val shapePrevious = previous
		val shapeName = name
		val shapeNormalisedBoundedRectangle = normalisedBoundedRectangle

		new LayedOutShape() {
			override def boundingRectangle: Rectangle = grips.boundedRectangle

			val normalisedBoundedRectangle: Rectangle = shapeNormalisedBoundedRectangle

			override def grips: Grips = layedOutShapeGrips

			override def render(canvas: Canvas): Unit = {
				draw(canvas, ls, normalisedBoundedRectangle)

				nestedLayedOutShapes foreach {
					_.render(canvas)
				}
			}

			override def nestedShapes: List[LayedOutShape] = nestedLayedOutShapes.toList

			override val name = shapeName

			override val previous: Option[LayedOutShape] = Option(shapePrevious)
		}
	}

	def shapeGrips(nestedLayedOutShapes: List[LayedOutShape], ls: LayoutState): Grips = {
		if (nestedShapes.isEmpty)
			initialLayoutOutShape.grips
		else
			nestedLayedOutShapes.tail.foldLeft(nestedLayedOutShapes.head.boundingRectangle)((br, layedOutShape) => br.union(layedOutShape.boundingRectangle)).grips
	}

	def draw(canvas: Canvas, ls: LayoutState, boundingRectangle: Rectangle): Unit = {
		canvas.setTransform(ls.tx)

		drawBackground(canvas, ls, boundingRectangle)
		drawForeground(canvas, ls, boundingRectangle)
	}

	def drawBackground(canvas: Canvas, ls: LayoutState, boundingRectangle: Rectangle): Unit = {
		canvas.drawRectangle(boundingRectangle, lineStyle, fillStyle)
	}

	def drawForeground(canvas: Canvas, ls: LayoutState, boundingRectangle: Rectangle): Unit = {
		if (text.isDefined) {
			canvas.drawText(text.get, boundingRectangle)
		}
	}

	protected def initialLayoutOutShape: LayedOutShape = {
		new LayedOutShape {
			override def boundingRectangle: Rectangle = PointRectangle(Point(0, 0))

			override def normalisedBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

			override def grips: Grips = new RectangleGrips(Point(0, 0), Point(0, 0))

			override def render(canvas: Canvas): Unit = ()

			override def nestedShapes: List[LayedOutShape] = List()

			override val name: String = "_"

			override val previous: Option[LayedOutShape] = Option.empty
		}
	}
}
