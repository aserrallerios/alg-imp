package main

import (
    "bufio"
    "os"
    "strings"
    "strconv"
    "fmt"
    "container/heap"
)

type Graph map[string][]Vertex
type Vertices map[string]*Vertex

// An Item is something we manage in a priority queue.
type Vertex struct {
    value    string // The value of the item; arbitrary.
    priority int    // The priority of the item in the queue.
    // The index is needed by update and is maintained by the heap.Interface methods.
    index int // The index of the item in the heap.
}

// A PriorityQueue implements heap.Interface and holds Items.
type PriorityQueue []*Vertex

func (pq PriorityQueue) Len() int { return len(pq) }

func (pq PriorityQueue) Less(i, j int) bool {
    return pq[i].priority < pq[j].priority
}

func (pq PriorityQueue) Swap(i, j int) {
    pq[i], pq[j] = pq[j], pq[i]
    pq[i].index = i
    pq[j].index = j
}

func (pq *PriorityQueue) Push(x interface{}) {
    n := len(*pq)
    item := x.(*Vertex)
    item.index = n
    *pq = append(*pq, item)
}

func (pq *PriorityQueue) Pop() interface{} {
    old := *pq
    n := len(old)
    item := old[n-1]
    item.index = -1 // for safety
    *pq = old[0 : n-1]
    return item
}

// update modifies the priority and value of an Item in the queue.
func (pq *PriorityQueue) update(item *Vertex, value string, priority int) {
    item.value = value
    item.priority = priority
    heap.Fix(pq, item.index)
}

func updateHeap(pq *PriorityQueue, distance int, source string, index *int, g Graph, v Vertices) {
    verticesToAdd := g[source]
    for _, vertex := range verticesToAdd {
        storedNode, present := v[vertex.value]
        if (!present) {
            storedNode = &Vertex{
                value:    vertex.value,
                priority: vertex.priority + distance,
                index:    *index,
            }
            *index++
            v[vertex.value] = storedNode
            heap.Push(pq, v[vertex.value])
        } else {
            if (storedNode.priority > vertex.priority + distance) {
                pq.update(storedNode, storedNode.value, vertex.priority + distance)
            }
        }
    }
}

func printMap(v Vertices) {
    for k, v := range v {
        fmt.Println(k, v)
    }
}

func shortestPath(source string, g Graph) {
    index := 0

    v := make(Vertices)
    v[source] = &Vertex{
        value:    source,
        priority: 0,
        index:    index,
    }
    index += 1

    // Create a priority queue, put the items in it, and
    // establish the priority queue (heap) invariants.
    pq := make(PriorityQueue, 0)
    heap.Init(&pq)
    heap.Push(&pq, v[source])

    results := make(map[string]int)
    for pq.Len() > 0 {
        //printMap(v)
        item := heap.Pop(&pq).(*Vertex)
        fmt.Printf("Sortest path for %s is %d\n", item.value, item.priority)
        results[item.value] = item.priority
        updateHeap(&pq, item.priority, item.value, &index, g, v)
    }
    // Starting from 1, distance to the nodes:
    // 10,30,50,80,90,110,130,160,180,190
    // 3205,2303,3152,982,2018,2317,1820,2403,3027,2596
}

func processVertex(line string, g Graph) {
    vertices := strings.Split(line, "\t")
    adjacentVertices := make([]Vertex, len(vertices) - 2)
    for i := 1; i < len(vertices); i++ {
        vertex := strings.Split(vertices[i], ",")
        if (len(vertex) < 2) {
            break
        }

        value := vertex[0]
        priority, err := strconv.Atoi(vertex[1])
        check(err)
        adjacentVertices[i - 1] = Vertex{value: value, priority: priority}
    }
    g[vertices[0]] = adjacentVertices
}

func check(e error) {
    if e != nil {
        panic(e)
    }
}

func main() {
    f, err := os.Open(os.Args[1])
    check(err)
    reader := bufio.NewReader(f)

    g := make(Graph)
    for {
        line, err := reader.ReadString('\n')
        if err != nil {
            break
        }
        processVertex(strings.Trim(line, "\n"), g)
    }
    shortestPath(os.Args[2], g)
    f.Close()
}
