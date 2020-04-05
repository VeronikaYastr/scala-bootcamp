package com.evolutiongaming.bootcamp.homework.akka

import akka.actor.{Actor, ActorRef, Props}
import com.evolutiongaming.bootcamp.homework.akka.BinaryTreeSet.Operation

object BinaryTreeNode {

  sealed trait Position

  case object Left extends Position

  case object Right extends Position

  def props(elem: Int, initiallyRemoved: Boolean): Props = Props(classOf[BinaryTreeNode], elem, initiallyRemoved)
}

class BinaryTreeNode(val elem: Int, initiallyRemoved: Boolean) extends Actor {

  import BinaryTreeNode._
  import BinaryTreeSet.Operation._
  import BinaryTreeSet.OperationReply._

  var subtrees = Map[Position, ActorRef]()
  var removed = initiallyRemoved

  def insertRightOrLeft(m: Insert): Unit = {
    if (m.elem == elem) {
      removed = false
      m.requester ! OperationFinished(m.id)
    } else {
      sendMessageToSubtree(m)
    }
  }

  def isExistedElement(m: Contains): Unit = {
    var result = true
    if (m.elem == elem) {
      result = !removed
      m.requester ! ContainsResult(m.id, result)
    } else {
      sendMessageToSubtree(m)
    }
  }

  def removeElement(m: Remove): Unit = {
    if (m.elem == elem) {
      removed = true
      m.requester ! OperationFinished(m.id)
    } else {
      sendMessageToSubtree(m)
    }
  }

  def definePosition(baseElem: Int): Position = if (baseElem < elem) Left else Right

  def sendMessageToSubtree(m: Operation): Unit = {
    val position: Position = definePosition(m.elem)
    subtrees.get(position) match {
      case Some(node) => node ! m
      case None =>
        m match {
          case Insert(requester, id, elem) =>
            subtrees += (position -> context.actorOf(BinaryTreeNode.props(elem, initiallyRemoved = false)))
            requester ! OperationFinished(id)
          case Contains(requester, id, _) =>  requester ! ContainsResult(id, result = false)
          case Remove(requester, id, _) => requester ! OperationFinished(id)
          case _ => println("Unexpected message.")
        }
    }
  }

  def receive: Receive = {
    case m: Insert => insertRightOrLeft(m)
    case m: Contains => isExistedElement(m)
    case m: Remove => removeElement(m)
    case _ => println("Unexpected message.")
  }
}

