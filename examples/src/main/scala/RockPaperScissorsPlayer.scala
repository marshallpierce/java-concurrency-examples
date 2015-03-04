import actors.Actor
import actors.OutputChannel
import util.Random



class RockPaperScissorsPlayer(name: String, other: Option[RockPaperScissorsPlayer]) extends Actor {

  def act() {
    other match {
      case None => 
      case Some(otherPlayer) => sendTo(otherPlayer, getRandomMove)
    }

    loop {
      react {
	  case move: RPSMove => respondToMove(move)
      }
    }
  }

  def log(str: String) {
    println(name + " " + str)
  }

  def respondToMove(theirMove: RPSMove) {
    val myMove = getRandomMove
    val result = myMove compareTo theirMove match {
      case 1 => "won"
      case 0 => "tied"
      case -1 => "lost"
    }

    log("rolled a " + myMove + ", received a " + theirMove + ": I " + result)
    Thread.sleep(1000)
    sendTo(sender, getRandomMove)
  }

  def sendTo(destination: OutputChannel[Any], move: RPSMove) {
    log("sending " + move)
    destination ! move
  }

  def getRandomMove(): RPSMove = {
    val moves: Array[RPSMove] = Array(Paper, Rock, Scissors)
    return moves(new Random().nextInt(moves.length))
  }
}