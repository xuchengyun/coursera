// You can edit this code!
// Click here and start typing.
package main

import (
	"bufio"
	"fmt"
	"io/ioutil"
	"os"
	"strings"
)

type name struct {
	fname string
	lname string
}

func main5() {
	fmt.Println("Enter file name:")
	var fileName string
	fmt.Scanln(&fileName)
	readFile, err := os.Open(fileName)
	if err != nil {
		fmt.Println(err)
	}
	fileScanner := bufio.NewScanner(readFile)
	fileScanner.Split(bufio.ScanLines)
	var fileNames []name

	for fileScanner.Scan() {
		fileNames = append(fileNames, name{strings.Split(fileScanner.Text(), " ")[0], strings.Split(fileScanner.Text(), " ")[1]})
	}
	readFile.Close()
	file, err := ioutil.ReadFile(fileName)
	fmt.Println(file, err)
	for _, n := range fileNames {
		fmt.Println("LastName: " + n.lname + " FirstName: " + n.fname)
	}
}
