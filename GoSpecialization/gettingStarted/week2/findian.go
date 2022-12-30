package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

// Hello returns a greeting for the named person.
func main() {
	fmt.Println("Enter a string:")
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	s := scanner.Text()
	sUpper := strings.ToUpper(s)
	if strings.HasPrefix(sUpper, strings.ToUpper("i")) &&
		strings.HasSuffix(sUpper, strings.ToUpper("n")) &&
		strings.Contains(sUpper, strings.ToUpper("a")) {
		fmt.Println("Found!")
	} else {
		fmt.Println("Not Found!")
	}
}
