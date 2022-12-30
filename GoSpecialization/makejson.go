// You can edit this code!
// Click here and start typing.
package main

import (
	"encoding/json"
	"fmt"
)

func main_1() {
	fmt.Println("Enter name:")
	var name string
	fmt.Scanln(&name)
	fmt.Println("Enter address:")
	var address string
	fmt.Scanln(&address)

	m := make(map[string]string, 1)
	m["name"] = name
	m["address"] = address
	json, _ := json.Marshal(m)
	fmt.Println(string(json))
}
