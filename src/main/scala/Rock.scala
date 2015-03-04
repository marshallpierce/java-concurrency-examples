case object Rock extends RPSMove {
  override def compareTo(move: RPSMove): Int = {
    move match {
      case Paper => -1
      case Scissors => 1
      case Rock => 0
    }
  }
}