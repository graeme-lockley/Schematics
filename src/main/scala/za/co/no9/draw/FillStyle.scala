package za.co.no9.draw

class FillStyle()

case class SolidFillStyle(colour: Colour) extends FillStyle()

case class GradientFillStyle(fromColour: Colour, toColour: Colour) extends FillStyle()