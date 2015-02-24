package com.example

import scala.annotation.tailrec
import scala.io.BufferedSource

object StrongComponents extends App {

  type Graph = Map[Int, List[Int]]
  type Edge = (Int, Int)

  abstract class Node

  case object VisitedNode extends Node

  case object NonVisitedNode extends Node

  def concatUnsortedLists[A](a: List[A], b: List[A]): List[A] = {
    a.foldRight(b)((x, y) => x :: y)
  }

  def dfs(graph: Graph, nodes: Map[Int, Node], leader: Int): (Int, Map[Int, Node], List[Int]) = {
    @tailrec def dfs(nodesToVisit: List[Int], nodes: Map[Int, Node], size: Int, newOrder: List[Int]): (Int, Map[Int, Node], List[Int]) = {
      nodesToVisit match {
        case Nil => (size, nodes, newOrder)
        case currentNode :: tail =>
          nodes(currentNode) match {
            case VisitedNode => dfs(tail, nodes, size, newOrder)
            case NonVisitedNode => {
              val nodesToVisit = graph.get(currentNode) match {
                case Some(adjacentNodes) => concatUnsortedLists(adjacentNodes, tail)
                case None => tail
              }
              dfs(nodesToVisit, nodes.updated(currentNode, VisitedNode), size + 1, currentNode :: newOrder)
            }
          }
      }
    }
    dfs(leader :: Nil, nodes, 0, Nil)
  }

  def buildOrderResult(newOrder: List[List[Int]]): Stream[Int] = {
    def buildSubgroupOrderResult(newOrder: List[Int]): Stream[Int] = {
      if (newOrder.isEmpty) return Stream.empty
      newOrder.head #:: buildSubgroupOrderResult(newOrder.tail)
    }
    newOrder match {
      case Nil => Stream.empty
      case head :: tail => buildSubgroupOrderResult(head.reverse) append buildOrderResult(tail)
    }
  }

  def dfsloop(graph: Graph, nodeCount: Int, nodes: Map[Int, Node], order: Stream[Int]): (Seq[(Int, Int)], Stream[Int]) = {
    @tailrec def dfsloop(nodes: Map[Int, Node], order: Stream[Int], acc: Seq[(Int, Int)], newOrder: List[List[Int]]): (Seq[(Int, Int)], Stream[Int]) = {
      order match {
        case Stream.Empty => (acc, buildOrderResult(newOrder))
        case 0 #:: tail => (acc, buildOrderResult(newOrder))
        case nodeId #:: tail =>
          nodes(nodeId) match {
            case VisitedNode => dfsloop(nodes, tail, acc, newOrder)
            case NonVisitedNode => {
              val (size, updatedNodes, moreNewOrder) = dfs(graph, nodes, nodeId)
              dfsloop(updatedNodes, tail, (nodeId, size) +: acc, moreNewOrder :: newOrder)
            }
          }
      }
    }
    val result = dfsloop(nodes, order, Nil, Nil)
    (result._1.sortBy({ case (leader, size) => size })(Ordering[Int].reverse), result._2)
  }

  def sccs(edges: Stream[Edge], nodeCount: Int) = {
    val reversedGraph = toAdjacencyList(reverse(edges))
    val (_, order) = dfsloop(reversedGraph, nodeCount, Map.empty.withDefault { x => NonVisitedNode }, Stream.from(nodeCount, -1))
    val (leaders, _) = dfsloop(toAdjacencyList(edges), nodeCount, Map.empty.withDefault { x => NonVisitedNode }, order)
    leaders.take(5).foreach { case (leader, size) => println("leader " + leader, " size " + size) }
  }

  def reverse(edges: Stream[Edge]): Stream[Edge] = {
    edges.map({ case (x, y) => (y, x) })
  }

  def toAdjacencyList(edges: Stream[Edge]): Graph = {
    @tailrec def toAdjacencyList(edges: Iterable[Edge], adjacencyList: Graph): Graph = {
      edges match {
        case Stream.Empty => adjacencyList
        case edge #:: list => toAdjacencyList(list,
          adjacencyList.updated(edge._1, adjacencyList.get(edge._1) match {
            case Some(nodes) => edge._2 :: nodes
            case None => edge._2 :: Nil
          }))
      }
    }
    toAdjacencyList(edges, Map.empty)
  }

  def buildEdges(lines: Iterator[String]): Stream[Edge] = {
    if (!lines.hasNext) {
      Stream.empty
    } else {
      val nodes = lines.next().split(' ')
      (nodes(0).toInt, nodes(1).toInt) #:: buildEdges(lines)
    }
  }

  val source: BufferedSource = scala.io.Source.fromFile(args(0))
  sccs(buildEdges(source.getLines()), args(1).toInt)
  source.close()
}
