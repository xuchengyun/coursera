package main

import "fmt"

// Hello returns a greeting for the named person.
func trunc() {
	fmt.Println("Enter float number:")
	var number float64
	fmt.Scanln(&number)
	fmt.Println(int64(number))
}
