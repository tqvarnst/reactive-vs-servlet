package main

import (
	"encoding/json"
	"log"
	"net/http"

	"github.com/gorilla/mux"
)

// Inventory represent an inventory item in the inventory db
type Inventory struct {
	ItemID   string `json:"itemId,omitempty"`
	Quantity int    `json:"quantity,omitempty"`
}

var inventory []Inventory

const listenaddress string = ":8080"

// GetAllInventory returns the complete inventory db as JSON
func GetAllInventory(w http.ResponseWriter, r *http.Request) {
    w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(inventory)
}

// GetInventory returns a single inventory from the inventory db as JSON
func GetInventory(w http.ResponseWriter, r *http.Request) {
	params := mux.Vars(r)
	for _, item := range inventory {
		if item.ItemID == params["id"] {
		    w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(item)
		}
	}
}

func loggingMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		log.Println("[INFO] - Received request: " + r.RequestURI)
		next.ServeHTTP(w, r)
	})
}

// our main function
func main() {
	inventory = append(inventory, Inventory{ItemID: "165613", Quantity: 303})
	inventory = append(inventory, Inventory{ItemID: "165614", Quantity: 54})
	inventory = append(inventory, Inventory{ItemID: "165954", Quantity: 407})
	inventory = append(inventory, Inventory{ItemID: "329199", Quantity: 123})
	inventory = append(inventory, Inventory{ItemID: "329299", Quantity: 78})
	inventory = append(inventory, Inventory{ItemID: "444434", Quantity: 343})
	inventory = append(inventory, Inventory{ItemID: "444435", Quantity: 85})
	inventory = append(inventory, Inventory{ItemID: "444436", Quantity: 245})

	router := mux.NewRouter()

	router.HandleFunc("/api/inventory", GetAllInventory).Methods("GET")
	router.HandleFunc("/api/inventory/{id}", GetInventory).Methods("GET")
	router.Use(loggingMiddleware)

	log.Println("[INFO] - Starting server on " + listenaddress)

	log.Fatal(http.ListenAndServe(listenaddress, router))
}
