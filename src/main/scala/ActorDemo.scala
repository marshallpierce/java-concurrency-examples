
object ActorDemo {

  def main(args: Array[String]) {
    val player1 = new RockPaperScissorsPlayer("player 1", None)
    val player2 = new RockPaperScissorsPlayer("player 2", Some(player1))

    player1.start
    player2.start

    player1 ! Paper
    player2 ! Rock
  }

}