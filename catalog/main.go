package main

import (
	"encoding/json"
	"log"
	"net/http"

	"github.com/gorilla/mux"
)

// Product represents a product object
type Product struct {
	ItemID string  `json:"itemId,omitempty"`
	Name   string  `json:"name,omitempty"`
	Desc   string  `json:"desc,omitempty"`
	Price  float32 `json:"price,omitempty"`
}

var product []Product

const listenaddress string = ":8080"

// GetAllProducts returns all products
func GetAllProducts(w http.ResponseWriter, r *http.Request) {
    w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(product)
}

// GetProduct returns a single product from the inventory db as JSON
func GetProduct(w http.ResponseWriter, r *http.Request) {
	params := mux.Vars(r)
	for _, item := range product {
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
	product = append(product, Product{ItemID: "165613", Name: "Solid Performance Polo", Desc: "Moisture-wicking, antimicrobial 100% polyester design wicks for life of garment. No-curl, rib-knit collar...", Price: 17.8})
	product = append(product, Product{ItemID: "165614", Name: "Ogio Caliber Polo", Desc: "Moisture-wicking 100% polyester. Rib-knit collar and cuffs; Ogio jacquard tape insitem_ide neck; bar-tacked three-button placket with...", Price: 28.75})
	product = append(product, Product{ItemID: "165954", Name: "16 oz. Vortex Tumbler", Desc: "Double-wall insulated, BPA-free, acrylic cup. Push-on litem_id with thumb-slitem_ide closure; for hot and cold beverages. Holds 16 oz. Hand wash only. Imprint. Clear.", Price: 6.0})
	product = append(product, Product{ItemID: "329199", Name: "Forge Laptop Sticker", Desc: "JBoss Community Forge Project Sticker", Price: 8.5})
	product = append(product, Product{ItemID: "329299", Name: "Red Fedora", Desc: "Official Red Hat Fedora", Price: 34.99})
	product = append(product, Product{ItemID: "444434", Name: "Pebble Smart Watch", Desc: "Smart glasses and smart watches are perhaps two of the most exciting developments in recent years. ", Price: 24.0})
	product = append(product, Product{ItemID: "444435", Name: "Oculus Rift", Desc: "The world of gaming has also undergone some very unique and compelling tech advances in recent years. Virtual reality...", Price: 106.0})
	product = append(product, Product{ItemID: "444436", Name: "Lytro Camera", Desc: "Consumers who want to up their photography game are looking at newfangled cameras like the Lytro Field camera, designed to ...", Price: 44.3})

	router := mux.NewRouter()
	router.HandleFunc("/api/products", GetAllProducts).Methods("GET")
	router.HandleFunc("/api/product/{id}", GetProduct).Methods("GET")
	router.Use(loggingMiddleware)

	log.Println("[INFO] - Starting server on " + listenaddress)

	log.Fatal(http.ListenAndServe(listenaddress, router))
}
