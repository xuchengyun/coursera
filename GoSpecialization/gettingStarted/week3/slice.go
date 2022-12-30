package main

import (
	"bufio"
	"fmt"
	"os"
	"sort"
	"strconv"
)

func main() {
	var intSlice = make([]int, 0, 3)

	scanner := bufio.NewScanner(os.Stdin)
	fmt.Println("Enter Integer:")
	for scanner.Scan() {
		var input string = scanner.Text()
		if input == "X" {
			os.Exit(0)
		}
		intVal, _ := strconv.Atoi(input)
		intSlice = append(intSlice, intVal)
		sort.Sort(sort.IntSlice(intSlice))
		fmt.Println(intSlice)
	}
}
