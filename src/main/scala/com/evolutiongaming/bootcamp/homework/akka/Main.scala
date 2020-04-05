package com.evolutiongaming.bootcamp.homework.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object Main extends App {
  class MainActor extends Actor {
    val treeSetActorRef: ActorRef = treeActorSystem.actorOf(Props[BinaryTreeSet], "treeSet")

    treeSetActorRef ! BinaryTreeSet.Operation.Insert(treeSetActorRef, 1, 2)
    treeSetActorRef ! BinaryTreeSet.Operation.Insert(treeSetActorRef, 2, -1)
    treeSetActorRef ! BinaryTreeSet.Operation.Insert(treeSetActorRef, 3, 5)
    treeSetActorRef ! BinaryTreeSet.Operation.Insert(treeSetActorRef, 4, 7)
    treeSetActorRef ! BinaryTreeSet.Operation.Insert(treeSetActorRef, 5, 4)
    treeSetActorRef ! BinaryTreeSet.Operation.Insert(treeSetActorRef, 6, 3)
    treeSetActorRef ! BinaryTreeSet.Operation.Contains(treeSetActorRef, 7, 2)
    treeSetActorRef ! BinaryTreeSet.Operation.Contains(treeSetActorRef, 8, 5)
    treeSetActorRef ! BinaryTreeSet.Operation.Contains(treeSetActorRef, 9, 4)
    treeSetActorRef ! BinaryTreeSet.Operation.Contains(treeSetActorRef, 10, 7)
    treeSetActorRef ! BinaryTreeSet.Operation.Contains(treeSetActorRef, 11, -1)
    treeSetActorRef ! BinaryTreeSet.Operation.Remove(treeSetActorRef, 12, 2)
    treeSetActorRef ! BinaryTreeSet.Operation.Remove(treeSetActorRef, 13, 4)
    treeSetActorRef ! BinaryTreeSet.Operation.Contains(treeSetActorRef, 14, 2)
    treeSetActorRef ! BinaryTreeSet.Operation.Contains(treeSetActorRef, 15, 5)
    treeSetActorRef ! BinaryTreeSet.Operation.Contains(treeSetActorRef, 16, 4)
    treeSetActorRef ! BinaryTreeSet.Operation.Contains(treeSetActorRef, 17, 7)
    treeSetActorRef ! BinaryTreeSet.Operation.Contains(treeSetActorRef, 18, -1)

    override def receive: Receive = {
      case _ => println("Hey")
    }
  }

  val treeActorSystem: ActorSystem = ActorSystem("binary-tree-actor-system")
  val mainActorRef: ActorRef = treeActorSystem.actorOf(Props[MainActor], "main")
}
