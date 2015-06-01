package za.co.no9.draw

import scala.collection.mutable

class BlockShape(val nestedShapes: List[Shape] = List(), val layoutPoint: LayedOutShape => LayoutPoint, val rotation: Double = 0.0, val text: Option[Text] = None, val lineStyle: Option[LineStyle] = None, val fillStyle: Option[FillStyle] = None, val name: String = "_block") extends Shape {
	override def layout(previous: LayedOutShape, layoutState: LayoutState): LayedOutShape = {
		val ls: LayoutState = layoutState.absoluteTranslate(Point(0, 0)).absoluteRotation(0.0)

		val calculatedLayout = layoutShapeContent(previous, ls)
		val calculatedGrips = calculatedLayout.grips

		val atPosition: LayoutPoint = layoutPoint(previous)
		val atLayoutState: LayoutState = layoutState.absoluteTranslate(atPosition.at.position).translate(atPosition.at.relative).rotate(rotation)

		val targetLayoutState: LayoutState = atPosition.grip.translate(atLayoutState, calculatedGrips)

		val shadowShape: LayedOutShape = blockShadow(previous, targetLayoutState, calculatedGrips.realBoundedRectangle)
		layoutShapeContent(shadowShape, targetLayoutState, calculatedGrips.realBoundedRectangle)
	}

	protected def blockShadow(previousLayedOutShape: LayedOutShape, ls: LayoutState, shadowBoundedRectangle: Rectangle): LayedOutShape = {
		new LayedOutShape with UsableLayedOutShape {
			override def __ls = ls

			override def realBoundedRectangle: Rectangle = grips.realBoundedRectangle

			override def grips: Grips = ls.transform(shadowBoundedRectangle.grips)

			override def render(canvas: Canvas): Unit = ???

			override def nestedShapes: List[LayedOutShape] = ???

			override def relativeBoundedRectangle: Rectangle = shadowBoundedRectangle

			override val name: String = "_" + BlockShape.this.name
			override val previous: Option[LayedOutShape] = Some(previousLayedOutShape)
		}
	}

	protected def layoutShapeContent(previous: LayedOutShape, ls: LayoutState, normalisedBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))): LayedOutShape = {
		val nestedLayedOutShapes = mutable.MutableList[LayedOutShape]()

		var previousLayoutOutShape = previous

		nestedShapes foreach { shape =>
			val nextLayoutOutShape = shape.layout(previousLayoutOutShape, ls)
			nestedLayedOutShapes += nextLayoutOutShape
			previousLayoutOutShape = nextLayoutOutShape
		}

		val layedOutShapeGrips = shapeGrips(nestedLayedOutShapes.toList, ls)

		val shapePrevious = previous
		val shapeName = name
		val shapeNormalisedBoundedRectangle = normalisedBoundedRectangle

		new LayedOutShape with IgnoredLayedOutShape {
			override def __ls = ls

			override def realBoundedRectangle: Rectangle = grips.realBoundedRectangle

			val relativeBoundedRectangle: Rectangle = shapeNormalisedBoundedRectangle

			override def grips: Grips = layedOutShapeGrips

			override def render(canvas: Canvas): Unit = {
				draw(canvas, ls, relativeBoundedRectangle)

				nestedLayedOutShapes foreach {
					_.render(canvas)
				}
			}

			override val name: String = shapeName

			override def nestedShapes: List[LayedOutShape] = nestedLayedOutShapes.toList

			override val previous: Option[LayedOutShape] = Option(shapePrevious)
		}
	}

	def shapeGrips(nestedLayedOutShapes: List[LayedOutShape], ls: LayoutState): Grips = {
		if (nestedShapes.isEmpty)
			initialLayoutOutShape.grips
		else
			nestedLayedOutShapes.tail.foldLeft(nestedLayedOutShapes.head.realBoundedRectangle)((br, layedOutShape) => br.union(layedOutShape.realBoundedRectangle)).grips
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
		new LayedOutShape with UsableLayedOutShape {
			override def __ls = new LayoutState(rotation = 0.0, scale = 1.0, translation = Point(0, 0))

			override def realBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

			override def relativeBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

			override def grips: Grips = new RectangleGrips(Point(0, 0), Point(0, 0))

			override def render(canvas: Canvas): Unit = ()

			override def nestedShapes: List[LayedOutShape] = List()

			override val name: String = "initialBlock"

			override val previous: Option[LayedOutShape] = Option.empty
		}
	}
}
