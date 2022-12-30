package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strings"
)

type Person struct {
	fname string
	lname string
}

func main() {
	var fileName string
	var people []Person = make([]Person, 0)

	fmt.Print("Enter file name: ")
	fmt.Scan(&fileName)

	fd, err := os.Open(fileName)

	if err != nil {
		log.Fatal(err)
	}

	defer fd.Close()

	scanner := bufio.NewScanner(fd)
	for scanner.Scan() {
		line := strings.Fields(scanner.Text())
		person := Person{
			fname: line[0],
			lname: line[1],
		}
		people = append(people, person)
	}

	fmt.Println(people)
}
