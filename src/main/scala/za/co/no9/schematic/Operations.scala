package za.co.no9.schematic

trait Operations {
	protected def addShape(shape: Shape): Shape

	protected def styles(): Styles

	def arrow(): ArrowShape = {
		val arrow = new ArrowShape()
		addShape(arrow)
		arrow
	}

	def box(title: String = null, width: Double = styles().boxWidth, height: Double = styles().boxHeight, textHeight: Double = styles().textHeight): BoxShape = {
		val box = new BoxShape(title, width, height, textHeight)
		addShape(box)
		box
	}

	def block(): BlockShape = {
		val blockShape = new BlockShape(styles())
		addShape(blockShape)
		blockShape.addShape(new PointShape())
		blockShape
	}

	def up(): DirectionShape = {
		val directionShape = new DirectionShape(Up())
		addShape(directionShape)
		directionShape
	}

	def down(): DirectionShape = {
		val directionShape = new DirectionShape(Down())
		addShape(directionShape)
		directionShape
	}

	def left(): DirectionShape = {
		val directionShape = new DirectionShape(Left())
		addShape(directionShape)
		directionShape
	}

	def right(): DirectionShape = {
		val directionShape = new DirectionShape(Right())
		addShape(directionShape)
		directionShape
	}
}
