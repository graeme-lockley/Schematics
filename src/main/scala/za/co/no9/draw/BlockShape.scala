package za.co.no9.draw

import scala.collection.mutable

class BlockShape(val nestedShapes: List[Shape] = List(), val layoutPoint: LaidOutShape => LayoutPoint, val rotation: Double = 0.0, val text: Option[Text] = None, val lineStyle: Option[LineStyle] = None, val fillStyle: Option[FillStyle] = None, val name: String = "_block") extends Shape {
	override def layout(previous: LaidOutShape, layoutState: TX): LaidOutShape = {
		val ls: TX = layoutState.absoluteTranslate(Point(0, 0)).absoluteRotation(0.0)

		val calculatedLayout = layoutShapeContent(previous, ls)
		val calculatedGrips = calculatedLayout.grips

		val atPosition: LayoutPoint = layoutPoint(previous)
		val atLayoutState: TX = layoutState.absoluteTranslate(atPosition.at.position).translate(atPosition.at.relative).rotate(rotation)

		val targetLayoutState: TX = atPosition.grip.translate(atLayoutState, calculatedGrips)

		val shadowShape: LaidOutShape = blockShadow(previous, targetLayoutState, calculatedGrips.realBoundedRectangle)
		layoutShapeContent(shadowShape, targetLayoutState, calculatedGrips.realBoundedRectangle)
	}

	protected def blockShadow(previousLayedOutShape: LaidOutShape, ls: TX, shadowBoundedRectangle: Rectangle): LaidOutShape = {
		new LaidOutShape with UsableLaidOutShape {
			override def realBoundedRectangle: Rectangle = grips.realBoundedRectangle

			override def grips: Grips = ls.transform(shadowBoundedRectangle.grips)

			override def render(canvas: Canvas): Unit = ???

			override def nestedShapes: List[LaidOutShape] = ???

			override def relativeBoundedRectangle: Rectangle = shadowBoundedRectangle

			override val name: String = "_" + BlockShape.this.name
			override val previous: Option[LaidOutShape] = Some(previousLayedOutShape)
		}
	}

	protected def layoutShapeContent(previous: LaidOutShape, ls: TX, normalisedBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))): LaidOutShape = {
		val nestedLayedOutShapes = mutable.MutableList[LaidOutShape]()

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

		new LaidOutShape with IgnoredLaidOutShape {
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

			override def nestedShapes: List[LaidOutShape] = nestedLayedOutShapes.toList

			override val previous: Option[LaidOutShape] = Option(shapePrevious)
		}
	}

	def shapeGrips(nestedLayedOutShapes: List[LaidOutShape], ls: TX): Grips = {
		if (nestedShapes.isEmpty)
			initialLayoutOutShape.grips
		else
			nestedLayedOutShapes.tail.foldLeft(nestedLayedOutShapes.head.realBoundedRectangle)((br, layedOutShape) => br.union(layedOutShape.realBoundedRectangle)).grips
	}

	def draw(canvas: Canvas, ls: TX, boundingRectangle: Rectangle): Unit = {
		canvas.setTransform(ls.tx)

		drawBackground(canvas, ls, boundingRectangle)
		drawForeground(canvas, ls, boundingRectangle)
	}

	def drawBackground(canvas: Canvas, ls: TX, boundingRectangle: Rectangle): Unit = {
		canvas.drawRectangle(boundingRectangle, lineStyle, fillStyle)
	}

	def drawForeground(canvas: Canvas, ls: TX, boundingRectangle: Rectangle): Unit = {
		if (text.isDefined) {
			canvas.drawText(text.get, boundingRectangle)
		}
	}

	protected def initialLayoutOutShape: LaidOutShape = {
		new LaidOutShape with UsableLaidOutShape {
			override def realBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

			override def relativeBoundedRectangle: Rectangle = PointRectangle(Point(0, 0))

			override def grips: Grips = new RectangleGrips(Point(0, 0), Point(0, 0))

			override def render(canvas: Canvas): Unit = ()

			override def nestedShapes: List[LaidOutShape] = List()

			override val name: String = "initialBlock"

			override val previous: Option[LaidOutShape] = Option.empty
		}
	}
}
