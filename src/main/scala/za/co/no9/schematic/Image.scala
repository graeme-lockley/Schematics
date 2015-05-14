package za.co.no9.schematic

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Image extends Operations {
	val blockShape = new BlockShape(blockStyles = new Styles(), translate = location => location.add(-location.nw.x, -location.nw.y))

	override protected def addShape(shape: Shape): Shape = blockShape.addShape(shape)

	override protected def styles(): Styles = blockShape.styles()

	def draw(): Unit = {
		val width = 600
		val height = 400

		val layouts = collection.mutable.MutableList[ShapeLayout]()
		val schematicRuntime = new SchematicRuntime(styles())
		schematicRuntime.pushLayouts(layouts)

		val shapeLayout = blockShape.layout(schematicRuntime)

		schematicRuntime.popLayouts()

		val renderWidth = 2 + shapeLayout.location.ne.x - shapeLayout.location.sw.x
		val renderHeight = 2 + shapeLayout.location.sw.y - shapeLayout.location.ne.y
		println(renderWidth)
		println(renderHeight)
		val renderBI = new BufferedImage(renderWidth.toInt, renderHeight.toInt, BufferedImage.TYPE_INT_ARGB)
		//		val renderBI = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
		val renderGraphics = renderBI.createGraphics()
		renderGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
		println(shapeLayout.location.nw)

		//		schematicRuntime.style.origin = Position(-shapeLayout.location.sw.x, -shapeLayout.location.sw.y)
		//		renderGraphics.translate(schematicRuntime.style.origin.x, schematicRuntime.style.origin.y)

		shapeLayout.render(renderGraphics)

		ImageIO.write(renderBI, "PNG", new File("bob.png"))
	}
}
