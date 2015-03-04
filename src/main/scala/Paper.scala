case object Paper extends RPSMove {
  override def compareTo(move: RPSMove): Int = {
    move match {
      case Paper => 0
      case Scissors => -1
      case Rock => 1
    }
  }
}