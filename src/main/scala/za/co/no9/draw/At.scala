package za.co.no9.draw

case class At(position: Point, relative: Point)

case class LayoutPoint(grip: CompassDirection, at: At)

