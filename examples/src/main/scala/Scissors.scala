case object Scissors extends RPSMove {
  override def compareTo(move: RPSMove): Int = {
    move match {
      case Paper => 1
      case Scissors => 0
      case Rock => -1
    }
  }
}